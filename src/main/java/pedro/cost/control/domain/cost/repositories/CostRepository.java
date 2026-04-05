package pedro.cost.control.domain.cost.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pedro.cost.control.domain.cost.dtos.CostOutputDTO;
import pedro.cost.control.domain.cost.entities.Cost;

import java.util.List;

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
            WHERE c.monthlyBalance.referenceMonth = :month AND c.monthlyBalance.referenceYear = :year
            ORDER BY c.amount DESC
            """)
    List<CostOutputDTO> findAllCostByYearMonth(@Param("year") Integer year, @Param("month") Integer month);

    @Query(value = """
            SELECT c FROM Cost c
            WHERE c.monthlyBalance.referenceMonth = :month AND c.monthlyBalance.referenceYear = :year
            AND c.recurrent IS TRUE
            """)
    List<Cost> findAllRecurrentCostByYearMonth(@Param("year") Integer year, @Param("month") Integer month);
}