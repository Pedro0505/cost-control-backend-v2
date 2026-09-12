package pedro.cost.control.domain.income.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pedro.cost.control.domain.income.dtos.IncomeOutputDTO;
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
          AND i.user.id = :userId
    """)
    Optional<BigDecimal> sumAmountByMonth(
            @Param("year") int year,
            @Param("month") int month,
            @Param("userId") Long userId
    );

    @Query("""
        SELECT new pedro.cost.control.domain.income.dtos.IncomeOutputDTO(
            i.id,
            i.amount,
            i.description,
            i.referenceDate,
            i.employmentContract.contractType
        ) FROM Income i
        WHERE i.user.id = :userId
    """)
    Page<IncomeOutputDTO> findAllByUserId(@Param("userId") Long userId, PageRequest pageable);

    @Query("""
        SELECT i FROM Income i
        WHERE i.user.id = :userId
        AND i.id = :id
    """)
    Optional<Income> findIncomeByIdAndUserId(@Param("id") Long id, @Param("userId") Long userId);
}
