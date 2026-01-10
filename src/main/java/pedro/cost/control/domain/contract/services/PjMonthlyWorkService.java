package pedro.cost.control.domain.contract.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pedro.cost.control.config.exceptions.ConflictException;

import pedro.cost.control.config.exceptions.NotFoundException;
import pedro.cost.control.domain.contract.entities.EmploymentContract;
import pedro.cost.control.domain.contract.entities.PjMonthlyWork;
import pedro.cost.control.domain.contract.repositories.PjMonthlyWorkRepository;
import pedro.cost.control.domain.income.entities.Income;

import java.time.LocalDate;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PjMonthlyWorkService {
    private final PjMonthlyWorkRepository pjMonthlyWorkRepository;
    private final EmploymentContractPjService employmentContractPjService;

    public void save(PjMonthlyWork pjMonthlyWork) {
        pjMonthlyWorkRepository.save(pjMonthlyWork);
    }

    public void delete(Long id) {
        PjMonthlyWork pjMonthlyWork = findById(id);

        pjMonthlyWorkRepository.delete(pjMonthlyWork);
    }

    public void delete(PjMonthlyWork pjMonthlyWork) {
        pjMonthlyWorkRepository.delete(pjMonthlyWork);
    }

    @Transactional
    public void createPjMonthlyWork(LocalDate referenceDate, Integer businessDays) {
        Integer referenceYear = referenceDate.getYear();
        Integer referenceMonth = referenceDate.getMonthValue();

        validateOverlap(referenceYear, referenceMonth);

        EmploymentContract employmentContract  = employmentContractPjService.getEmploymentContractByYearAndMonth(
                referenceYear, referenceMonth
        );

        PjMonthlyWork pjMonthlyWork = createPjMonthlyWorkObject(referenceYear, referenceMonth, businessDays, employmentContract);

        save(pjMonthlyWork);
    }

    public Optional<PjMonthlyWork> getPjMonthlyWorkLinkedWithIncomeId(Long incomeId) {
        return pjMonthlyWorkRepository.findPjMonthlyWorkLinkedWithIncomeId(incomeId);
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

    private void validateOverlap(Integer referenceYear, Integer referenceMonth) {
        Optional<PjMonthlyWork> optionalPjMonthlyWork = pjMonthlyWorkRepository.findByYearAndMonth(referenceYear, referenceMonth);

        if (optionalPjMonthlyWork.isPresent()) {
            throw new ConflictException("Já existe horas cadastradas para esse mês e ano");
        }
    }

    public PjMonthlyWork findById(Long id) {
        Optional<PjMonthlyWork> optionalIncome = pjMonthlyWorkRepository.findById(id);

        if (optionalIncome.isEmpty()) {
            throw new NotFoundException("Horas PJ não encontradas");
        }

        return optionalIncome.get();
    }
}
