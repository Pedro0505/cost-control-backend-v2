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
        SELECT pmw
        FROM PjMonthlyWork pmw
        JOIN pmw.employmentContract ec
        WHERE pmw.referenceMonth = :month
          AND pmw.referenceYear = :year
          AND ec.user.id = :userId
    """)
    Optional<PjMonthlyWork> findByYearAndMonth(
            @Param("year") Integer year,
            @Param("month") Integer month,
            @Param("userId") Long userId
    );

    @Query("""
        SELECT pmw
        FROM PjMonthlyWork pmw
        JOIN pmw.employmentContract ec
        JOIN Income i ON i.employmentContract = ec
        WHERE i.id = :incomeId
          AND i.user.id = :userId
          AND pmw.referenceMonth = i.monthlyBalance.referenceMonth
          AND pmw.referenceYear = i.monthlyBalance.referenceYear
    """)
    Optional<PjMonthlyWork> findPjMonthlyWorkLinkedWithIncomeId(
            @Param("incomeId") Long incomeId,
            @Param("userId") Long userId
    );
}
