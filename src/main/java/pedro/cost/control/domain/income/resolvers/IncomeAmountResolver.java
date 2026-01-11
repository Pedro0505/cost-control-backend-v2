package pedro.cost.control.domain.income.resolvers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pedro.cost.control.domain.contract.dtos.ContractSummaryDTO;
import pedro.cost.control.domain.income.dtos.IncomeInputCreateDTO;
import pedro.cost.control.domain.salary.services.SalaryCalculationService;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class IncomeAmountResolver {

    private final SalaryCalculationService salaryCalculationService;

    public BigDecimal resolve(IncomeInputCreateDTO input, ContractSummaryDTO contract) {

        if (isUserDefinedAmount(input)) {
            return input.getAmount();
        }

        return calculateAutomaticAmount(input, contract);
    }

    private boolean isUserDefinedAmount(IncomeInputCreateDTO input) {
        return input.getAmount() != null && input.getAmount().compareTo(BigDecimal.ZERO) > 0;
    }

    private BigDecimal calculateAutomaticAmount(IncomeInputCreateDTO input, ContractSummaryDTO contract) {
        return salaryCalculationService.calculateSalary(contract, input.getBusinessDays());
    }
}
