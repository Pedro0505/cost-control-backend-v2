package pedro.cost.control.domain.balance.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pedro.cost.control.domain.balance.dtos.AvailableBalanceYearMonth;
import pedro.cost.control.domain.balance.services.MonthlyBalanceService;
import pedro.cost.control.security.CustomUserDetails;

import java.util.List;

@RestController
@RequestMapping("/api/v2/monthly-balance")
@RequiredArgsConstructor
public class MonthlyBalanceController {
    private final MonthlyBalanceService monthlyBalanceService;

    @GetMapping("/available-periods")
    public ResponseEntity<List<AvailableBalanceYearMonth>> getAllAvailablePeriod(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        List<AvailableBalanceYearMonth> allMonthlyBalanceWithIncomeRelation = monthlyBalanceService
                .getAllMonthlyBalanceWithIncomeRelation(userDetails.getId());

        return ResponseEntity.ok(allMonthlyBalanceWithIncomeRelation);
    }
}
