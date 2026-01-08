package pedro.cost.control.domain.income.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pedro.cost.control.domain.income.entities.Income;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface IncomeRepository extends JpaRepository<Income, Long>  {
    @Query("""
        SELECT SUM(i.amount)
        FROM Income i
        WHERE YEAR(i.referenceDate) = :year
          AND MONTH(i.referenceDate) = :month
    """)
    Optional<BigDecimal> sumAmountByMonth(
            @Param("year") int year,
            @Param("month") int month
    );
}
