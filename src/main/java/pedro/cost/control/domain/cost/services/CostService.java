package pedro.cost.control.domain.cost.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pedro.cost.control.config.exceptions.NotFoundException;
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
import pedro.cost.control.security.CustomUserDetails;

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
    public CostSummaryOutputDTO create(CreateCostInputDTO dto, CustomUserDetails user) {
        CostCreationContext context = costCreationContextFactory.create(dto, user.getId());
        Cost cost = costFactory.create(context, user.getUser());
        Cost saved = costRepository.save(cost);

        return costSummaryAssembler.assemble(saved, user.getId());
    }

    @Transactional
    public CostSummaryOutputDTO delete(Long id, Long userId) {
        Cost cost = findCostByIdAndUserId(id, userId);

        int deletedRows = costRepository.deleteCostByIdAndUserId(cost.getId(), userId);

        if (deletedRows == 0) {
            throw new NotFoundException("Custo não encontrado");
        }

        return costSummaryAssembler.assemble(cost, userId);
    }

    @Transactional
    public CostSummaryOutputDTO update(Long id, UpdateCostInputDTO updateCostInputDTO, Long userId) {
        Cost cost = findCostByIdAndUserId(id, userId);
        BigDecimal updateAmount = costUpdateAmountFactory.getUpdateAmount(cost, updateCostInputDTO, userId);

        cost.setAmount(updateAmount);
        cost.setPercentage(updateCostInputDTO.getPercentage());
        cost.setDescription(updateCostInputDTO.getDescription());
        cost.setPaid(updateCostInputDTO.getPaid());
        cost.setRecurrent(updateCostInputDTO.getRecurrent());
        cost.setCalculationType(updateCostInputDTO.getCalculationType());

        costRepository.save(cost);

        return costSummaryAssembler.assemble(cost, userId);
    }

    public Cost findCostByIdAndUserId(Long id, Long userId) {
        return costRepository.findCostByIdAndUserId(id, userId).orElseThrow(() -> new ResourceNotFoundException("Custo não encontrado"));
    }

    public List<CostOutputDTO> getAllCostByYearMonth(Integer year, Integer month, Long userId) {
        return costRepository.findAllCostByYearMonth(year, month, userId);
    }

    @Transactional
    public void importRecurrentCosts(ImportCostRecurrentInputDTO dto, Long userId) {
        MonthlyBalance targetBalance = costCreationContextFactory.getMonthlyBalanceByYearAndMonth(
                dto.getTargetReferenceYear(), dto.getTargetReferenceMonth(), userId
        );

        importRecurrentCosts.importRecurrentCosts(dto, targetBalance, userId);
    }

    public List<PreviewRecurrentCostsForImportOutPutDTO> getPreviewRecurrentCostsForImport(
            Integer sourceReferenceYear,
            Integer sourceReferenceMonth,
            Integer targetReferenceYear,
            Integer targetReferenceMonth,
            Long userId
    ) {
        MonthlyBalance targetBalance = costCreationContextFactory.getMonthlyBalanceByYearAndMonth(
                targetReferenceYear, targetReferenceMonth, userId
        );

        List<Cost> recalculatedCostFromTarget = importRecurrentCosts.getRecalculatedCostFromTarget(
                sourceReferenceYear,
                sourceReferenceMonth,
                targetReferenceYear,
                targetReferenceMonth,
                targetBalance,
                userId
        );

        List<Cost> recalculatedCostFromTargetRecurrent = recalculatedCostFromTarget.stream()
                .filter(Cost::getRecurrent)
                .toList();

        return costMapper.costToPreviewRecurrentCostsForImportOutPutDTO(recalculatedCostFromTargetRecurrent);
    }
}
