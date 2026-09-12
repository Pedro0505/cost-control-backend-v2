package pedro.cost.control.domain.creditcard.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pedro.cost.control.domain.creditcard.dtos.AvailableCreditCardDiscriminationYearMonth;
import pedro.cost.control.domain.creditcard.dtos.CreditCardExpensesGroupedOutputDTO;
import pedro.cost.control.domain.creditcard.dtos.ExpenseEvolutionDTO;
import pedro.cost.control.domain.creditcard.dtos.InvoiceSummaryByYearMonth;
import pedro.cost.control.domain.creditcard.records.CreditCardExpensePercentageResponse;
import pedro.cost.control.domain.creditcard.records.CreditCardInstallmentPercentageResponse;
import pedro.cost.control.domain.creditcard.services.CreditCardExpenseService;
import pedro.cost.control.security.CustomUserDetails;

import java.util.List;

@RestController
@RequestMapping("/api/v2/credit-card-expenses")
@RequiredArgsConstructor
public class CreditCardExpenseController {
    private final CreditCardExpenseService creditCardExpenseService;

    @GetMapping
    public ResponseEntity<List<CreditCardExpensesGroupedOutputDTO>> getGroupedExpensesByEnterprise(
            @RequestParam(name = "invoiceYear") Integer invoiceYear,
            @RequestParam(name = "invoiceMonth") Integer invoiceMonth,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        List<CreditCardExpensesGroupedOutputDTO> creditCardExpensesGrouped = creditCardExpenseService
                .getGroupedExpensesByEnterpriseByInvoiceYearAndMonth(invoiceYear, invoiceMonth, userDetails.getId());

        return ResponseEntity.ok(creditCardExpensesGrouped);
    }

    @GetMapping("/available-months")
    public ResponseEntity<List<AvailableCreditCardDiscriminationYearMonth>> getAvailableCreditCardDiscriminationYearMonths(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        List<AvailableCreditCardDiscriminationYearMonth> creditCardExpensesGrouped = creditCardExpenseService
                .getAvailableCreditCardDiscriminationYearMonths(userDetails.getId());

        return ResponseEntity.ok(creditCardExpensesGrouped);
    }

    @GetMapping("/graph/total-amount-by-month")
    public ResponseEntity<List<InvoiceSummaryByYearMonth>> getTotalInvoiceAmountGroupedByYearMonth(
            @RequestParam(name = "invoiceStartYear", required = false) Integer invoiceStartYear,
            @RequestParam(name = "invoiceStartMonth", required = false) Integer invoiceStartMonth,
            @RequestParam(name = "invoiceEndYear", required = false) Integer invoiceEndYear,
            @RequestParam(name = "invoiceEndMonth", required = false) Integer invoiceEndMonth,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        List<InvoiceSummaryByYearMonth> expenseByCategory =
                creditCardExpenseService.getTotalInvoiceAmountGroupedByYearMonth(
                        invoiceStartYear,
                        invoiceStartMonth,
                        invoiceEndYear,
                        invoiceEndMonth,
                        userDetails.getId()
                );

        return ResponseEntity.ok(expenseByCategory);
    }

    @GetMapping("/graph/top-expenses-evolution")
    public ResponseEntity<List<ExpenseEvolutionDTO>> getTopExpensesEvolutionLast12Months(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        List<ExpenseEvolutionDTO> evolution = creditCardExpenseService.getTopEightExpensesEvolutionLastTwelveMonths(userDetails.getId());

        return ResponseEntity.ok(evolution);
    }

    @PutMapping("/reprocessing-descriptions")
    public ResponseEntity<Void> reprocessingCreditCardExpensesDescriptions(@AuthenticationPrincipal CustomUserDetails userDetails) {
        creditCardExpenseService.reprocessingCreditCardExpensesDescriptions(userDetails.getId());

        return ResponseEntity.ok().build();
    }

    @GetMapping("/percentage")
    public List<CreditCardExpensePercentageResponse> getPercentage(
            @RequestParam Integer month,
            @RequestParam Integer year,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return creditCardExpenseService.getPercentageByMonthAndYear(month, year, userDetails.getId());
    }

    @GetMapping("/installment-percentage")
    public CreditCardInstallmentPercentageResponse getInstallmentPercentage(
            @RequestParam Integer month,
            @RequestParam Integer year,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return creditCardExpenseService.getInstallmentPercentageByMonthAndYear(month, year, userDetails.getId());
    }
}
