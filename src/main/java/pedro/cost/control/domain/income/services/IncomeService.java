package pedro.cost.control.domain.income.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import pedro.cost.control.common.LegacyPageResponse;
import pedro.cost.control.config.exceptions.NotFoundException;
import pedro.cost.control.domain.balance.entities.MonthlyBalance;
import pedro.cost.control.domain.balance.services.MonthlyBalanceService;
import pedro.cost.control.domain.contract.dtos.ContractSummaryDTO;
import pedro.cost.control.domain.contract.entities.EmploymentContract;
import pedro.cost.control.domain.contract.services.EmploymentContractService;
import pedro.cost.control.domain.income.dtos.IncomeInputCreateDTO;
import pedro.cost.control.domain.income.dtos.IncomeOutputDTO;
import pedro.cost.control.domain.income.entities.Income;
import pedro.cost.control.domain.income.mapper.IncomeMapper;
import pedro.cost.control.domain.income.repositories.IncomeRepository;
import pedro.cost.control.domain.salary.services.SalaryCalculationService;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class IncomeService {
    private final IncomeRepository incomeRepository;
    private final EmploymentContractService employmentContractService;
    private final SalaryCalculationService salaryCalculationService;
    private final MonthlyBalanceService monthlyBalanceService;
    private final IncomeMapper incomeMapper;

    public void save(Income income) {
        incomeRepository.save(income);
    }

    @Transactional
    public void createIncome(IncomeInputCreateDTO incomeInputCreateDTO) {
        Integer referenceYear = incomeInputCreateDTO.getReferenceDate().getYear();
        Integer referenceMonth = incomeInputCreateDTO.getReferenceDate().getMonthValue();

        ContractSummaryDTO contractSummaryDTO = employmentContractService.getOpenedEmploymentContract(
                incomeInputCreateDTO.getReferenceDate().minusMonths(1)
        );

        MonthlyBalance incomeMonthlyBalance = monthlyBalanceService.getOrCreateMonthlyBalance(referenceYear, referenceMonth);
        BigDecimal salary = determineSalaryAmount(incomeInputCreateDTO, contractSummaryDTO);

        Income incomeCreated = createIncomeObject(
                incomeInputCreateDTO,
                incomeMonthlyBalance,
                contractSummaryDTO.getEmploymentContract(),
                salary
        );

        save(incomeCreated);
    }

    public BigDecimal getTotalIncomeByYearAndMonth(Integer year, Integer month) {
        return incomeRepository.sumAmountByMonth(year, month)
                .orElseThrow(() -> new NotFoundException(
                        "Não foi encontradas entradas para o mês " + month + " e ano " + year
                ));
    }

    private BigDecimal determineSalaryAmount(IncomeInputCreateDTO incomeInputCreateDTO, ContractSummaryDTO contractSummaryDTO) {
        BigDecimal salaryAmount = salaryCalculationService.calculateSalary(contractSummaryDTO);
        Optional<BigDecimal> optionalAmount = Optional.ofNullable(incomeInputCreateDTO.getAmount());

        if (optionalAmount.isEmpty() || optionalAmount.get().equals(BigDecimal.ZERO)) {
            return salaryAmount;
        }

        return optionalAmount.orElse(salaryAmount);
    }

    private Income createIncomeObject(IncomeInputCreateDTO incomeInputCreate, MonthlyBalance monthlyBalance, EmploymentContract employmentContract, BigDecimal amount) {
        Income incomeToCreate = new Income();

        incomeToCreate.setMonthlyBalance(monthlyBalance);
        incomeToCreate.setEmploymentContract(employmentContract);
        incomeToCreate.setReferenceDate(incomeInputCreate.getReferenceDate());
        incomeToCreate.setDescription(incomeInputCreate.getDescription());
        incomeToCreate.setAmount(amount);

        return incomeToCreate;
    }


    public LegacyPageResponse<IncomeOutputDTO> getAllPageable(PageRequest pageable) {
        Page<IncomeOutputDTO> pageResult = incomeRepository.findAll(pageable).map(incomeMapper::toDto);

        return new LegacyPageResponse<>(
                pageResult.getContent(),
                pageResult.getPageable(),
                pageResult.getTotalPages(),
                pageResult.getTotalElements(),
                pageResult.isLast(),
                pageResult.isFirst(),
                pageResult.getSize(),
                pageResult.getNumber(),
                pageResult.getSort(),
                pageResult.getNumberOfElements(),
                pageResult.isEmpty()
        );
    }

    public Income findById(Long id) {
        Optional<Income> optionalIncome = incomeRepository.findById(id);

        if (optionalIncome.isEmpty()) {
            throw new NotFoundException("Renda não encontrada");
        }

        return optionalIncome.get();
    }

    public void delete(Long id) {
        Income incomeToDelete = findById(id);

        incomeRepository.delete(incomeToDelete);
    }
}
