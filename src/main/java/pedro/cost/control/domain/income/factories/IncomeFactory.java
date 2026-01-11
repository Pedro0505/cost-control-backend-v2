package pedro.cost.control.domain.income.factories;

import org.springframework.stereotype.Component;
import pedro.cost.control.domain.income.contexts.IncomeCreationContext;
import pedro.cost.control.domain.income.entities.Income;

@Component
public class IncomeFactory {
    public Income create(IncomeCreationContext context) {
        Income income = new Income();
        income.setMonthlyBalance(context.getMonthlyBalance());
        income.setEmploymentContract(context.getContractSummary().getEmploymentContract());
        income.setReferenceDate(context.getInput().getReferenceDate());
        income.setDescription(context.getInput().getDescription());
        income.setAmount(context.getAmount());
        return income;
    }
}