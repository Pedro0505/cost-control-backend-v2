package pedro.cost.control.domain.cost.calculate.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pedro.cost.control.domain.cost.calculate.context.CostCalculationContext;
import pedro.cost.control.domain.cost.calculate.strategy.CostCalculationStrategy;
import pedro.cost.control.domain.cost.calculate.strategy.CostCalculationStrategyResolver;
import pedro.cost.control.domain.cost.dtos.CreateCostInputDTO;
import pedro.cost.control.domain.income.services.IncomeService;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class CostCalculationService {
    private final CostCalculationStrategyResolver resolver;
    private final IncomeService incomeService;

    public final BigDecimal calculateAmount(CreateCostInputDTO createCostInput) {
        BigDecimal totalAmountIncomeForPercentage = incomeService.getTotalIncomeByYearAndMonth(
                createCostInput.getReferenceYear(), createCostInput.getReferenceMonth()
        );

        CostCalculationContext context = buildContext(createCostInput, totalAmountIncomeForPercentage);

        CostCalculationStrategy calculator = resolver.getCalculator(createCostInput.getCalculationType());

        return calculator.calculate(context);
    }

    private static CostCalculationContext buildContext(CreateCostInputDTO createCostInput, BigDecimal totalAmountIncomeForPercentage) {
        return CostCalculationContext.builder()
                .fixedAmount(createCostInput.getAmount())
                .percentageAmount(totalAmountIncomeForPercentage)
                .percentage(createCostInput.getPercentage())
                .build();
    }
}
