package pedro.cost.control.domain.income.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pedro.cost.control.config.exceptions.BadRequestException;
import pedro.cost.control.domain.contract.dtos.ContractSummaryDTO;
import pedro.cost.control.domain.contract.entities.PjMonthlyWork;
import pedro.cost.control.domain.contract.enums.ContractType;
import pedro.cost.control.domain.contract.services.PjMonthlyWorkService;
import pedro.cost.control.domain.income.dtos.IncomeInputCreateDTO;
import pedro.cost.control.domain.income.entities.Income;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PjIncomeHandler {
    private final PjMonthlyWorkService pjMonthlyWorkService;

    public void deletePjMonthlyWorkLinkedWithIncomeIfNecessary(Income incomeToDelete) {
        Optional<PjMonthlyWork> pjMonthlyWorkLinkedWithIncome = pjMonthlyWorkService.getPjMonthlyWorkLinkedWithIncomeId(incomeToDelete.getId());
        pjMonthlyWorkLinkedWithIncome.ifPresent(pjMonthlyWorkService::delete);
    }

    public void createPjMonthlyWorkToIncome(IncomeInputCreateDTO incomeInputCreateDTO, ContractSummaryDTO openedEmploymentContract) {
        boolean isContractPj = openedEmploymentContract.getContractType().equals(ContractType.PJ.name());
        Integer businessDays = incomeInputCreateDTO.getBusinessDays();

        if (isContractPj) {
            if (businessDays == null || businessDays.equals(0)) {
                throw new BadRequestException("Rendas PJ devem ter os dias úteis preenchidos");
            }

            pjMonthlyWorkService.createPjMonthlyWork(incomeInputCreateDTO.getReferenceDate(), businessDays);
        }
    }
}
