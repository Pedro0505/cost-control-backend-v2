package pedro.cost.control.domain.contract.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pedro.cost.control.domain.contract.dtos.PjMonthlyContractOutputDTO;
import pedro.cost.control.domain.contract.entities.PjMonthlyWork;
import org.springframework.data.domain.PageRequest;

import java.util.List;

@Repository
public interface PjMonthlyWorkRepository extends JpaRepository<PjMonthlyWork, Long> {
    @Query(value = """
                SELECT new pedro.cost.control.domain.contract.dtos.PjMonthlyContractOutputDTO(
                    pjw.id,
                    pjw.referenceMonth,
                    pjw.referenceYear,
                    pjw.businessDays,
                    ecp.hourlyRate
                ) FROM PjMonthlyWork pjw
                JOIN EmploymentContractPj ecp ON pjw.employmentContract.id = ecp.id
            """)
    Page<PjMonthlyContractOutputDTO> getAllPjMonthlyWithContractPageable(PageRequest pageable);

    @Query(value = """
        SELECT new pedro.cost.control.domain.contract.dtos.PjMonthlyContractOutputDTO(
            pjw.id,
            pjw.referenceMonth,
            pjw.referenceYear,
            pjw.businessDays,
            ecp.hourlyRate
        ) FROM PjMonthlyWork pjw
        JOIN EmploymentContractPj ecp ON pjw.employmentContract.id = ecp.id
        WHERE (:year IS NULL OR :year = pjw.referenceYear)
    """)
    List<PjMonthlyContractOutputDTO> getAllPjMonthlyWithContractFiltered(@Param("year") Integer year);
}
