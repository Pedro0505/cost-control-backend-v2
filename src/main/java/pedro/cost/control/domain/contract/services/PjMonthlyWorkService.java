package pedro.cost.control.domain.contract.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pedro.cost.control.config.exceptions.ConflictException;

import pedro.cost.control.domain.contract.entities.EmploymentContract;
import pedro.cost.control.domain.contract.entities.PjMonthlyWork;
import pedro.cost.control.domain.contract.repositories.PjMonthlyWorkRepository;

import java.time.LocalDate;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PjMonthlyWorkService {
    private final PjMonthlyWorkRepository pjMonthlyWorkRepository;
    private final EmploymentContractPjService employmentContractPjService;

    @Transactional
    public void createPjMonthlyWork(LocalDate referenceDate, Integer businessDays, Long userId) {
        Integer referenceYear = referenceDate.getYear();
        Integer referenceMonth = referenceDate.getMonthValue();

        validateOverlap(referenceYear, referenceMonth, userId);

        EmploymentContract employmentContract  = employmentContractPjService.getEmploymentContractByYearAndMonth(
                referenceYear, referenceMonth, userId
        );

        PjMonthlyWork pjMonthlyWork = createPjMonthlyWorkObject(referenceYear, referenceMonth, businessDays, employmentContract);

        save(pjMonthlyWork);
    }

    public void save(PjMonthlyWork pjMonthlyWork) {
        pjMonthlyWorkRepository.save(pjMonthlyWork);
    }

    public Optional<PjMonthlyWork> getPjMonthlyWorkLinkedWithIncomeId(Long incomeId, Long userId) {
        return pjMonthlyWorkRepository.findPjMonthlyWorkLinkedWithIncomeId(incomeId, userId);
    }

    public void delete(PjMonthlyWork pjMonthlyWork) {
        pjMonthlyWorkRepository.delete(pjMonthlyWork);
    }

    private PjMonthlyWork createPjMonthlyWorkObject(
            Integer referenceYear, Integer referenceMonth, Integer businessDays, EmploymentContract employmentContract
    ) {
        PjMonthlyWork pjMonthlyWork = new PjMonthlyWork();

        pjMonthlyWork.setReferenceMonth(referenceMonth);
        pjMonthlyWork.setReferenceYear(referenceYear);
        pjMonthlyWork.setBusinessDays(businessDays);
        pjMonthlyWork.setEmploymentContract(employmentContract);

        return pjMonthlyWork;
    }

    private void validateOverlap(Integer referenceYear, Integer referenceMonth, Long userId) {
        Optional<PjMonthlyWork> optionalPjMonthlyWork = pjMonthlyWorkRepository.findByYearAndMonth(referenceYear, referenceMonth, userId);

        if (optionalPjMonthlyWork.isPresent()) {
            throw new ConflictException("Já existe horas cadastradas para esse mês e ano");
        }
    }
}
