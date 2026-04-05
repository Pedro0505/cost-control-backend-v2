package pedro.cost.control.domain.cost.factories;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pedro.cost.control.domain.balance.entities.MonthlyBalance;
import pedro.cost.control.domain.cost.contexts.AmountCalculationContext;
import pedro.cost.control.domain.cost.dtos.UpdateCostInputDTO;
import pedro.cost.control.domain.cost.entities.Cost;
import pedro.cost.control.domain.cost.resolvers.CostAmountResolver;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class CostUpdateAmountFactory {
    private final CostAmountResolver costAmountResolver;

    public BigDecimal getUpdateAmount(Cost costUpdate, UpdateCostInputDTO updateCostInput) {
        MonthlyBalance monthlyBalance = costUpdate.getMonthlyBalance();
        Integer referenceYear = monthlyBalance.getReferenceYear();
        Integer referenceMonth = monthlyBalance.getReferenceMonth();

        AmountCalculationContext amountCalculationContext = buildContext(updateCostInput, referenceYear, referenceMonth);

        return costAmountResolver.resolve(amountCalculationContext);
    }

    private static AmountCalculationContext buildContext(UpdateCostInputDTO updateCostInput, Integer referenceYear, Integer referenceMonth) {
        return AmountCalculationContext.builder()
                .percentage(updateCostInput.getPercentage())
                .calculationType(updateCostInput.getCalculationType())
                .amount(updateCostInput.getAmount())
                .referenceYear(referenceYear)
                .referenceMonth(referenceMonth)
                .build();
    }
}
