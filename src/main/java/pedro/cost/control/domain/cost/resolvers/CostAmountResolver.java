package pedro.cost.control.domain.cost.resolvers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pedro.cost.control.domain.cost.calculate.services.CostCalculationService;
import pedro.cost.control.domain.cost.dtos.CreateCostInputDTO;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class CostAmountResolver {
    private final CostCalculationService costCalculationService;

    public BigDecimal resolve(CreateCostInputDTO input) {
        return costCalculationService.calculateAmount(input);
    }
}
