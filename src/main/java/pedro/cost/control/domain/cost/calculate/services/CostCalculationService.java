package pedro.cost.control.domain.cost.calculate.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pedro.cost.control.domain.cost.calculate.context.CostCalculationContext;
import pedro.cost.control.domain.cost.calculate.strategy.CostCalculationStrategy;
import pedro.cost.control.domain.cost.calculate.strategy.CostCalculationStrategyResolver;
import pedro.cost.control.domain.cost.contexts.AmountCalculationContext;
import pedro.cost.control.domain.income.services.IncomeService;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class CostCalculationService {
    private final CostCalculationStrategyResolver resolver;
    private final IncomeService incomeService;

    public final BigDecimal calculateAmount(AmountCalculationContext amountCalculationContext) {
        BigDecimal totalAmountIncomeForPercentage = incomeService.getTotalIncomeByYearAndMonth(
                amountCalculationContext.getReferenceYear(), amountCalculationContext.getReferenceMonth()
        );

        CostCalculationContext context = buildContext(amountCalculationContext, totalAmountIncomeForPercentage);

        CostCalculationStrategy calculator = resolver.getCalculator(amountCalculationContext.getCalculationType());

        return calculator.calculate(context);
    }

    private static CostCalculationContext buildContext(
            AmountCalculationContext amountCalculationContext,
            BigDecimal totalAmountIncomeForPercentage
    ) {
        return CostCalculationContext.builder()
                .fixedAmount(amountCalculationContext.getAmount())
                .percentageAmount(totalAmountIncomeForPercentage)
                .percentage(amountCalculationContext.getPercentage())
                .build();
    }
}
