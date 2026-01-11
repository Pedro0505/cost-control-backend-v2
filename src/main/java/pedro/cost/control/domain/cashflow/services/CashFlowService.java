package pedro.cost.control.domain.cashflow.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pedro.cost.control.domain.cashflow.dtos.BalanceSummaryOutputDTO;
import pedro.cost.control.domain.cost.dtos.CostOutputDTO;
import pedro.cost.control.domain.cost.services.CostService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CashFlowService {
    private final CostService costService;
    private final FinancialBalanceService financialBalanceService;

    public BalanceSummaryOutputDTO getFinancialSummaryByMonth(Integer year, Integer month) {
        List<CostOutputDTO> costs = costService.getAllCostByYearMonth(year, month);

        return financialBalanceService.calculateFinancialSummaryByMonth(costs, year, month);
    }
}
