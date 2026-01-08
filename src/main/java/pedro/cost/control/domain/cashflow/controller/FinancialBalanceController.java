package pedro.cost.control.domain.cashflow.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pedro.cost.control.domain.cashflow.dtos.BalanceSummaryOutputDTO;
import pedro.cost.control.domain.cashflow.services.CashFlowService;

@RestController
@RequestMapping("/api/v2/financial-balance")
@RequiredArgsConstructor
public class FinancialBalanceController {
    private final CashFlowService cashFlowService;

    @GetMapping("/month")
    public ResponseEntity<BalanceSummaryOutputDTO> financialBalanceOfYearMonth(@RequestParam(name = "year") Integer year,
                                                                               @RequestParam(name = "month") Integer month) {
        BalanceSummaryOutputDTO balanceSummary = cashFlowService.getFinancialSummaryByMonth(year, month);

        return ResponseEntity.ok().body(balanceSummary);
    }
}
