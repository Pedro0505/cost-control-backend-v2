package pedro.cost.control.domain.cost.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pedro.cost.control.domain.cashflow.dtos.MoneySummaryOutputDTO;
import pedro.cost.control.domain.cost.enums.CostCalculationType;

import java.math.BigDecimal;

@NoArgsConstructor
@Getter
@Setter
public class CostSummaryOutputDTO {
    private Long id;
    private CostCalculationType calculationType;
    private BigDecimal amount;
    private String description;
    private Boolean recurrent;
    private Boolean paid;
    private MoneySummaryOutputDTO moneySummary;
}
