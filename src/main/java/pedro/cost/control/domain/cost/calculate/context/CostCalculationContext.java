package pedro.cost.control.domain.cost.calculate.context;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@Data
@RequiredArgsConstructor
@Builder
public class CostCalculationContext {
    private final BigDecimal percentageAmount;
    private final BigDecimal percentage;
    private final BigDecimal fixedAmount;
}
