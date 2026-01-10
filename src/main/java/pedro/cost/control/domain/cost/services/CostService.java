package pedro.cost.control.domain.cost.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pedro.cost.control.config.exceptions.NotFoundException;
import pedro.cost.control.config.exceptions.ResourceNotFoundException;
import pedro.cost.control.domain.balance.entities.MonthlyBalance;
import pedro.cost.control.domain.balance.services.MonthlyBalanceService;
import pedro.cost.control.domain.cashflow.dtos.BalanceSummaryOutputDTO;
import pedro.cost.control.domain.cashflow.services.FinancialBalanceService;
import pedro.cost.control.domain.cost.calculate.services.CostCalculationService;
import pedro.cost.control.domain.cost.dtos.CostOutputDTO;
import pedro.cost.control.domain.cost.dtos.CostSummaryOutputDTO;
import pedro.cost.control.domain.cost.dtos.CreateCostInputDTO;
import pedro.cost.control.domain.cost.dtos.ImportCostRecurrentInputDTO;
import pedro.cost.control.domain.cost.dtos.UpdateCostInputDTO;
import pedro.cost.control.domain.cost.entities.Cost;
import pedro.cost.control.domain.cost.mappers.CostMapper;
import pedro.cost.control.domain.cost.repositories.CostRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CostService {
    private final CostRepository costRepository;
    private final MonthlyBalanceService monthlyBalanceService;
    private final CostCalculationService costCalculationService;
    private final FinancialBalanceService financialBalanceService;
    private final CostMapper costMapper;

    @Transactional
    public CostSummaryOutputDTO create(CreateCostInputDTO createCostInputDTO) {
        Integer referenceYear = createCostInputDTO.getReferenceYear();
        Integer referenceMonth = createCostInputDTO.getReferenceMonth();

        MonthlyBalance monthlyBalance = getMonthlyBalanceByCost(referenceYear, referenceMonth);
        BigDecimal calculatedAmount = costCalculationService.calculateAmount(createCostInputDTO);
        Cost newCostObject = createCostObject(createCostInputDTO, calculatedAmount, monthlyBalance);

        Cost createdCost = costRepository.save(newCostObject);

        return appendMoneySummary(createdCost, referenceYear, referenceMonth);
    }

    @Transactional
    public CostSummaryOutputDTO delete(Long id) {
        Cost cost = findById(id);

        costRepository.delete(cost);

        return appendMoneySummary(cost, cost.getMonthlyBalance().getReferenceYear(), cost.getMonthlyBalance().getReferenceMonth());

    }

    @Transactional
    public CostSummaryOutputDTO update(Long id, UpdateCostInputDTO updateCostInputDTO) {
        Cost cost = findById(id);

        cost.setAmount(updateCostInputDTO.getAmount());
        cost.setPercentage(updateCostInputDTO.getPercentage());
        cost.setDescription(updateCostInputDTO.getDescription());
        cost.setPaid(updateCostInputDTO.getPaid());
        cost.setRecurrent(updateCostInputDTO.getRecurrent());
        cost.setCalculationType(updateCostInputDTO.getCalculationType());

        costRepository.save(cost);

        return appendMoneySummary(cost, cost.getMonthlyBalance().getReferenceYear(), cost.getMonthlyBalance().getReferenceMonth());
    }

    public Cost findById(Long id) {
        Optional<Cost> costOptional = costRepository.findById(id);

        if (costOptional.isEmpty()) {
            throw new ResourceNotFoundException("Custo não encontrado");
        }

        return costOptional.get();
    }

    public List<CostOutputDTO> getAllCostByYearMonth(Integer year, Integer month) {
        return costRepository.findAllCostByYearMonth(year, month);
    }

    public void importRecurrentCosts(ImportCostRecurrentInputDTO importCostRecurrentInputDTO) {
        List<Cost> sourceRecurrentCost = costRepository.findAllRecurrentCostByYearMonth(
          importCostRecurrentInputDTO.getSourceReferenceYear(), importCostRecurrentInputDTO.getSourceReferenceMonth()
        );

        MonthlyBalance targetMonthlyBalance = getMonthlyBalanceByCost(
                importCostRecurrentInputDTO.getTargetReferenceYear(), importCostRecurrentInputDTO.getTargetReferenceMonth()
        );

        List<Cost> newCosts = createNewCosts(sourceRecurrentCost, targetMonthlyBalance);

        saveAll(newCosts);
    }

    public void saveAll(List<Cost> costs) {
        costRepository.saveAll(costs);
    }

    private BalanceSummaryOutputDTO getBalanceSummary(Integer referenceYear, Integer referenceMonth) {
        List<CostOutputDTO> updatedCosts = getAllCostByYearMonth(referenceYear, referenceMonth);

        return financialBalanceService.calculateFinancialSummaryByMonth(updatedCosts, referenceYear, referenceMonth);
    }

    private MonthlyBalance getMonthlyBalanceByCost(Integer referenceYear, Integer referenceMonth) {
        Optional<MonthlyBalance> monthlyBalanceOptional = monthlyBalanceService.getMonthlyBalanceByYearAndMonth(
                referenceYear, referenceMonth
        );

        return monthlyBalanceOptional.orElseThrow(() -> new NotFoundException(
                "Não foi encontrado o mês " + referenceMonth + " e ano " + referenceYear
        ));
    }

    private Cost createCostObject(CreateCostInputDTO createCostInputDTO, BigDecimal calculatedAmount, MonthlyBalance monthlyBalance) {
        return Cost.builder()
                .amount(calculatedAmount)
                .percentage(createCostInputDTO.getPercentage())
                .calculationType(createCostInputDTO.getCalculationType())
                .description(createCostInputDTO.getDescription())
                .recurrent(createCostInputDTO.getRecurrent())
                .monthlyBalance(monthlyBalance)
                .paid(createCostInputDTO.getPaid())
                .build();
    }

    private List<Cost> createNewCosts(List<Cost> costs, MonthlyBalance monthlyBalance) {
        return costs
                .stream()
                .map(e -> Cost.builder()
                        .amount(e.getAmount())
                        .percentage(e.getPercentage())
                        .calculationType(e.getCalculationType())
                        .description(e.getDescription())
                        .recurrent(e.getRecurrent())
                        .monthlyBalance(monthlyBalance)
                        .paid(e.getPaid())
                        .build()
                )
                .toList();
    }

    private CostSummaryOutputDTO appendMoneySummary(Cost createdCost, Integer referenceYear, Integer referenceMonth) {
        CostSummaryOutputDTO costOutputDTO = costMapper.costToCostSummaryOutputDTO(createdCost);

        BalanceSummaryOutputDTO balanceSummary = getBalanceSummary(referenceYear, referenceMonth);

        costOutputDTO.setMoneySummary(balanceSummary.getMoneySummary());

        return costOutputDTO;
    }
}
