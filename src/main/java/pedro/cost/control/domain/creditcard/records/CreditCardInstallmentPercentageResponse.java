package pedro.cost.control.domain.creditcard.records;

import java.math.BigDecimal;

public record CreditCardInstallmentPercentageResponse(
        Integer month,
        Integer year,
        BigDecimal installmentPercentage,
        BigDecimal nonInstallmentPercentage
) {
}