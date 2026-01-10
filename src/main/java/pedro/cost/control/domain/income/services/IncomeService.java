package pedro.cost.control.domain.income.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import pedro.cost.control.common.LegacyPageResponse;
import pedro.cost.control.config.exceptions.BadRequestException;
import pedro.cost.control.config.exceptions.NotFoundException;
import pedro.cost.control.domain.balance.entities.MonthlyBalance;
import pedro.cost.control.domain.balance.services.MonthlyBalanceService;
import pedro.cost.control.domain.contract.dtos.ContractSummaryDTO;
import pedro.cost.control.domain.contract.entities.EmploymentContract;
import pedro.cost.control.domain.contract.entities.PjMonthlyWork;
import pedro.cost.control.domain.contract.enums.ContractType;
import pedro.cost.control.domain.contract.services.EmploymentContractService;
import pedro.cost.control.domain.contract.services.PjMonthlyWorkService;
import pedro.cost.control.domain.income.dtos.IncomeInputCreateDTO;
import pedro.cost.control.domain.income.dtos.IncomeOutputDTO;
import pedro.cost.control.domain.income.entities.Income;
import pedro.cost.control.domain.income.mapper.IncomeMapper;
import pedro.cost.control.domain.income.repositories.IncomeRepository;
import pedro.cost.control.domain.salary.services.SalaryCalculationService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class IncomeService {
    private final IncomeRepository incomeRepository;
    private final EmploymentContractService employmentContractService;
    private final SalaryCalculationService salaryCalculationService;
    private final MonthlyBalanceService monthlyBalanceService;
    private final PjMonthlyWorkService pjMonthlyWorkService;
    private final IncomeMapper incomeMapper;

    @Transactional
    public void createIncome(IncomeInputCreateDTO incomeInputCreateDTO) {
        Integer referenceYear = incomeInputCreateDTO.getReferenceDate().getYear();
        Integer referenceMonth = incomeInputCreateDTO.getReferenceDate().getMonthValue();
        LocalDate referenceDateLastMonth = incomeInputCreateDTO.getReferenceDate().minusMonths(1);

        ContractSummaryDTO openedEmploymentContract = employmentContractService.getOpenedEmploymentContract(referenceDateLastMonth);
        MonthlyBalance incomeMonthlyBalance = monthlyBalanceService.getOrCreateMonthlyBalance(referenceYear, referenceMonth);
        BigDecimal salary = determineSalaryAmount(incomeInputCreateDTO, openedEmploymentContract);

        handlePjMonthlyWork(incomeInputCreateDTO, openedEmploymentContract);

        Income incomeCreated = createIncomeObject(
                incomeInputCreateDTO, incomeMonthlyBalance, openedEmploymentContract.getEmploymentContract(), salary
        );

        save(incomeCreated);
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

        Optional<PjMonthlyWork> pjMonthlyWorkLinkedWithIncome = pjMonthlyWorkService.getPjMonthlyWorkLinkedWithIncomeId(incomeToDelete.getId());
        pjMonthlyWorkLinkedWithIncome.ifPresent(pjMonthlyWorkService::delete);

        incomeRepository.delete(incomeToDelete);
    }

    public void save(Income income) {
        incomeRepository.save(income);
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

    public BigDecimal getTotalIncomeByYearAndMonth(Integer year, Integer month) {
        return incomeRepository.sumAmountByMonth(year, month)
                .orElseThrow(() -> new NotFoundException(
                        "Não foi encontradas entradas para o mês " + month + " e ano " + year
                ));
    }

    private BigDecimal determineSalaryAmount(IncomeInputCreateDTO incomeInputCreateDTO, ContractSummaryDTO contractSummaryDTO) {
        Boolean isUserAmountDefinition = incomeInputCreateDTO.getAmount() == null || incomeInputCreateDTO.getAmount().equals(BigDecimal.ZERO);
        BigDecimal salaryAmount = salaryCalculationService.calculateSalary(contractSummaryDTO, incomeInputCreateDTO.getBusinessDays());
        Optional<BigDecimal> optionalAmount = Optional.ofNullable(incomeInputCreateDTO.getAmount());

        if (Boolean.TRUE.equals(isUserAmountDefinition)) {
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

    private void handlePjMonthlyWork(IncomeInputCreateDTO incomeInputCreateDTO, ContractSummaryDTO openedEmploymentContract) {
        boolean isContractPj = openedEmploymentContract.getContractType().equals(ContractType.PJ.name());
        Integer businessDays = incomeInputCreateDTO.getBusinessDays();

        if (isContractPj) {
            if (businessDays == null || businessDays.equals(0)) {
                throw new BadRequestException("Rendas PJ devem ter os dias úteis preenchidos");
            }

            pjMonthlyWorkService.createPjMonthlyWork(incomeInputCreateDTO.getReferenceDate(), businessDays);
        }
    }
}
