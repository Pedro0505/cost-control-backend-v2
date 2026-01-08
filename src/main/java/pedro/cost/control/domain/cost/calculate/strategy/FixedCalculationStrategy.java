package pedro.cost.control.domain.cost.calculate.strategy;

import org.springframework.stereotype.Component;
import pedro.cost.control.domain.cost.calculate.context.CostCalculationContext;
import pedro.cost.control.domain.cost.enums.CostCalculationType;

import java.math.BigDecimal;

@Component
public class FixedCalculationStrategy implements CostCalculationStrategy {
    @Override
    public CostCalculationType getType() {
        return CostCalculationType.FIXED;
    }

    @Override
    public BigDecimal calculate(CostCalculationContext context) {
        return context.getFixedAmount();
    }
}
