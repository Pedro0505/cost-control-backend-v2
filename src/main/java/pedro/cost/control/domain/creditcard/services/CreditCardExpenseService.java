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
import pedro.cost.control.domain.creditcard.dtos.InvoiceSummaryByYearMonth;
import pedro.cost.control.domain.creditcard.entities.CreditCardExpense;
import pedro.cost.control.domain.creditcard.records.CreditCardExpensePercentageResponse;
import pedro.cost.control.domain.creditcard.records.CreditCardInstallmentPercentageResponse;
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

    public void deleteAllByYearAndMonth(Integer year, Integer month, Long userId) {
        creditCardExpenseRepository.deleteAllByInvoiceYearAndMonth(year, month, userId);
    }

    public List<CreditCardExpense> getAllByYearAndMonthInvoice(Integer year, Integer month, Long userId) {
        return creditCardExpenseRepository.findAllByYearAndMonthInvoice(year, month, userId);
    }

    public List<CreditCardExpensesGroupedOutputDTO> getGroupedExpensesByEnterpriseByInvoiceYearAndMonth(
            Integer invoiceYear, Integer invoiceMonth, Long userId
    ) {
        List<CreditCardExpense> creditCardExpenses = getAllByYearAndMonthInvoice(invoiceYear, invoiceMonth, userId);

        return creditCardExpensesHandler.getGroupedExpensesByEnterprise(creditCardExpenses);
    }

    public List<AvailableCreditCardDiscriminationYearMonth> getAvailableCreditCardDiscriminationYearMonths(Long userId) {
        List<YearMonthSummary> monthlyBalanceWithIncomeRelation = creditCardExpenseRepository.findAllDistinctAddedInvoices(
                userId
        );

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

    public void reprocessingCreditCardExpensesDescriptions(Long userId) {
        List<CreditCardExpense> creditCardExpenses = creditCardExpenseRepository.findAllByUserId(userId);

        List<CreditCardExpense> updatedCreditCardExpenses = creditCardExpensesHandler.updateCreditCardExpenseDescriptions(creditCardExpenses);

        creditCardExpenseRepository.saveAll(updatedCreditCardExpenses);
    }

    public List<InvoiceSummaryByYearMonth> getTotalInvoiceAmountGroupedByYearMonth(
            Integer invoiceStartYear,
            Integer invoiceStartMonth,
            Integer invoiceEndYear,
            Integer invoiceEndMonth,
            Long userId
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

        return creditCardExpenseRepository.findTotalInvoiceAmountGroupedByYearMonth(
                start.getYear(),
                start.getMonthValue(),
                end.getYear(),
                end.getMonthValue(),
                userId
        );
    }

    public List<ExpenseEvolutionDTO> getTopEightExpensesEvolutionLastTwelveMonths(Long userId) {
        YearMonth now = YearMonth.now();
        YearMonth start = now.minusMonths(11);

        List<String> topCategories = creditCardExpenseRepository.findTopCategoriesByInvoicePeriod(
                                start.getYear(),
                                start.getMonthValue(),
                                now.getYear(),
                                now.getMonthValue(),
                                userId,
                                PageRequest.of(0, 8)
                        ).stream()
                        .map(ExpenseByCategoryDTO::getCategory)
                        .toList();

        return creditCardExpenseRepository.findMonthlyEvolutionByCategories(
                topCategories,
                start.getYear(),
                start.getMonthValue(),
                now.getYear(),
                now.getMonthValue(),
                userId
        );
    }

    public List<CreditCardExpensePercentageResponse> getPercentageByMonthAndYear(
            Integer month,
            Integer year,
            Long userId
    ) {
        return creditCardExpenseRepository.findPercentageByMonthAndYear(month, year, userId);
    }

    public CreditCardInstallmentPercentageResponse getInstallmentPercentageByMonthAndYear(
            Integer month,
            Integer year,
            Long userId
    ) {
        return creditCardExpenseRepository.findInstallmentPercentageByMonthAndYear(month, year, userId);
    }
}
