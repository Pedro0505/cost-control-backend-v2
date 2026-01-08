package pedro.cost.control.domain.salary.strategy;

import org.springframework.stereotype.Component;
import pedro.cost.control.domain.contract.enums.ContractType;
import pedro.cost.control.domain.salary.context.SalaryCalculationContext;

import java.math.BigDecimal;

@Component
public class PjSalaryCalculationStrategy implements SalaryCalculationStrategy {
    @Override
    public ContractType getType() {
        return ContractType.PJ;
    }

    @Override
    public BigDecimal calculate(SalaryCalculationContext context) {

        BigDecimal hoursWorkedPerMonth = BigDecimal.valueOf(context.getHoursPerDay()).multiply(BigDecimal.valueOf(context.getBusinessDays()));

        return hoursWorkedPerMonth.multiply(context.getHourlyRate());
    }
}