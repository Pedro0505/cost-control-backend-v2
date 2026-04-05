package pedro.cost.control.domain.cost.contexts;

import lombok.Builder;
import lombok.Getter;
import pedro.cost.control.domain.cost.enums.CostCalculationType;

import java.math.BigDecimal;

@Getter
@Builder
public class AmountCalculationContext {
    private Integer referenceMonth;
    private Integer referenceYear;
    private CostCalculationType calculationType;
    private BigDecimal percentage;
    private BigDecimal amount;
}
