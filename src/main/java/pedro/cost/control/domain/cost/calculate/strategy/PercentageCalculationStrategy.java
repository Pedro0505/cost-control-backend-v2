package pedro.cost.control.domain.cost.calculate.strategy;

import org.springframework.stereotype.Component;
import pedro.cost.control.common.MonetaryCalculationRules;
import pedro.cost.control.domain.cost.calculate.context.CostCalculationContext;
import pedro.cost.control.domain.cost.enums.CostCalculationType;

import java.math.BigDecimal;

@Component
public class PercentageCalculationStrategy implements CostCalculationStrategy {
    @Override
    public CostCalculationType getType() {
        return CostCalculationType.PERCENTAGE;
    }

    @Override
    public BigDecimal calculate(CostCalculationContext context) {
        BigDecimal percentageFactor = context.getPercentage().divide(
                BigDecimal.valueOf(100),
                MonetaryCalculationRules.MONEY_SCALE,
                MonetaryCalculationRules.MONEY_ROUNDING
        );

        return context.getPercentageAmount().multiply(percentageFactor)
                .setScale(MonetaryCalculationRules.MONEY_SCALE, MonetaryCalculationRules.MONEY_ROUNDING);
    }
}
