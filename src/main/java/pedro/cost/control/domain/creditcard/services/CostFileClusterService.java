package pedro.cost.control.domain.creditcard.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pedro.cost.control.domain.creditcard.contexts.CostFileDiscriminationContext;
import pedro.cost.control.domain.creditcard.emuns.CreditCardCategoryType;
import pedro.cost.control.domain.creditcard.entities.CreditCardCategory;
import pedro.cost.control.domain.creditcard.entities.CreditCardCategoryRule;

import java.text.Normalizer;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CostFileClusterService {
    private final CreditCardCategoryRuleService cardCategoryRuleService;
    private final CreditCardCategoryService creditCardCategoryService;

    public String normalizeDescription(String raw) {
        if (raw == null) {
            return getOutrosCategory().getName();
        }

        String title = raw.trim();

        String noAccents = Normalizer.normalize(title, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .replaceAll("(?i)\\s*-\\s*parcela.*$", "")
                .trim();

        String lower = noAccents.toLowerCase();

        if (lower.contains("ifood") || lower.contains("ifd") || lower.contains("ifoo")) {
            return "Ifood";
        }

        List<CreditCardCategoryRule> rules = cardCategoryRuleService.getByActiveTrue();

        for (CreditCardCategoryRule rule : rules) {
            Pattern pattern = Pattern.compile(rule.getPattern(), Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(noAccents);

            if (matcher.find()) {
                CreditCardCategory category = rule.getCategory();
                return category.getName();
            }
        }

        return getOutrosCategory().getName();
    }

    private CreditCardCategory getOutrosCategory() {
        return creditCardCategoryService.getCardCategoryByType(CreditCardCategoryType.OUTROS);
    }

    public Map<String, List<CostFileDiscriminationContext>> groupExpensesByEnterprise(
            List<CostFileDiscriminationContext> inputList
    ) {
        return inputList.stream().collect(Collectors.groupingBy(item -> normalizeDescription(item.getDescription())));
    }
}