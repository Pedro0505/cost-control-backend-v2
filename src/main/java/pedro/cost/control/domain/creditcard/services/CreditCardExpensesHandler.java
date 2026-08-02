package pedro.cost.control.domain.creditcard.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pedro.cost.control.domain.creditcard.contexts.CostFileDiscriminationContext;
import pedro.cost.control.domain.creditcard.dtos.CreditCardExpensesDetailsOutputDTO;
import pedro.cost.control.domain.creditcard.dtos.CreditCardExpensesGroupedOutputDTO;
import pedro.cost.control.domain.creditcard.entities.CreditCardExpense;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CreditCardExpensesHandler {
    private final CostFileClusterService costFileClusterService;

    public List<CostFileDiscriminationContext> getNormalizedExpensesDescriptions(
            List<CostFileDiscriminationContext> inputList,
            Integer invoiceReferenceYear,
            Integer invoiceReferenceMonth
    ) {

        return inputList.stream()
                .map(e -> CostFileDiscriminationContext.builder()
                        .amount(e.getAmount())
                        .date(e.getDate())
                        .description(costFileClusterService.normalizeDescription(e.getRawDescription()))
                        .isInstallment(defineIfIsInstallment(e.getRawDescription()))
                        .rawDescription(e.getRawDescription())
                        .invoiceReferenceYear(invoiceReferenceYear)
                        .invoiceReferenceMonth(invoiceReferenceMonth)
                        .build()
                )
                .filter(e -> e.getDescription() != null)
                .toList();
    }

    public List<CreditCardExpensesGroupedOutputDTO> getGroupedExpensesByEnterprise(
            List<CreditCardExpense> inputList
    ) {
        return inputList.stream()
                .collect(Collectors.groupingBy(CreditCardExpense::getNormalizedDescription))
                .entrySet()
                .stream()
                .map(this::mapToGroupedOutput)
                .sorted(Comparator.comparing(CreditCardExpensesGroupedOutputDTO::getTotalAmount).reversed())
                .toList();
    }

    public List<CreditCardExpense> updateCreditCardExpenseDescriptions(List<CreditCardExpense> creditCardExpenses) {
        creditCardExpenses.forEach(e -> {
                e.setNormalizedDescription(costFileClusterService.normalizeDescription(e.getRawDescription()));
                e.setInstallment(defineIfIsInstallment(e.getRawDescription()));
            }
        );
        return creditCardExpenses;
    }

    private CreditCardExpensesGroupedOutputDTO mapToGroupedOutput(
            Map.Entry<String, List<CreditCardExpense>> entry
    ) {
        BigDecimal totalAmount = entry.getValue().stream()
                .map(CreditCardExpense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<CreditCardExpensesDetailsOutputDTO> details = entry.getValue().stream()
                .map(this::mapToDetails)
                .toList();

        return CreditCardExpensesGroupedOutputDTO.builder()
                .normalizedDescription(entry.getKey())
                .totalAmount(totalAmount)
                .expensesDetails(details)
                .build();
    }

    private CreditCardExpensesDetailsOutputDTO mapToDetails(CreditCardExpense expense) {
        return CreditCardExpensesDetailsOutputDTO.builder()
                .expenseDate(expense.getExpenseDate())
                .amount(expense.getAmount())
                .rawDescription(expense.getRawDescription())
                .build();
    }

    private boolean defineIfIsInstallment(String texto) {
        return Pattern.compile("parcela", Pattern.CASE_INSENSITIVE)
                .matcher(texto)
                .find();
    }
}
