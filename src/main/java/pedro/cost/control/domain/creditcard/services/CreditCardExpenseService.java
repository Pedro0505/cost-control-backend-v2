package pedro.cost.control.domain.creditcard.services;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import pedro.cost.control.common.YearMonthSummary;
import pedro.cost.control.domain.creditcard.dtos.AvailableCreditCardDiscriminationYearMonth;
import pedro.cost.control.domain.creditcard.dtos.CreditCardDiscriminationMonthInfo;
import pedro.cost.control.domain.creditcard.dtos.CreditCardExpensesGroupedOutputDTO;
import pedro.cost.control.domain.creditcard.dtos.ExpenseByCategoryDTO;
import pedro.cost.control.domain.creditcard.dtos.ExpenseEvolutionDTO;
import pedro.cost.control.domain.creditcard.entities.CreditCardExpense;
import pedro.cost.control.domain.creditcard.repositories.CreditCardExpenseRepository;
import pedro.cost.control.utils.HandleNullablesUtils;

import java.time.YearMonth;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CreditCardExpenseService {
    private final CreditCardExpenseRepository creditCardExpenseRepository;
    private final CreditCardExpensesHandler creditCardExpensesHandler;

    public void saveAll(List<CreditCardExpense> creditCardExpense) {
        creditCardExpenseRepository.saveAll(creditCardExpense);
    }

    public void deleteAllByYearAndMonth(Integer year, Integer month) {
        creditCardExpenseRepository.deleteAllByInvoiceYearAndMonth(year, month);
    }

    public List<CreditCardExpense> getAllByYearAndMonthInvoice(Integer year, Integer month) {
        return creditCardExpenseRepository.findAllByYearAndMonthInvoice(year, month);
    }

    public List<CreditCardExpensesGroupedOutputDTO> getGroupedExpensesByEnterpriseByInvoiceYearAndMonth(
            Integer invoiceYear, Integer invoiceMonth
    ) {
        List<CreditCardExpense> creditCardExpenses = getAllByYearAndMonthInvoice(invoiceYear, invoiceMonth);

        return creditCardExpensesHandler.getGroupedExpensesByEnterprise(creditCardExpenses);
    }

    public List<AvailableCreditCardDiscriminationYearMonth> getAvailableCreditCardDiscriminationYearMonths() {
        List<YearMonthSummary> monthlyBalanceWithIncomeRelation = creditCardExpenseRepository.findAllDistinctAddedInvoices();

        return monthlyBalanceWithIncomeRelation
                .stream()
                .collect(Collectors.groupingBy(YearMonthSummary::getYear))
                .entrySet()
                .stream()
                .map(e -> {
                    List<CreditCardDiscriminationMonthInfo> monthByYearList = e.getValue().stream()
                            .map(j ->new CreditCardDiscriminationMonthInfo(j.getMonth()))
                            .sorted(Comparator.comparing(CreditCardDiscriminationMonthInfo::getValue))
                            .toList();

                    return new AvailableCreditCardDiscriminationYearMonth(e.getKey(), monthByYearList);
                })
                .sorted(Comparator.comparing(AvailableCreditCardDiscriminationYearMonth::getAvailableYear).reversed())
                .toList();
    }

    public void reprocessingCreditCardExpensesDescriptions() {
        List<CreditCardExpense> creditCardExpenses = creditCardExpenseRepository.findAll();

        List<CreditCardExpense> updatedCreditCardExpenses = creditCardExpensesHandler.updateCreditCardExpenseDescriptions(creditCardExpenses);

        creditCardExpenseRepository.saveAll(updatedCreditCardExpenses);
    }

    public List<ExpenseByCategoryDTO> getTotalByCategoryByInvoicePeriod(
            Integer invoiceStartYear,
            Integer invoiceStartMonth,
            Integer invoiceEndYear,
            Integer invoiceEndMonth
    ) {
        YearMonth now = YearMonth.now();
        YearMonth defaultStart = now.minusMonths(12);

        YearMonth start = YearMonth.of(
                HandleNullablesUtils.getValueOrDefault(invoiceStartYear, defaultStart.getYear()),
                HandleNullablesUtils.getValueOrDefault(invoiceStartMonth, defaultStart.getMonthValue())
        );

        YearMonth end = YearMonth.of(
                HandleNullablesUtils.getValueOrDefault(invoiceEndYear, now.getYear()),
                HandleNullablesUtils.getValueOrDefault(invoiceEndMonth, now.getMonthValue())
        );

        return creditCardExpenseRepository.findTotalByCategoryByInvoicePeriod(
                start.getYear(),
                start.getMonthValue(),
                end.getYear(),
                end.getMonthValue()
        );
    }

    public List<ExpenseEvolutionDTO> getTopEightExpensesEvolutionLastTwelveMonths() {
        YearMonth now = YearMonth.now();
        YearMonth start = now.minusMonths(11);

        List<String> topCategories = creditCardExpenseRepository.findTopCategoriesByInvoicePeriod(
                                start.getYear(),
                                start.getMonthValue(),
                                now.getYear(),
                                now.getMonthValue(),
                                PageRequest.of(0, 8)
                        ).stream()
                        .map(ExpenseByCategoryDTO::getCategory)
                        .toList();

        return creditCardExpenseRepository.findMonthlyEvolutionByCategories(
                topCategories,
                start.getYear(),
                start.getMonthValue(),
                now.getYear(),
                now.getMonthValue()
        );
    }
}
