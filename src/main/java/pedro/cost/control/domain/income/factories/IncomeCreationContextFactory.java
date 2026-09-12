package pedro.cost.control.domain.income.factories;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pedro.cost.control.config.exceptions.BadRequestException;
import pedro.cost.control.domain.balance.entities.MonthlyBalance;
import pedro.cost.control.domain.balance.services.MonthlyBalanceService;
import pedro.cost.control.domain.contract.dtos.ContractSummaryDTO;
import pedro.cost.control.domain.contract.enums.ContractType;
import pedro.cost.control.domain.contract.services.EmploymentContractService;
import pedro.cost.control.domain.income.contexts.IncomeCreationContext;
import pedro.cost.control.domain.income.dtos.IncomeInputCreateDTO;
import pedro.cost.control.domain.income.resolvers.IncomeAmountResolver;
import pedro.cost.control.domain.user.entities.User;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class IncomeCreationContextFactory {
    private final EmploymentContractService employmentContractService;
    private final MonthlyBalanceService monthlyBalanceService;
    private final IncomeAmountResolver incomeAmountResolver;

    public IncomeCreationContext create(IncomeInputCreateDTO dto, User user) {
        ContractSummaryDTO contract = employmentContractService.getOpenedEmploymentContract(dto.getReferenceDate(), user.getId());

        checkIncomeCreateCorrespondsTheCurrentContract(dto.getContractType(), contract);

        MonthlyBalance balance = monthlyBalanceService.getOrCreateMonthlyBalance(
                dto.getReferenceDate().getYear(),
                dto.getReferenceDate().getMonthValue(),
                user
        );

        BigDecimal amount = incomeAmountResolver.resolve(dto, contract);

        return IncomeCreationContext.builder()
                .input(dto)
                .contractSummary(contract)
                .monthlyBalance(balance)
                .amount(amount)
                .build();
    }

    private void checkIncomeCreateCorrespondsTheCurrentContract(ContractType contractTypeToCreate, ContractSummaryDTO currentContract) {
        if (!currentContract.getContractType().equals(contractTypeToCreate.name())) {
            throw new BadRequestException("O contrato ativo para essa data é " + currentContract.getContractType());
        }
    }
}
