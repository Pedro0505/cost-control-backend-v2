package pedro.cost.control.domain.contract.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pedro.cost.control.domain.contract.entities.PjMonthlyWork;

import java.util.Optional;

@Repository
public interface PjMonthlyWorkRepository extends JpaRepository<PjMonthlyWork, Long> {
    @Query("""
        SELECT pmw FROM PjMonthlyWork pmw
        WHERE pmw.referenceMonth = :month AND pmw.referenceYear = :year
    """)
    Optional<PjMonthlyWork> findByYearAndMonth(@Param("year") Integer year, @Param("month") Integer month);

    @Query("""
        SELECT pmw
        FROM PjMonthlyWork pmw
        WHERE EXISTS (
            SELECT 1
            FROM Income i
            WHERE i.id = :incomeId
              AND pmw.referenceMonth = i.monthlyBalance.referenceMonth
              AND pmw.referenceYear = i.monthlyBalance.referenceYear
        )
    """)
    Optional<PjMonthlyWork> findPjMonthlyWorkLinkedWithIncomeId(@Param("incomeId") Long incomeId);
}
