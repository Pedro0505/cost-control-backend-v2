package pedro.cost.control.domain.creditcard.services;

import org.springframework.stereotype.Component;

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

        fixedMappings.put(Pattern.compile("(?i).*amazon\\s*prime.*"), "Assinaturas");
        fixedMappings.put(Pattern.compile("(?i).*prime\\s*aluguel.*"), "Assinaturas");
        fixedMappings.put(ASSINATURAS_GOOGLE, "Assinaturas");
        fixedMappings.put(Pattern.compile("(?i).*google\\s*(one|youtube|play).*"), "Assinaturas");
        fixedMappings.put(Pattern.compile("(?i).*hbomax.*"), "Assinaturas");
        fixedMappings.put(Pattern.compile("(?i).*microsoft.*(subscription|meses).*"), "Assinaturas");
        fixedMappings.put(Pattern.compile("(?i).*match\\s*fit.*"), "Assinaturas");
        fixedMappings.put(Pattern.compile("(?i).*ifood\\s*club.*"), "Assinaturas");
        fixedMappings.put(Pattern.compile("(?i)^.*\\*\\s*melimais.*"), "Assinaturas");
        fixedMappings.put(Pattern.compile("(?i).*vivo.*"), "Assinaturas");
        fixedMappings.put(Pattern.compile("(?i).*ebanx.*|.*xsolla.*"), "Assinaturas");

        fixedMappings.put(Pattern.compile("(?i).*uber.*trip.*"), "Transporte");
        fixedMappings.put(Pattern.compile("(?i).*\\buber\\b.*"), "Transporte");
        fixedMappings.put(Pattern.compile("(?i).*99app.*"), "Transporte");

        fixedMappings.put(Pattern.compile("(?i)^ifd\\*.*"), "Ifood");
        fixedMappings.put(Pattern.compile("(?i).*ifood.*"), "Ifood");

        fixedMappings.put(Pattern.compile("(?i).*amazon.*"), "Amazon");

        fixedMappings.put(Pattern.compile("(?i).*steam.*"), "Steam");

        fixedMappings.put(Pattern.compile("(?i).*mercadolivre.*"), "Mercado Livre");
        fixedMappings.put(Pattern.compile("(?i).*mercadopago.*"), "Mercado Livre");
        fixedMappings.put(Pattern.compile("(?i)^mp"), "Mercado Livre");

        fixedMappings.put(Pattern.compile("(?i).*padaria.*"), "Alimentação");

        fixedMappings.put(Pattern.compile("(?i).*shopee.*"), "Compras Online");
        fixedMappings.put(Pattern.compile("(?i).*magazineluiza.*|.*magalu.*|.*magazinelu.*"), "Compras Online");
        fixedMappings.put(Pattern.compile("(?i).*kabum.*"), "Compras Online");
        fixedMappings.put(Pattern.compile("(?i).*nike.*"), "Compras Online");
        fixedMappings.put(Pattern.compile("(?i).*centauro.*"), "Compras Online");
        fixedMappings.put(Pattern.compile("(?i).*ferreira\\s*costa.*"), "Compras Online");
        fixedMappings.put(Pattern.compile("(?i).*nuuvem.*"), "Compras Online");

        fixedMappings.put(Pattern.compile("(?i).*farmaci.*|.*pague\\s*menos.*|.*raiadrogasil.*"), "Saúde");

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
