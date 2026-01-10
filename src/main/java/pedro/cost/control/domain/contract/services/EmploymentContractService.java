package pedro.cost.control.domain.contract.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import pedro.cost.control.common.LegacyPageResponse;
import pedro.cost.control.config.exceptions.ConflictException;
import pedro.cost.control.config.exceptions.NotFoundException;
import pedro.cost.control.domain.contract.dtos.ContractSummaryDTO;
import pedro.cost.control.domain.contract.dtos.EmploymentContractOutputDTO;
import pedro.cost.control.domain.contract.dtos.PjContractInputCreateDTO;
import pedro.cost.control.domain.contract.entities.EmploymentContract;
import pedro.cost.control.domain.contract.entities.EmploymentContractPj;
import pedro.cost.control.domain.contract.repositories.EmploymentContractRepository;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmploymentContractService {
    private final EmploymentContractPjService employmentContractPjService;
    private final EmploymentContractRepository employmentContractRepository;

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

    public void save(EmploymentContract employmentContract) {
        employmentContractRepository.save(employmentContract);
    }

    public ContractSummaryDTO getOpenedEmploymentContract(LocalDate referenceDate) {
        return employmentContractRepository.findOpenedEmploymentContract(referenceDate)
                .orElseThrow(() -> new NotFoundException("Contrato ativo não encontrado"));
    }

    public LegacyPageResponse<EmploymentContractOutputDTO> getAllContractsPaged(PageRequest pageable) {
        Page<EmploymentContractOutputDTO> employmentContract = employmentContractRepository.getAllContractsPaged(pageable);

        return new LegacyPageResponse<>(
                employmentContract.getContent(),
                employmentContract.getPageable(),
                employmentContract.getTotalPages(),
                employmentContract.getTotalElements(),
                employmentContract.isLast(),
                employmentContract.isFirst(),
                employmentContract.getSize(),
                employmentContract.getNumber(),
                employmentContract.getSort(),
                employmentContract.getNumberOfElements(),
                employmentContract.isEmpty()
        );
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
            save(openedEmploymentContract);
        }
    }
}
