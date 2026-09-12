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
    @Query("""
        SELECT mb
        FROM MonthlyBalance mb
        WHERE mb.referenceYear = :year
        AND mb.referenceMonth = :month
        AND mb.user.id = :userId
    """)
    Optional<MonthlyBalance> findMonthlyBalanceByYearAndMonth(
            @Param("year") Integer year,
            @Param("month") Integer month,
            @Param("userId") Long userId
    );

    @Query("""
        SELECT DISTINCT mb FROM MonthlyBalance mb
        JOIN Income i ON i.monthlyBalance.id = mb.id
        WHERE mb.user.id = :userId
    """)
    List<MonthlyBalance> findAllMonthlyBalanceWithIncomeRelation(@Param("userId") Long userId);
}
