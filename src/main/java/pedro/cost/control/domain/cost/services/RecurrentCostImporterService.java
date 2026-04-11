package pedro.cost.control.domain.cost.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pedro.cost.control.common.MonetaryCalculationRules;
import pedro.cost.control.domain.balance.entities.MonthlyBalance;
import pedro.cost.control.domain.cost.dtos.ImportCostRecurrentInputDTO;
import pedro.cost.control.domain.cost.entities.Cost;
import pedro.cost.control.domain.cost.enums.CostCalculationType;
import pedro.cost.control.domain.cost.repositories.CostRepository;
import pedro.cost.control.domain.income.services.IncomeService;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RecurrentCostImporterService {
    private final CostRepository costRepository;
    private final IncomeService incomeService;

    public void importRecurrentCosts(ImportCostRecurrentInputDTO input, MonthlyBalance targetMonthlyBalance) {
        List<Cost> costsToPersist = getRecalculatedCostFromTarget(
                input.getSourceReferenceYear(),
                input.getSourceReferenceMonth(),
                input.getTargetReferenceYear(),
                input.getTargetReferenceMonth(),
                targetMonthlyBalance
        );

        costRepository.saveAll(costsToPersist);
    }

    public List<Cost> getRecalculatedCostFromTarget(
            Integer sourceReferenceYear,
            Integer sourceReferenceMonth,
            Integer targetReferenceYear,
            Integer targetReferenceMonth,
            MonthlyBalance targetMonthlyBalance
    ) {
        List<Cost> sourceCosts = findSourceCosts(sourceReferenceYear, sourceReferenceMonth);
        BigDecimal targetIncome = findTargetIncome(targetReferenceYear, targetReferenceMonth);

        return sourceCosts.stream()
                .map(cost -> cloneAndRecalculate(cost, targetMonthlyBalance, targetIncome))
                .toList();
    }

    private List<Cost> findSourceCosts(Integer referenceYear, Integer referenceMonth) {
        return costRepository.findAllRecurrentCostByYearMonth(referenceYear, referenceMonth);
    }

    private BigDecimal findTargetIncome(Integer referenceYear, Integer referenceMonth) {
        return incomeService.getTotalIncomeByYearAndMonth(referenceYear, referenceMonth);
    }

    private Cost cloneAndRecalculate(Cost source, MonthlyBalance targetBalance, BigDecimal targetIncome) {
        BigDecimal amount = calculateAmount(source, targetIncome);

        return Cost.builder()
                .amount(amount)
                .percentage(source.getPercentage())
                .calculationType(source.getCalculationType())
                .description(source.getDescription())
                .recurrent(source.getRecurrent())
                .monthlyBalance(targetBalance)
                .paid(source.getPaid())
                .build();
    }

    private BigDecimal calculateAmount(Cost cost, BigDecimal targetIncome) {
        if (!CostCalculationType.PERCENTAGE.equals(cost.getCalculationType())) {
            return cost.getAmount();
        }

        BigDecimal percentageFactor = cost.getPercentage().divide(
                BigDecimal.valueOf(100),
                MonetaryCalculationRules.MONEY_SCALE,
                MonetaryCalculationRules.MONEY_ROUNDING
        );

        return targetIncome.multiply(percentageFactor);
    }
}
