package pedro.cost.control.domain.salary.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pedro.cost.control.domain.contract.dtos.ContractSummaryDTO;
import pedro.cost.control.domain.contract.enums.ContractType;
import pedro.cost.control.domain.salary.strategy.SalaryCalculationStrategy;
import pedro.cost.control.domain.salary.context.SalaryCalculationContext;
import pedro.cost.control.domain.salary.strategy.SalaryCalculationStrategyResolver;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class SalaryCalculationService {
    private static final int DEFAULT_HOURS_PER_DAY = 8;

    private final SalaryCalculationStrategyResolver resolver;

    public BigDecimal calculateSalary(ContractSummaryDTO contractSummary, Integer businessDays) {

        ContractType contractType = ContractType.getByName(contractSummary.getContractType());

        SalaryCalculationContext context = buildContext(contractSummary, businessDays);

        SalaryCalculationStrategy calculator = resolver.getCalculator(contractType);

        return calculator.calculate(context);
    }

    private SalaryCalculationContext buildContext(ContractSummaryDTO contractSummary, Integer businessDays) {
        return new SalaryCalculationContext(
                contractSummary.getNetSalary(),
                contractSummary.getHourlyRate(),
                businessDays,
                DEFAULT_HOURS_PER_DAY
        );
    }
}