package pedro.cost.control.domain.cost.assemblers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pedro.cost.control.domain.cashflow.dtos.BalanceSummaryOutputDTO;
import pedro.cost.control.domain.cashflow.services.FinancialBalanceService;
import pedro.cost.control.domain.cost.dtos.CostOutputDTO;
import pedro.cost.control.domain.cost.dtos.CostSummaryOutputDTO;
import pedro.cost.control.domain.cost.entities.Cost;
import pedro.cost.control.domain.cost.mappers.CostMapper;
import pedro.cost.control.domain.cost.repositories.CostRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CostSummaryAssembler {

    private final FinancialBalanceService financialBalanceService;
    private final CostRepository costRepository;
    private final CostMapper costMapper;

    public CostSummaryOutputDTO assemble(Cost cost) {
        Integer year = cost.getMonthlyBalance().getReferenceYear();
        Integer month = cost.getMonthlyBalance().getReferenceMonth();

        CostSummaryOutputDTO dto = costMapper.costToCostSummaryOutputDTO(cost);

        List<CostOutputDTO> costs = costRepository.findAllCostByYearMonth(year, month);
        BalanceSummaryOutputDTO summary = financialBalanceService.calculateFinancialSummaryByMonth(costs, year, month);

        dto.setMoneySummary(summary.getMoneySummary());

        return dto;
    }
}
