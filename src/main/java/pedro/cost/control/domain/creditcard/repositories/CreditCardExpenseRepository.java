package pedro.cost.control.domain.creditcard.repositories;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pedro.cost.control.common.YearMonthSummary;
import pedro.cost.control.domain.creditcard.dtos.ExpenseByCategoryDTO;
import pedro.cost.control.domain.creditcard.dtos.ExpenseEvolutionDTO;
import pedro.cost.control.domain.creditcard.dtos.InvoiceSummaryByYearMonth;
import pedro.cost.control.domain.creditcard.entities.CreditCardExpense;
import pedro.cost.control.domain.creditcard.records.CreditCardExpensePercentageResponse;
import pedro.cost.control.domain.creditcard.records.CreditCardInstallmentPercentageResponse;

import java.util.List;

@Repository
public interface CreditCardExpenseRepository extends JpaRepository<CreditCardExpense, Long>  {
    @Transactional
    @Modifying
    @Query("""
        DELETE FROM CreditCardExpense cre
        WHERE cre.invoiceReferenceYear = :year
        AND cre.invoiceReferenceMonth = :month
    """)
    void deleteAllByInvoiceYearAndMonth(@Param("year") Integer year, @Param("month") Integer month);

    @Query("""
        SELECT cce FROM CreditCardExpense cce
        WHERE cce.invoiceReferenceYear = :year AND cce.invoiceReferenceMonth = :month
    """)
    List<CreditCardExpense> findAllByYearAndMonthInvoice(@Param("year") Integer year, @Param("month") Integer month);

    @Query("""
        SELECT DISTINCT new pedro.cost.control.common.YearMonthSummary(cce.invoiceReferenceYear, cce.invoiceReferenceMonth)
        FROM CreditCardExpense cce
    """)
    List<YearMonthSummary> findAllDistinctAddedInvoices();

    @Query("""
        SELECT new pedro.cost.control.domain.creditcard.dtos.InvoiceSummaryByYearMonth(
            c.invoiceReferenceMonth,
            c.invoiceReferenceYear,
            SUM(c.amount)
        )
        FROM CreditCardExpense c
        WHERE c.normalizedDescription IS NOT NULL
          AND (
                c.invoiceReferenceYear > :startYear
                OR (c.invoiceReferenceYear = :startYear AND c.invoiceReferenceMonth >= :startMonth)
              )
          AND (
                c.invoiceReferenceYear < :endYear
                OR (c.invoiceReferenceYear = :endYear AND c.invoiceReferenceMonth <= :endMonth)
              )
        GROUP BY c.invoiceReferenceYear, c.invoiceReferenceMonth
        ORDER BY c.invoiceReferenceYear, c.invoiceReferenceMonth
    """)
    List<InvoiceSummaryByYearMonth> findTotalInvoiceAmountGroupedByYearMonth(
            @Param("startYear") Integer startYear,
            @Param("startMonth") Integer startMonth,
            @Param("endYear") Integer endYear,
            @Param("endMonth") Integer endMonth
    );

    @Query("""
        SELECT new pedro.cost.control.domain.creditcard.dtos.ExpenseByCategoryDTO(
            c.normalizedDescription,
            SUM(c.amount)
        )
        FROM CreditCardExpense c
        WHERE c.normalizedDescription IS NOT NULL
          AND (
                c.invoiceReferenceYear > :startYear
                OR (c.invoiceReferenceYear = :startYear AND c.invoiceReferenceMonth >= :startMonth)
              )
          AND (
                c.invoiceReferenceYear < :endYear
                OR (c.invoiceReferenceYear = :endYear AND c.invoiceReferenceMonth <= :endMonth)
              )
        GROUP BY c.normalizedDescription
        ORDER BY SUM(c.amount) DESC
    """)
    List<ExpenseByCategoryDTO> findTopCategoriesByInvoicePeriod(
            @Param("startYear") Integer startYear,
            @Param("startMonth") Integer startMonth,
            @Param("endYear") Integer endYear,
            @Param("endMonth") Integer endMonth,
            PageRequest pageable
    );

    @Query("""
        SELECT new pedro.cost.control.domain.creditcard.dtos.ExpenseEvolutionDTO(
            c.normalizedDescription,
            c.invoiceReferenceYear,
            c.invoiceReferenceMonth,
            SUM(c.amount)
        )
        FROM CreditCardExpense c
        WHERE c.normalizedDescription IN :categories
            AND (
                c.invoiceReferenceYear > :startYear
                OR (c.invoiceReferenceYear = :startYear AND c.invoiceReferenceMonth >= :startMonth)
            )
            AND (
                c.invoiceReferenceYear < :endYear
                OR (c.invoiceReferenceYear = :endYear AND c.invoiceReferenceMonth <= :endMonth)
            )
        GROUP BY
            c.normalizedDescription,
            c.invoiceReferenceYear,
            c.invoiceReferenceMonth
        ORDER BY
            c.invoiceReferenceYear,
            c.invoiceReferenceMonth
    """)
    List<ExpenseEvolutionDTO> findMonthlyEvolutionByCategories(
            @Param("categories") List<String> categories,
            @Param("startYear") Integer startYear,
            @Param("startMonth") Integer startMonth,
            @Param("endYear") Integer endYear,
            @Param("endMonth") Integer endMonth
    );

    @Query("""
        SELECT new pedro.cost.control.domain.creditcard.records.CreditCardExpensePercentageResponse(
            e.invoiceReferenceMonth,
            e.invoiceReferenceYear,
            e.normalizedDescription,
            CAST(
                (SUM(e.amount) * 100) /
                (
                    SELECT SUM(e2.amount)
                    FROM CreditCardExpense e2
                    WHERE e2.invoiceReferenceMonth = :month
                    AND e2.invoiceReferenceYear = :year
                )
                AS BigDecimal
            )
        )
        FROM CreditCardExpense e
        WHERE e.invoiceReferenceMonth = :month
        AND e.invoiceReferenceYear = :year
        GROUP BY
            e.invoiceReferenceMonth,
            e.invoiceReferenceYear,
            e.normalizedDescription
        ORDER BY SUM(e.amount) DESC
    """)
    List<CreditCardExpensePercentageResponse> findPercentageByMonthAndYear(
            @Param("month") Integer month,
            @Param("year") Integer year
    );

    @Query("""
        SELECT new pedro.cost.control.domain.creditcard.records.CreditCardInstallmentPercentageResponse(
            e.invoiceReferenceMonth,
            e.invoiceReferenceYear,
    
            CAST(
                (
                    SUM(
                        CASE
                            WHEN e.installment = true THEN e.amount
                            ELSE 0
                        END
                    ) * 100
                ) / SUM(e.amount)
                AS BigDecimal
            ),
    
            CAST(
                (
                    SUM(
                        CASE
                            WHEN e.installment = false THEN e.amount
                            ELSE 0
                        END
                    ) * 100
                ) / SUM(e.amount)
                AS BigDecimal
            )
        )
        FROM CreditCardExpense e
        WHERE e.invoiceReferenceMonth = :month
          AND e.invoiceReferenceYear = :year
        GROUP BY
            e.invoiceReferenceMonth,
            e.invoiceReferenceYear
    """)
    CreditCardInstallmentPercentageResponse findInstallmentPercentageByMonthAndYear(
            @Param("month") Integer month,
            @Param("year") Integer year
    );
}
