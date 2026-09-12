package pedro.cost.control.domain.contract.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pedro.cost.control.config.exceptions.NotFoundException;
import pedro.cost.control.domain.contract.dtos.PjContractInputCreateDTO;
import pedro.cost.control.domain.contract.entities.EmploymentContract;
import pedro.cost.control.domain.contract.entities.EmploymentContractPj;
import pedro.cost.control.domain.contract.repositories.EmploymentContractPjRepository;
import pedro.cost.control.domain.user.entities.User;

import java.time.LocalDate;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmploymentContractPjService {
    private final EmploymentContractPjRepository employmentContractPjRepository;

    public EmploymentContractPj createEmploymentContractPjObject(PjContractInputCreateDTO pjContractInputCreateDTO, User user) {
        EmploymentContractPj employmentContractPj = new EmploymentContractPj();

        employmentContractPj.setHourlyRate(pjContractInputCreateDTO.getHourlyRate());
        employmentContractPj.setInitDate(pjContractInputCreateDTO.getContractInitDate());
        employmentContractPj.setEndDate(pjContractInputCreateDTO.getContractEndDate());
        employmentContractPj.setUser(user);

        return employmentContractPj;
    }

    public EmploymentContract getEmploymentContractByYearAndMonth(Integer year, Integer month, Long userId) {
        LocalDate referenceDate = LocalDate.of(year, month, 1);

        return findEmploymentContractByYearAndMonth(referenceDate, userId);
    }

    private EmploymentContract findEmploymentContractByYearAndMonth(LocalDate referenceDate, Long userId) {
        Optional<EmploymentContractPj> employmentContractPj = employmentContractPjRepository.findEmploymentContractByYearAndMonth(
                referenceDate, userId
        );

        return employmentContractPj.orElseThrow(() -> new NotFoundException("Não foi encontrado um contrato ativo"));
    }
}
