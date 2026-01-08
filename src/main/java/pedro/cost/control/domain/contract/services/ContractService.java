package pedro.cost.control.domain.contract.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pedro.cost.control.config.exceptions.ConflictException;
import pedro.cost.control.domain.contract.dtos.PjContractInputCreateDTO;
import pedro.cost.control.domain.contract.entities.EmploymentContract;
import pedro.cost.control.domain.contract.entities.EmploymentContractPj;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ContractService {
    private final EmploymentContractPjService employmentContractPjService;
    private final EmploymentContractService employmentContractService;

    @Transactional
    public void addNewPjContract(PjContractInputCreateDTO pjContractInputCreateDTO) {
        LocalDate contractInitDate = pjContractInputCreateDTO.getContractInitDate();
        LocalDate contractEndDate = pjContractInputCreateDTO.getContractEndDate();

        validateIfHasContractOverlap(contractInitDate, contractEndDate);

        EmploymentContractPj employmentContractPj = employmentContractPjService.createEmploymentContractPjObject(pjContractInputCreateDTO);
        Optional<EmploymentContractPj> openedEmploymentContractPj = employmentContractPjService.getEmploymentContractOpenedByContractType();

        endsOpenContractsDate(employmentContractPj.getInitDate(), openedEmploymentContractPj.orElse(null));
        employmentContractPjService.save(employmentContractPj);
    }

    private void validateIfHasContractOverlap(LocalDate initDate, LocalDate endDate) {
        Optional<EmploymentContractPj> employmentContractOverlap = employmentContractPjService.getEmploymentContractOverlap(
                initDate, endDate
        );

        if (employmentContractOverlap.isPresent()) {
            throw new ConflictException("Já existe um contrato PJ ativo para essa data");
        }
    }

    private void endsOpenContractsDate(LocalDate newContractInitDate, EmploymentContract openedEmploymentContract) {
        if (openedEmploymentContract != null && openedEmploymentContract.getInitDate().isBefore(newContractInitDate)) {
            openedEmploymentContract.setEndDate(newContractInitDate.minusMonths(1).with(TemporalAdjusters.lastDayOfMonth()));
            employmentContractService.save(openedEmploymentContract);
        }
    }
}
