package pedro.cost.control.domain.creditcard.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class CreditCardExpensesGroupedOutputDTO {
    private String normalizedDescription;
    private BigDecimal totalAmount;
    private List<CreditCardExpensesDetailsOutputDTO> expensesDetails;
}
