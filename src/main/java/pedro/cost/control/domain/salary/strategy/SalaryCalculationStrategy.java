package pedro.cost.control.domain.salary.strategy;

import pedro.cost.control.domain.contract.enums.ContractType;
import pedro.cost.control.domain.salary.context.SalaryCalculationContext;

import java.math.BigDecimal;

public interface SalaryCalculationStrategy {

    ContractType getType();

    BigDecimal calculate(SalaryCalculationContext context);
}
