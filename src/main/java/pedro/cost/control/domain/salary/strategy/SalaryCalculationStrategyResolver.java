package pedro.cost.control.domain.salary.strategy;

import org.springframework.stereotype.Component;
import pedro.cost.control.domain.contract.enums.ContractType;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class SalaryCalculationStrategyResolver {
    private final Map<ContractType, SalaryCalculationStrategy> calculators;

    public SalaryCalculationStrategyResolver(List<SalaryCalculationStrategy> implementations) {
        this.calculators = implementations.stream()
                .collect(Collectors.toUnmodifiableMap(SalaryCalculationStrategy::getType, Function.identity()));
    }

    public SalaryCalculationStrategy getCalculator(ContractType type) {
        SalaryCalculationStrategy calculator = calculators.get(type);

        if (calculator == null) {
            throw new IllegalStateException("Nenhum SalaryCalculationStrategy encontrado para o tipo " + type);
        }

        return calculator;
    }
}