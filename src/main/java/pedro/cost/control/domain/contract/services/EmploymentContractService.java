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
import pedro.cost.control.domain.user.entities.User;

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
    private void addNewContract(EmploymentContract newContract, LocalDate initDate, LocalDate endDate, Long userId) {
        validateIfHasContractOverlap(initDate, endDate, userId);

        Optional<EmploymentContract> openedEmploymentContract = getEmploymentContractOpened(userId);

        endsOpenContractsDate(newContract.getInitDate(), openedEmploymentContract.orElse(null));

        employmentContractRepository.save(newContract);
    }

    public void addNewPjContract(PjContractInputCreateDTO dto, User user) {
        EmploymentContractPj contract = employmentContractPjService.createEmploymentContractPjObject(dto, user);

        addNewContract(contract, dto.getContractInitDate(),dto.getContractEndDate(), user.getId());
    }

    public void addNewCltContract(CltContractInputCreateDTO dto, User user) {
        EmploymentContractClt contract = employmentContractCltService.createEmploymentContractCltObject(dto, user);

        addNewContract(contract,dto.getContractInitDate(),dto.getContractEndDate(), user.getId());
    }

    public void save(EmploymentContract employmentContract) {
        employmentContractRepository.save(employmentContract);
    }

    public ContractSummaryDTO getOpenedEmploymentContract(LocalDate referenceDate, Long userId) {
        return employmentContractRepository.findOpenedEmploymentContract(referenceDate, userId)
                .orElseThrow(() -> new NotFoundException("Contrato ativo não encontrado"));
    }

    public LegacyPageResponse<EmploymentContractOutputDTO> getAllContractsPaged(PageRequest pageable, Long userId) {
        Page<EmploymentContractOutputDTO> employmentContract = employmentContractRepository.getAllContractsPaged(
                pageable, userId
        );

        return new LegacyPageResponse<>(employmentContract);
    }

    public Optional<EmploymentContract> getEmploymentContractOverlap(LocalDate initDate, LocalDate endDate, Long userId) {
        return employmentContractRepository.findContractPjOverlap(initDate, endDate, userId);
    }

    private void validateIfHasContractOverlap(LocalDate initDate, LocalDate endDate, Long userId) {
        Optional<EmploymentContract> employmentContractOverlap = getEmploymentContractOverlap(initDate, endDate, userId);

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

    private Optional<EmploymentContract> getEmploymentContractOpened(Long userId) {
        return employmentContractRepository.findEmploymentContractOpened(userId);
    }
}
