package pedro.cost.control.domain.contract.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import pedro.cost.control.common.LegacyPageResponse;
import pedro.cost.control.config.exceptions.ConflictException;
import pedro.cost.control.config.exceptions.NotFoundException;
import pedro.cost.control.domain.contract.dtos.CltContractInputCreateDTO;
import pedro.cost.control.domain.contract.dtos.ContractSummaryDTO;
import pedro.cost.control.domain.contract.dtos.EmploymentContractOutputDTO;
import pedro.cost.control.domain.contract.dtos.PjContractInputCreateDTO;
import pedro.cost.control.domain.contract.entities.EmploymentContract;
import pedro.cost.control.domain.contract.entities.EmploymentContractClt;
import pedro.cost.control.domain.contract.entities.EmploymentContractPj;
import pedro.cost.control.domain.contract.repositories.EmploymentContractRepository;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmploymentContractService {
    private final EmploymentContractPjService employmentContractPjService;
    private final EmploymentContractCltService employmentContractCltService;
    private final EmploymentContractRepository employmentContractRepository;

    @Transactional
    private void addNewContract(EmploymentContract newContract, LocalDate initDate, LocalDate endDate) {
        validateIfHasContractOverlap(initDate, endDate);

        Optional<EmploymentContract> openedEmploymentContract = getEmploymentContractOpened();

        endsOpenContractsDate(newContract.getInitDate(), openedEmploymentContract.orElse(null));

        employmentContractRepository.save(newContract);
    }

    public void addNewPjContract(PjContractInputCreateDTO dto) {
        EmploymentContractPj contract = employmentContractPjService.createEmploymentContractPjObject(dto);

        addNewContract(contract, dto.getContractInitDate(),dto.getContractEndDate());
    }

    public void addNewCltContract(CltContractInputCreateDTO dto) {
        EmploymentContractClt contract = employmentContractCltService.createEmploymentContractCltObject(dto);

        addNewContract(contract,dto.getContractInitDate(),dto.getContractEndDate());
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

        return new LegacyPageResponse<>(employmentContract);
    }

    public Optional<EmploymentContract> getEmploymentContractOverlap(LocalDate initDate, LocalDate endDate) {
        return employmentContractRepository.findContractPjOverlap(initDate, endDate);
    }

    private void validateIfHasContractOverlap(LocalDate initDate, LocalDate endDate) {
        Optional<EmploymentContract> employmentContractOverlap = getEmploymentContractOverlap(initDate, endDate);

        if (employmentContractOverlap.isPresent()) {
            throw new ConflictException("Já existe um contrato " + employmentContractOverlap.get().getContractType() + " ativo para essa data");
        }
    }

    private void endsOpenContractsDate(LocalDate newContractInitDate, EmploymentContract openedEmploymentContract) {
        if (openedEmploymentContract != null && openedEmploymentContract.getInitDate().isBefore(newContractInitDate)) {
            openedEmploymentContract.setEndDate(newContractInitDate.minusMonths(1).with(TemporalAdjusters.lastDayOfMonth()));
            save(openedEmploymentContract);
        }
    }

    private Optional<EmploymentContract> getEmploymentContractOpened() {
        return employmentContractRepository.findEmploymentContractOpened();
    }
}
