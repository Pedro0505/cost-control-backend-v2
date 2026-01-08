package pedro.cost.control.domain.cost.calculate.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pedro.cost.control.common.YearMonthSummary;
import pedro.cost.control.domain.cost.calculate.context.CostCalculationContext;
import pedro.cost.control.domain.cost.calculate.strategy.CostCalculationStrategy;
import pedro.cost.control.domain.cost.calculate.strategy.CostCalculationStrategyResolver;
import pedro.cost.control.domain.cost.dtos.CreateCostInputDTO;
import pedro.cost.control.domain.income.services.IncomeService;
import pedro.cost.control.utils.MonthYearUtils;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class CostCalculationService {
    private final CostCalculationStrategyResolver resolver;
    private final IncomeService incomeService;

    public final BigDecimal calculateAmount(CreateCostInputDTO createCostInput) {
        BigDecimal totalAmountIncomeForPercentage = getTotalAmountIncomeForPercentage(createCostInput);

        CostCalculationContext context = buildContext(createCostInput, totalAmountIncomeForPercentage);

        CostCalculationStrategy calculator = resolver.getCalculator(createCostInput.getCalculationType());

        return calculator.calculate(context);
    }

    private BigDecimal getTotalAmountIncomeForPercentage(CreateCostInputDTO createCostInput) {
        YearMonthSummary yearMonthSummary = MonthYearUtils.decreaseMonth(
                createCostInput.getReferenceYear(), createCostInput.getReferenceMonth()
        );

        return incomeService.getTotalIncomeByYearAndMonth(yearMonthSummary.getYear(), yearMonthSummary.getMonth());
    }

    private static CostCalculationContext buildContext(CreateCostInputDTO createCostInput, BigDecimal totalAmountIncomeForPercentage) {
        return CostCalculationContext.builder()
                .fixedAmount(createCostInput.getAmount())
                .percentageAmount(totalAmountIncomeForPercentage)
                .percentage(createCostInput.getPercentage())
                .build();
    }
}
