package pedro.cost.control.domain.cashflow.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pedro.cost.control.domain.cost.dtos.CostOutputDTO;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class BalanceSummaryOutputDTO {
    private MoneySummaryOutputDTO moneySummary;
    private List<CostOutputDTO> costs;
}
