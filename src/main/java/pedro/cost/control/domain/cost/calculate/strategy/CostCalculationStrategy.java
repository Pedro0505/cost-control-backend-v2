package pedro.cost.control.domain.cost.calculate.strategy;

import pedro.cost.control.domain.cost.calculate.context.CostCalculationContext;
import pedro.cost.control.domain.cost.enums.CostCalculationType;

import java.math.BigDecimal;

public interface CostCalculationStrategy {
    CostCalculationType getType();
    BigDecimal calculate(CostCalculationContext context);
}
