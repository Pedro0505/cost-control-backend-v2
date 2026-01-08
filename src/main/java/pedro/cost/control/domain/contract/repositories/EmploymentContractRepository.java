package pedro.cost.control.domain.contract.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pedro.cost.control.domain.contract.dtos.ContractSummaryDTO;
import pedro.cost.control.domain.contract.entities.EmploymentContract;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface EmploymentContractRepository extends JpaRepository<EmploymentContract, Long> {
    @Query("""
        SELECT new pedro.cost.control.domain.contract.dtos.ContractSummaryDTO(
            ec.id,
            ec.contractType,
            TREAT(ec AS EmploymentContractPj).hourlyRate,
            pmw.businessDays,
            TREAT(ec AS EmploymentContractClt).grossSalary,
            ec
        )
        FROM EmploymentContract ec
        LEFT JOIN PjMonthlyWork pmw
            ON pmw.employmentContract = ec
            AND pmw.referenceMonth = MONTH(:referenceDate)
            AND pmw.referenceYear = YEAR(:referenceDate)
        WHERE :referenceDate >= ec.initDate AND (ec.endDate IS NULL OR :referenceDate <= ec.endDate)
    """)
    Optional<ContractSummaryDTO> findOpenedEmploymentContract(@Param("referenceDate") LocalDate referenceDate);
}
