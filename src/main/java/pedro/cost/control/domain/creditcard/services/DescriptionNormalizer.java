package pedro.cost.control.domain.creditcard.services;

import pedro.cost.control.domain.creditcard.emuns.DescriptionMappingType;
import pedro.cost.control.domain.creditcard.entities.DescriptionMapping;

import java.text.Normalizer;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

public final class DescriptionNormalizer {

    public static final String FALLBACK_DESCRIPTION = "Outros";

    private static final int PATTERN_FLAGS = Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE;

    public record CompiledRule(Long id, int position, Pattern pattern, DescriptionMappingType type, String value) {
    }

    private final List<CompiledRule> rules;

    private DescriptionNormalizer(List<CompiledRule> rules) {
        this.rules = rules;
    }

    public static DescriptionNormalizer of(List<DescriptionMapping> mappings) {
        List<CompiledRule> compiled = mappings.stream()
                .sorted(Comparator.comparing(DescriptionMapping::getPosition))
                .map(m -> new CompiledRule(m.getId(), m.getPosition(), compile(m.getPattern()), m.getType(), m.getValue()))
                .toList();

        return new DescriptionNormalizer(compiled);
    }

    public static Pattern compile(String regex) {
        return Pattern.compile(stripAccents(regex), PATTERN_FLAGS);
    }

    public String normalize(String raw) {
        if (raw == null) return FALLBACK_DESCRIPTION;

        Optional<CompiledRule> match = findMatch(raw);

        if (match.isEmpty()) return FALLBACK_DESCRIPTION;

        CompiledRule rule = match.get();
        return rule.type() == DescriptionMappingType.IGNORE ? null : rule.value();
    }

    public Optional<CompiledRule> findMatch(String raw) {
        if (raw == null) return Optional.empty();

        String prepared = prepare(raw);

        for (CompiledRule rule : rules) {
            if (rule.pattern().matcher(prepared).find()) {
                return Optional.of(rule);
            }
        }

        return Optional.empty();
    }

    static String prepare(String raw) {
        return stripAccents(raw.trim())
                .replace(' ', ' ')
                .replaceAll("(?i)\\s*-\\s*parcela.*$", "")
                .trim();
    }

    private static String stripAccents(String value) {
        return Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }
}
