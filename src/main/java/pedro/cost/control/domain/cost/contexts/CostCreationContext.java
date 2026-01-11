package pedro.cost.control.domain.cost.contexts;

import lombok.Builder;
import lombok.Getter;
import pedro.cost.control.domain.balance.entities.MonthlyBalance;
import pedro.cost.control.domain.cost.dtos.CreateCostInputDTO;

import java.math.BigDecimal;

@Getter
@Builder
public class CostCreationContext {
    private CreateCostInputDTO input;
    private MonthlyBalance monthlyBalance;
    private BigDecimal amount;
}