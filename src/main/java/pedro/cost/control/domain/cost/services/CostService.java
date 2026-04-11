package pedro.cost.control.domain.cost.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pedro.cost.control.config.exceptions.ResourceNotFoundException;
import pedro.cost.control.domain.balance.entities.MonthlyBalance;
import pedro.cost.control.domain.cost.assemblers.CostSummaryAssembler;
import pedro.cost.control.domain.cost.contexts.CostCreationContext;
import pedro.cost.control.domain.cost.dtos.CostOutputDTO;
import pedro.cost.control.domain.cost.dtos.CostSummaryOutputDTO;
import pedro.cost.control.domain.cost.dtos.CreateCostInputDTO;
import pedro.cost.control.domain.cost.dtos.ImportCostRecurrentInputDTO;
import pedro.cost.control.domain.cost.dtos.PreviewRecurrentCostsForImportOutPutDTO;
import pedro.cost.control.domain.cost.dtos.UpdateCostInputDTO;
import pedro.cost.control.domain.cost.entities.Cost;
import pedro.cost.control.domain.cost.factories.CostCreationContextFactory;
import pedro.cost.control.domain.cost.factories.CostFactory;
import pedro.cost.control.domain.cost.factories.CostUpdateAmountFactory;
import pedro.cost.control.domain.cost.mappers.CostMapper;
import pedro.cost.control.domain.cost.repositories.CostRepository;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CostService {
    private final CostRepository costRepository;
    private final CostCreationContextFactory costCreationContextFactory;
    private final CostSummaryAssembler costSummaryAssembler;
    private final RecurrentCostImporterService importRecurrentCosts;
    private final CostFactory costFactory;
    private final CostUpdateAmountFactory costUpdateAmountFactory;
    private final CostMapper costMapper;

    @Transactional
    public CostSummaryOutputDTO create(CreateCostInputDTO dto) {
        CostCreationContext context = costCreationContextFactory.create(dto);
        Cost cost = costFactory.create(context);
        Cost saved = costRepository.save(cost);

        return costSummaryAssembler.assemble(saved);
    }

    @Transactional
    public CostSummaryOutputDTO delete(Long id) {
        Cost cost = findById(id);

        costRepository.delete(cost);
        return costSummaryAssembler.assemble(cost);
    }

    @Transactional
    public CostSummaryOutputDTO update(Long id, UpdateCostInputDTO updateCostInputDTO) {
        Cost cost = findById(id);
        BigDecimal updateAmount = costUpdateAmountFactory.getUpdateAmount(cost, updateCostInputDTO);

        cost.setAmount(updateAmount);
        cost.setPercentage(updateCostInputDTO.getPercentage());
        cost.setDescription(updateCostInputDTO.getDescription());
        cost.setPaid(updateCostInputDTO.getPaid());
        cost.setRecurrent(updateCostInputDTO.getRecurrent());
        cost.setCalculationType(updateCostInputDTO.getCalculationType());

        costRepository.save(cost);

        return costSummaryAssembler.assemble(cost);
    }

    public Cost findById(Long id) {
        return costRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Custo não encontrado"));
    }

    public List<CostOutputDTO> getAllCostByYearMonth(Integer year, Integer month) {
        return costRepository.findAllCostByYearMonth(year, month);
    }

    @Transactional
    public void importRecurrentCosts(ImportCostRecurrentInputDTO dto) {
        MonthlyBalance targetBalance = costCreationContextFactory.getMonthlyBalanceByYearAndMonth(
                dto.getTargetReferenceYear(), dto.getTargetReferenceMonth()
        );

        importRecurrentCosts.importRecurrentCosts(dto, targetBalance);
    }


    public List<PreviewRecurrentCostsForImportOutPutDTO> getPreviewRecurrentCostsForImport(
            Integer sourceReferenceYear,
            Integer sourceReferenceMonth,
            Integer targetReferenceYear,
            Integer targetReferenceMonth
    ) {
        MonthlyBalance targetBalance = costCreationContextFactory.getMonthlyBalanceByYearAndMonth(
                targetReferenceYear, targetReferenceMonth
        );

        List<Cost> recalculatedCostFromTarget = importRecurrentCosts.getRecalculatedCostFromTarget(
                sourceReferenceYear,
                sourceReferenceMonth,
                targetReferenceYear,
                targetReferenceMonth,
                targetBalance
        );

        List<Cost> recalculatedCostFromTargetRecurrent = recalculatedCostFromTarget.stream()
                .filter(Cost::getRecurrent)
                .toList();

        return costMapper.costToPreviewRecurrentCostsForImportOutPutDTO(recalculatedCostFromTargetRecurrent);
    }
}
