package pedro.cost.control.domain.creditcard.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pedro.cost.control.domain.creditcard.dtos.AvailableCreditCardDiscriminationYearMonth;
import pedro.cost.control.domain.creditcard.dtos.CreditCardExpensesGroupedOutputDTO;
import pedro.cost.control.domain.creditcard.dtos.ExpenseEvolutionDTO;
import pedro.cost.control.domain.creditcard.dtos.InvoiceSummaryByYearMonth;
import pedro.cost.control.domain.creditcard.services.CreditCardExpenseService;

import java.util.List;

@RestController
@RequestMapping("/api/v2/credit-card-expenses")
@RequiredArgsConstructor
public class CreditCardExpenseController {
    private final CreditCardExpenseService creditCardExpenseService;

    @GetMapping
    public ResponseEntity<List<CreditCardExpensesGroupedOutputDTO>> getGroupedExpensesByEnterprise(
            @RequestParam(name = "invoiceYear") Integer invoiceYear, @RequestParam(name = "invoiceMonth") Integer invoiceMonth
    ) {
        List<CreditCardExpensesGroupedOutputDTO> creditCardExpensesGrouped = creditCardExpenseService
                .getGroupedExpensesByEnterpriseByInvoiceYearAndMonth(invoiceYear, invoiceMonth);

        return ResponseEntity.ok(creditCardExpensesGrouped);
    }

    @GetMapping("/available-months")
    public ResponseEntity<List<AvailableCreditCardDiscriminationYearMonth>> getAvailableCreditCardDiscriminationYearMonths() {
        List<AvailableCreditCardDiscriminationYearMonth> creditCardExpensesGrouped = creditCardExpenseService
                .getAvailableCreditCardDiscriminationYearMonths();

        return ResponseEntity.ok(creditCardExpensesGrouped);
    }

    @GetMapping("/graph/total-amount-by-month")
    public ResponseEntity<List<InvoiceSummaryByYearMonth>> getTotalInvoiceAmountGroupedByYearMonth(
            @RequestParam(name = "invoiceStartYear", required = false) Integer invoiceStartYear,
            @RequestParam(name = "invoiceStartMonth", required = false) Integer invoiceStartMonth,
            @RequestParam(name = "invoiceEndYear", required = false) Integer invoiceEndYear,
            @RequestParam(name = "invoiceEndMonth", required = false) Integer invoiceEndMonth
    ) {
        List<InvoiceSummaryByYearMonth> expenseByCategory =
                creditCardExpenseService.getTotalInvoiceAmountGroupedByYearMonth(
                        invoiceStartYear,
                        invoiceStartMonth,
                        invoiceEndYear,
                        invoiceEndMonth
                );

        return ResponseEntity.ok(expenseByCategory);
    }

    @GetMapping("/graph/top-expenses-evolution")
    public ResponseEntity<List<ExpenseEvolutionDTO>> getTopExpensesEvolutionLast12Months() {
        List<ExpenseEvolutionDTO> evolution = creditCardExpenseService.getTopEightExpensesEvolutionLastTwelveMonths();

        return ResponseEntity.ok(evolution);
    }

    @PutMapping("/reprocessing-descriptions")
    public ResponseEntity<Void> reprocessingCreditCardExpensesDescriptions() {
        creditCardExpenseService.reprocessingCreditCardExpensesDescriptions();

        return ResponseEntity.ok().build();
    }
}
