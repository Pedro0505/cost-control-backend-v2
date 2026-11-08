package pedro.cost.control.domain.creditcard.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pedro.cost.control.config.exceptions.BadRequestException;
import pedro.cost.control.config.exceptions.ConflictException;
import pedro.cost.control.config.exceptions.NotFoundException;
import pedro.cost.control.domain.creditcard.dtos.DescriptionMappingInputDTO;
import pedro.cost.control.domain.creditcard.dtos.DescriptionMappingOutputDTO;
import pedro.cost.control.domain.creditcard.dtos.DescriptionMappingPreviewOutputDTO;
import pedro.cost.control.domain.creditcard.dtos.DescriptionMappingReorderInputDTO;
import pedro.cost.control.domain.creditcard.emuns.DescriptionMappingType;
import pedro.cost.control.domain.creditcard.entities.DescriptionMapping;
import pedro.cost.control.domain.creditcard.repositories.DescriptionMappingRepository;
import pedro.cost.control.domain.user.entities.User;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class DescriptionMappingService {

    private static final int MAX_PATTERN_LENGTH = 500;
    private static final int MAX_VALUE_LENGTH = 255;

    private final DescriptionMappingRepository repository;

    public List<DescriptionMappingOutputDTO> findAll(Long userId) {
        return repository.findAllByUserIdOrdered(userId).stream()
                .map(DescriptionMappingOutputDTO::from)
                .toList();
    }

    public DescriptionMappingOutputDTO findById(Long id, Long userId) {
        return DescriptionMappingOutputDTO.from(findOwned(repository.findAllByUserIdOrdered(userId), id));
    }

    public DescriptionNormalizer buildNormalizer(Long userId) {
        return DescriptionNormalizer.of(repository.findAllByUserIdOrdered(userId));
    }

    public DescriptionMappingPreviewOutputDTO preview(String rawDescription, Long userId) {
        DescriptionNormalizer normalizer = buildNormalizer(userId);
        Optional<DescriptionNormalizer.CompiledRule> match = normalizer.findMatch(rawDescription);
        String normalized = normalizer.normalize(rawDescription);

        return new DescriptionMappingPreviewOutputDTO(
                rawDescription,
                normalized,
                match.map(r -> r.type() == DescriptionMappingType.IGNORE).orElse(false),
                match.map(DescriptionNormalizer.CompiledRule::id).orElse(null),
                match.map(DescriptionNormalizer.CompiledRule::position).orElse(null)
        );
    }

    @Transactional
    public DescriptionMappingOutputDTO create(DescriptionMappingInputDTO dto, User user) {
        DescriptionMappingType type = resolveType(dto.getType());
        String pattern = validatePattern(dto.getPattern());
        String value = validateValue(type, dto.getValue());

        List<DescriptionMapping> ordered = new ArrayList<>(repository.findAllByUserIdOrderedForUpdate(user.getId()));

        int position = dto.getPosition() == null ? ordered.size() + 1 : dto.getPosition();
        validatePosition(position, ordered.size() + 1);

        DescriptionMapping mapping = DescriptionMapping.builder()
                .pattern(pattern)
                .value(value)
                .type(type)
                .user(user)
                .build();

        ordered.add(position - 1, mapping);
        persistOrder(ordered);

        return DescriptionMappingOutputDTO.from(mapping);
    }

    @Transactional
    public DescriptionMappingOutputDTO update(Long id, DescriptionMappingInputDTO dto, Long userId) {
        List<DescriptionMapping> ordered = new ArrayList<>(repository.findAllByUserIdOrderedForUpdate(userId));
        DescriptionMapping mapping = findOwned(ordered, id);

        DescriptionMappingType type = resolveType(dto.getType());
        mapping.setPattern(validatePattern(dto.getPattern()));
        mapping.setType(type);
        mapping.setValue(validateValue(type, dto.getValue()));

        if (dto.getPosition() != null && !dto.getPosition().equals(mapping.getPosition())) {
            validatePosition(dto.getPosition(), ordered.size());
            ordered.remove(mapping);
            ordered.add(dto.getPosition() - 1, mapping);
            persistOrder(ordered);
        } else {
            repository.save(mapping);
        }

        return DescriptionMappingOutputDTO.from(mapping);
    }

    @Transactional
    public void delete(Long id, Long userId) {
        List<DescriptionMapping> ordered = new ArrayList<>(repository.findAllByUserIdOrderedForUpdate(userId));
        DescriptionMapping mapping = findOwned(ordered, id);

        repository.delete(mapping);
        repository.flush();

        ordered.remove(mapping);
        persistOrder(ordered);
    }

    @Transactional
    public List<DescriptionMappingOutputDTO> reorder(DescriptionMappingReorderInputDTO dto, Long userId) {
        if (dto == null || dto.ids() == null) {
            throw new BadRequestException("Informe a lista completa de ids na nova ordem");
        }

        List<DescriptionMapping> current = repository.findAllByUserIdOrderedForUpdate(userId);
        Map<Long, DescriptionMapping> byId = current.stream()
                .collect(Collectors.toMap(DescriptionMapping::getId, Function.identity()));

        Set<Long> seen = new HashSet<>();
        for (Long id : dto.ids()) {
            if (id == null || !seen.add(id)) {
                throw new BadRequestException("A lista de ids não pode ter valores nulos ou repetidos");
            }
        }

        if (!seen.equals(byId.keySet())) {
            throw new BadRequestException(
                    "A lista deve conter exatamente todos os mapeamentos do usuário (" + byId.size() + " ids)"
            );
        }

        List<DescriptionMapping> ordered = dto.ids().stream().map(byId::get).toList();
        persistOrder(ordered);

        return ordered.stream().map(DescriptionMappingOutputDTO::from).toList();
    }

    @Transactional
    public List<DescriptionMappingOutputDTO> loadDefaults(User user) {
        List<DescriptionMapping> current = repository.findAllByUserIdOrderedForUpdate(user.getId());

        if (!current.isEmpty()) {
            throw new ConflictException("O usuário já possui mapeamentos cadastrados");
        }

        List<DescriptionMapping> ordered = DefaultDescriptionMappings.ALL.stream()
                .map(d -> DescriptionMapping.builder()
                        .pattern(d.pattern())
                        .type(d.type())
                        .value(d.value())
                        .user(user)
                        .build())
                .toList();

        persistOrder(ordered);

        return ordered.stream().map(DescriptionMappingOutputDTO::from).toList();
    }


    private void persistOrder(List<DescriptionMapping> ordered) {
        for (int i = 0; i < ordered.size(); i++) {
            ordered.get(i).setPosition(-(i + 1));
        }
        repository.saveAllAndFlush(ordered);

        for (int i = 0; i < ordered.size(); i++) {
            ordered.get(i).setPosition(i + 1);
        }
        repository.saveAllAndFlush(ordered);
    }

    private DescriptionMapping findOwned(List<DescriptionMapping> mappings, Long id) {
        return mappings.stream()
                .filter(m -> m.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Mapeamento não encontrado"));
    }

    private DescriptionMappingType resolveType(DescriptionMappingType type) {
        return type == null ? DescriptionMappingType.CLASSIFY : type;
    }

    private String validatePattern(String pattern) {
        if (pattern == null || pattern.isBlank()) {
            throw new BadRequestException("A regex é obrigatória");
        }
        if (pattern.length() > MAX_PATTERN_LENGTH) {
            throw new BadRequestException("A regex deve ter no máximo " + MAX_PATTERN_LENGTH + " caracteres");
        }
        try {
            DescriptionNormalizer.compile(pattern);
        } catch (PatternSyntaxException e) {
            throw new BadRequestException("Regex inválida: " + e.getDescription());
        }
        return pattern;
    }

    private String validateValue(DescriptionMappingType type, String value) {
        if (type == DescriptionMappingType.IGNORE) {
            return null;
        }
        if (value == null || value.isBlank()) {
            throw new BadRequestException("O valor normalizado é obrigatório para mapeamentos do tipo CLASSIFY");
        }
        String trimmed = value.trim();
        if (trimmed.length() > MAX_VALUE_LENGTH) {
            throw new BadRequestException("O valor deve ter no máximo " + MAX_VALUE_LENGTH + " caracteres");
        }
        return trimmed;
    }

    private void validatePosition(int position, int maxAllowed) {
        if (position < 1 || position > maxAllowed) {
            throw new BadRequestException("Posição deve estar entre 1 e " + maxAllowed);
        }
    }
}
