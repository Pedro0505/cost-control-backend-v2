package pedro.cost.control.domain.income.factories;

import org.springframework.stereotype.Component;
import pedro.cost.control.domain.income.contexts.IncomeCreationContext;
import pedro.cost.control.domain.income.entities.Income;
import pedro.cost.control.domain.user.entities.User;

@Component
public class IncomeFactory {
    public Income create(IncomeCreationContext context, User user) {
        Income income = new Income();
        income.setMonthlyBalance(context.getMonthlyBalance());
        income.setEmploymentContract(context.getContractSummary().getEmploymentContract());
        income.setReferenceDate(context.getInput().getReferenceDate());
        income.setDescription(context.getInput().getDescription());
        income.setAmount(context.getAmount());
        income.setUser(user);
        return income;
    }
}