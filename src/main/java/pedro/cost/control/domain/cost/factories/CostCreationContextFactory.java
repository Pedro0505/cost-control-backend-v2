package pedro.cost.control.domain.cost.factories;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pedro.cost.control.config.exceptions.NotFoundException;
import pedro.cost.control.domain.balance.entities.MonthlyBalance;
import pedro.cost.control.domain.balance.services.MonthlyBalanceService;
import pedro.cost.control.domain.cost.contexts.AmountCalculationContext;
import pedro.cost.control.domain.cost.contexts.CostCreationContext;
import pedro.cost.control.domain.cost.dtos.CreateCostInputDTO;
import pedro.cost.control.domain.cost.resolvers.CostAmountResolver;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class CostCreationContextFactory {
    private final MonthlyBalanceService monthlyBalanceService;
    private final CostAmountResolver costAmountResolver;

    public CostCreationContext create(CreateCostInputDTO dto) {
        MonthlyBalance balance = getMonthlyBalanceByYearAndMonth(dto.getReferenceYear(), dto.getReferenceMonth());
        AmountCalculationContext amountCalculationContext = buildContext(dto);
        BigDecimal amount = costAmountResolver.resolve(amountCalculationContext);

        return CostCreationContext.builder()
                .input(dto)
                .monthlyBalance(balance)
                .amount(amount)
                .build();
    }

    private static AmountCalculationContext buildContext(CreateCostInputDTO dto) {
        return AmountCalculationContext.builder()
                .percentage(dto.getPercentage())
                .calculationType(dto.getCalculationType())
                .referenceYear(dto.getReferenceYear())
                .referenceMonth(dto.getReferenceMonth())
                .amount(dto.getAmount())
                .build();
    }

    public MonthlyBalance getMonthlyBalanceByYearAndMonth(Integer year, Integer month) {
        return monthlyBalanceService.getMonthlyBalanceByYearAndMonth(year, month)
                .orElseThrow(() -> new NotFoundException("Não foi encontrado o mês " + month + " e ano " + year));
    }
}
