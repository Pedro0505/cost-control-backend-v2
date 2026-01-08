package pedro.cost.control.domain.contract.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pedro.cost.control.config.exceptions.NotFoundException;
import pedro.cost.control.domain.contract.dtos.ContractSummaryDTO;
import pedro.cost.control.domain.contract.entities.EmploymentContract;
import pedro.cost.control.domain.contract.repositories.EmploymentContractRepository;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class EmploymentContractService {
    private final EmploymentContractRepository employmentContractRepository;

    public void save(EmploymentContract employmentContract) {
        employmentContractRepository.save(employmentContract);
    }

    public ContractSummaryDTO getOpenedEmploymentContract(LocalDate referenceDate) {
        return employmentContractRepository.findOpenedEmploymentContract(referenceDate)
                .orElseThrow(() -> new NotFoundException("Contrato ativo não encontrado"));
    }
}
