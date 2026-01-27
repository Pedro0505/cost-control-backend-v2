package pedro.cost.control.domain.contract.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pedro.cost.control.domain.contract.dtos.PjContractInputCreateDTO;
import pedro.cost.control.domain.contract.entities.EmploymentContract;
import pedro.cost.control.domain.contract.entities.EmploymentContractPj;
import pedro.cost.control.domain.contract.repositories.EmploymentContractPjRepository;

@Service
@RequiredArgsConstructor
public class EmploymentContractPjService {
    private final EmploymentContractPjRepository employmentContractPjRepository;

    public EmploymentContractPj createEmploymentContractPjObject(PjContractInputCreateDTO pjContractInputCreateDTO) {
        EmploymentContractPj employmentContractPj = new EmploymentContractPj();

        employmentContractPj.setHourlyRate(pjContractInputCreateDTO.getHourlyRate());
        employmentContractPj.setInitDate(pjContractInputCreateDTO.getContractInitDate());
        employmentContractPj.setEndDate(pjContractInputCreateDTO.getContractEndDate());

        return employmentContractPj;
    }

    public EmploymentContract getEmploymentContractByYearAndMonth(Integer year, Integer month) {
        return employmentContractPjRepository.findEmploymentContractByYearAndMonth(year, month);
    }
}
