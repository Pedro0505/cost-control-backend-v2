package pedro.cost.control.domain.cashflow.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class MoneySummaryOutputDTO {
    private BigDecimal income;
    private BigDecimal expense;
    private BigDecimal balance;
}
