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
import pedro.cost.control.domain.creditcard.entities.CreditCardExpense;

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
    List<ExpenseByCategoryDTO> findTotalByCategoryByInvoicePeriod(
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
}
