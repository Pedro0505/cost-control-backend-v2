package pedro.cost.control.domain.contract.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pedro.cost.control.domain.contract.dtos.PjContractInputCreateDTO;
import pedro.cost.control.domain.contract.entities.EmploymentContract;
import pedro.cost.control.domain.contract.entities.EmploymentContractPj;
import pedro.cost.control.domain.contract.repositories.EmploymentContractPjRepository;

import java.time.LocalDate;
import java.util.Optional;

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

    public Optional<EmploymentContractPj> getEmploymentContractOverlap(LocalDate initDate, LocalDate endDate) {
        return employmentContractPjRepository.findContractPjOverlap(initDate, endDate);
    }

    public Optional<EmploymentContractPj> getEmploymentContractOpenedByContractType() {
        return employmentContractPjRepository.findEmploymentContractOpenedByContractType();
    }

    public void save(EmploymentContractPj employmentContractPj) {
        employmentContractPjRepository.save(employmentContractPj);
    }
}
