package pedro.cost.control.domain.income.contexts;

import lombok.Builder;
import lombok.Getter;
import pedro.cost.control.domain.balance.entities.MonthlyBalance;
import pedro.cost.control.domain.contract.dtos.ContractSummaryDTO;
import pedro.cost.control.domain.income.dtos.IncomeInputCreateDTO;

import java.math.BigDecimal;

@Getter
@Builder
public class IncomeCreationContext {

    private IncomeInputCreateDTO input;
    private ContractSummaryDTO contractSummary;
    private MonthlyBalance monthlyBalance;
    private BigDecimal amount;
}