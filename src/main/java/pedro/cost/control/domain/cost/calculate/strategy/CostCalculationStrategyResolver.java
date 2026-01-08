package pedro.cost.control.domain.cost.calculate.strategy;

import org.springframework.stereotype.Component;
import pedro.cost.control.domain.cost.enums.CostCalculationType;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class CostCalculationStrategyResolver {
    private final Map<CostCalculationType, CostCalculationStrategy> calculators;

    public CostCalculationStrategyResolver(List<CostCalculationStrategy> implementations) {
        this.calculators = implementations.stream()
                .collect(Collectors.toUnmodifiableMap(CostCalculationStrategy::getType, Function.identity()));
    }

    public CostCalculationStrategy getCalculator(CostCalculationType type) {
        CostCalculationStrategy calculator = calculators.get(type);

        if (calculator == null) {
            throw new IllegalStateException("Nenhum CostCalculationStrategy encontrado para o tipo " + type);
        }

        return calculator;
    }
}
