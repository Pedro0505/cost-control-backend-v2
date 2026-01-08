package pedro.cost.control.domain.cashflow.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pedro.cost.control.domain.cashflow.dtos.BalanceSummaryOutputDTO;
import pedro.cost.control.domain.cashflow.dtos.MoneySummaryOutputDTO;
import pedro.cost.control.domain.cost.dtos.CostOutputDTO;
import pedro.cost.control.domain.income.services.IncomeService;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FinancialBalanceService {
    private final IncomeService incomeService;

    public BalanceSummaryOutputDTO calculateFinancialSummaryByMonth(List<CostOutputDTO> costs, Integer year, Integer month) {
        BigDecimal income = incomeService.getTotalIncomeByYearAndMonth(year, month);
        BigDecimal expense = calculateExpense(costs);
        BigDecimal balance = calculateBalance(income, expense);

        return BalanceSummaryOutputDTO.builder()
                .moneySummary(buildMoneySummary(balance, expense, income))
                .costs(costs)
                .build();
    }

    private BigDecimal calculateExpense(List<CostOutputDTO> costs) {
        return costs.stream().map(CostOutputDTO::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateBalance(BigDecimal income, BigDecimal expense) {
        return income.subtract(expense);
    }

    private MoneySummaryOutputDTO buildMoneySummary(BigDecimal balance, BigDecimal expense, BigDecimal income) {
        return MoneySummaryOutputDTO.builder()
                .balance(balance)
                .expense(expense)
                .income(income)
                .build();
    }
}
