package pedro.cost.control.domain.creditcard.services;

import org.springframework.stereotype.Component;
import pedro.cost.control.domain.creditcard.emuns.DescriptionNameEnum;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Component
public class CostFileClusterService {
    private static final Pattern ASSINATURAS_GOOGLE = Pattern.compile("(?i).*dl\\*google.*(youtub|google|one|play).*");

    private final LinkedHashMap<Pattern, String> fixedMappings = new LinkedHashMap<>();
    private final List<Pattern> excludedMappings = new ArrayList<>();

    public CostFileClusterService() {
        excludedMappings.add(Pattern.compile("(?i)\\bpagamento(s)?\\s*recebido\\b"));
        excludedMappings.add(Pattern.compile("(?i)\\bestorno\\b"));

        fixedMappings.put(Pattern.compile("(?i).*amazon\\s*prime.*"), DescriptionNameEnum.ASSINATURAS.getValue());
        fixedMappings.put(Pattern.compile("(?i).*prime\\s*aluguel.*"), DescriptionNameEnum.ASSINATURAS.getValue());
        fixedMappings.put(ASSINATURAS_GOOGLE, DescriptionNameEnum.ASSINATURAS.getValue());
        fixedMappings.put(Pattern.compile("(?i).*google\\s*(one|youtube|play).*"), DescriptionNameEnum.ASSINATURAS.getValue());
        fixedMappings.put(Pattern.compile("(?i).*hbomax.*"), DescriptionNameEnum.ASSINATURAS.getValue());
        fixedMappings.put(Pattern.compile("(?i).*microsoft.*(subscription|meses).*"), DescriptionNameEnum.ASSINATURAS.getValue());
        fixedMappings.put(Pattern.compile("(?i).*match\\s*fit.*"), DescriptionNameEnum.ASSINATURAS.getValue());
        fixedMappings.put(Pattern.compile("(?i).*ifood\\s*club.*"), DescriptionNameEnum.ASSINATURAS.getValue());
        fixedMappings.put(Pattern.compile("(?i)^.*\\*\\s*melimais.*"), DescriptionNameEnum.ASSINATURAS.getValue());
        fixedMappings.put(Pattern.compile("(?i).*vivo.*"), DescriptionNameEnum.ASSINATURAS.getValue());
        fixedMappings.put(Pattern.compile("(?i).*ebanx.*|.*xsolla.*"), DescriptionNameEnum.ASSINATURAS.getValue());
        fixedMappings.put(Pattern.compile("(?i).*crunchyroll.*"), DescriptionNameEnum.ASSINATURAS.getValue());
        fixedMappings.put(Pattern.compile("(?i)^dl\\s*\\*\\s*google\\s+chess\\b.*"), DescriptionNameEnum.ASSINATURAS.getValue());

        fixedMappings.put(Pattern.compile("(?i).*uber.*trip.*"), DescriptionNameEnum.TRANSPORTE.getValue());
        fixedMappings.put(Pattern.compile("(?i).*\\buber\\b.*"), DescriptionNameEnum.TRANSPORTE.getValue());
        fixedMappings.put(Pattern.compile("(?i).*99app.*"), DescriptionNameEnum.TRANSPORTE.getValue());
        fixedMappings.put(Pattern.compile("(?i).*\\bdl\\s*\\*\\s*99\\s+ride\\b.*"), DescriptionNameEnum.TRANSPORTE.getValue());
        fixedMappings.put(Pattern.compile("(?i).*uberrides.*"), DescriptionNameEnum.TRANSPORTE.getValue());

        fixedMappings.put(Pattern.compile("(?i)^ifd\\*.*"), DescriptionNameEnum.DELIVERY.getValue());
        fixedMappings.put(Pattern.compile("(?i).*ifood.*"), DescriptionNameEnum.DELIVERY.getValue());
        fixedMappings.put(Pattern.compile("(?i)^ifd\\s+\\*.*"), DescriptionNameEnum.DELIVERY.getValue());
        fixedMappings.put(Pattern.compile("(?i).*99food.*"), DescriptionNameEnum.DELIVERY.getValue());

        fixedMappings.put(Pattern.compile("(?i).*amazon.*"), DescriptionNameEnum.AMAZON.getValue());

        fixedMappings.put(Pattern.compile("(?i).*steam.*"), DescriptionNameEnum.STEAM.getValue());

        fixedMappings.put(Pattern.compile("(?i).*mercadolivre.*"), DescriptionNameEnum.MERCADOLIVRE.getValue());
        fixedMappings.put(Pattern.compile("(?i).*mercadopago.*"), DescriptionNameEnum.MERCADOLIVRE.getValue());
        fixedMappings.put(Pattern.compile("(?i)^mp"), DescriptionNameEnum.MERCADOLIVRE.getValue());

        fixedMappings.put(Pattern.compile("(?i).*padaria.*"), DescriptionNameEnum.ALIMENTACAO.getValue());

        fixedMappings.put(Pattern.compile("(?i).*shopee.*"), DescriptionNameEnum.COMPRAS_ONLINE.getValue());
        fixedMappings.put(Pattern.compile("(?i).*magazineluiza.*|.*magalu.*|.*magazinelu.*"), DescriptionNameEnum.COMPRAS_ONLINE.getValue());
        fixedMappings.put(Pattern.compile("(?i).*kabum.*"), DescriptionNameEnum.COMPRAS_ONLINE.getValue());
        fixedMappings.put(Pattern.compile("(?i).*nike.*"), DescriptionNameEnum.COMPRAS_ONLINE.getValue());
        fixedMappings.put(Pattern.compile("(?i).*centauro.*"), DescriptionNameEnum.COMPRAS_ONLINE.getValue());
        fixedMappings.put(Pattern.compile("(?i).*ferreira\\s*costa.*"), DescriptionNameEnum.COMPRAS_ONLINE.getValue());
        fixedMappings.put(Pattern.compile("(?i).*nuuvem.*"), DescriptionNameEnum.COMPRAS_ONLINE.getValue());

        fixedMappings.put(Pattern.compile("(?i).*farmaci.*|.*pague\\s*menos.*|.*raiadrogasil.*|.*pharma.*|.*fcia.*|.*raia\\sdrogasil.*"), DescriptionNameEnum.SAUDE.getValue());
    }

    public String normalizeDescription(String raw) {
        if (raw == null) return "Outros";

        String original = raw.trim();

        String normalized = Normalizer.normalize(original, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .replace('\u00A0', ' ')
                .replaceAll("(?i)\\s*-\\s*parcela.*$", "")
                .trim();

        if (isExcluded(normalized)) {
            return null;
        }

        for (Map.Entry<Pattern, String> entry : fixedMappings.entrySet()) {
            if (entry.getKey().matcher(normalized).find()) {
                return entry.getValue();
            }
        }

        return "Outros";
    }

    private boolean isExcluded(String normalized) {
        return excludedMappings.stream()
                .anyMatch(p -> p.matcher(normalized).find());
    }
}
