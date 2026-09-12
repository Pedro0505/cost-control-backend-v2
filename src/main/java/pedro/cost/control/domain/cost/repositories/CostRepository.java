package pedro.cost.control.domain.cost.repositories;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pedro.cost.control.domain.cost.dtos.CostOutputDTO;
import pedro.cost.control.domain.cost.entities.Cost;

import java.util.List;
import java.util.Optional;

public interface CostRepository extends JpaRepository<Cost, Long> {
    @Query(value = """
            SELECT new pedro.cost.control.domain.cost.dtos.CostOutputDTO(
                c.id,
                c.calculationType,
                c.amount,
                c.percentage,
                c.description,
                c.recurrent,
                c.paid
            ) FROM Cost c
            WHERE c.monthlyBalance.referenceMonth = :month
            AND c.monthlyBalance.referenceYear = :year
            AND c.user.id = :userId
            ORDER BY c.amount DESC
            """)
    List<CostOutputDTO> findAllCostByYearMonth(
            @Param("year") Integer year,
            @Param("month") Integer month,
            @Param("userId") Long userId
    );

    @Query(value = """
            SELECT c FROM Cost c
            WHERE c.monthlyBalance.referenceMonth = :month
            AND c.monthlyBalance.referenceYear = :year
            AND c.recurrent IS TRUE
            AND c.user.id = :userId
            """)
    List<Cost> findAllRecurrentCostByYearMonth(
            @Param("year") Integer year,
            @Param("month") Integer month,
            @Param("userId") Long userId
    );

    @Transactional
    @Modifying
    @Query("""
        DELETE FROM Cost c
        WHERE c.id = :costId AND c.user.id = :userId
    """)
    int deleteCostByIdAndUserId(@Param("costId") Long costId, @Param("userId") Long userId);

    @Query(value = """
        SELECT c FROM Cost c
        WHERE c.id = :costId
        AND c.user.id = :userId
    """)
    Optional<Cost> findCostByIdAndUserId(@Param("costId") Long costId, @Param("userId") Long userId);
}