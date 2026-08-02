package pedro.cost.control.domain.creditcard.records;

import java.math.BigDecimal;

public record CreditCardExpensePercentageResponse(
        Integer month,
        Integer year,
        String normalizedDescription,
        BigDecimal percentage
) {
}