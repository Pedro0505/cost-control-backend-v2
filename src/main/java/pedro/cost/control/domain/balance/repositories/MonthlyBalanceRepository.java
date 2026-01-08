package pedro.cost.control.domain.balance.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pedro.cost.control.domain.balance.entities.MonthlyBalance;

import java.util.List;
import java.util.Optional;

@Repository
public interface MonthlyBalanceRepository extends JpaRepository<MonthlyBalance, Long>  {
    @Query(value = """
                SELECT mb.id, mb.reference_month, mb.reference_year FROM monthly_balance mb
                WHERE mb.reference_year = :year AND mb.reference_month = :month
            """, nativeQuery = true
    )
    Optional<MonthlyBalance> findMonthlyBalanceByYearAndMonth(@Param("year") Integer year, @Param("month") Integer month);

    @Query("""
        SELECT DISTINCT mb FROM MonthlyBalance mb
        JOIN Income i ON i.monthlyBalance.id = mb.id
    """)
    List<MonthlyBalance> findAllMonthlyBalanceWithIncomeRelation();
}
