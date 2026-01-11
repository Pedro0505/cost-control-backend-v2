package pedro.cost.control.domain.cost.factories;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pedro.cost.control.config.exceptions.NotFoundException;
import pedro.cost.control.domain.balance.entities.MonthlyBalance;
import pedro.cost.control.domain.balance.services.MonthlyBalanceService;
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
        MonthlyBalance balance = monthlyBalanceService
                .getMonthlyBalanceByYearAndMonth(dto.getReferenceYear(), dto.getReferenceMonth())
                .orElseThrow(() -> new NotFoundException(
                        "Não foi encontrado o mês " + dto.getReferenceMonth() +
                        " e ano " + dto.getReferenceYear()
                ));

        BigDecimal amount = costAmountResolver.resolve(dto);

        return CostCreationContext.builder()
                .input(dto)
                .monthlyBalance(balance)
                .amount(amount)
                .build();
    }
}
