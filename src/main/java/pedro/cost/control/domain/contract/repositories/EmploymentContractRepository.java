package pedro.cost.control.domain.contract.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pedro.cost.control.domain.contract.dtos.ContractSummaryDTO;
import pedro.cost.control.domain.contract.dtos.EmploymentContractOutputDTO;
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
            TREAT(ec AS EmploymentContractClt).grossSalary,
            ec
        )
        FROM EmploymentContract ec
        WHERE :referenceDate >= ec.initDate AND (ec.endDate IS NULL OR :referenceDate <= ec.endDate)
    """)
    Optional<ContractSummaryDTO> findOpenedEmploymentContract(@Param("referenceDate") LocalDate referenceDate);

    @Query("""
        SELECT new pedro.cost.control.domain.contract.dtos.EmploymentContractOutputDTO(
            ec.id,
            ec.initDate,
            ec.endDate,
            ec.contractType,
            pj.hourlyRate,
            clt.grossSalary,
            clt.netSalary
        )
        FROM EmploymentContract ec
        LEFT JOIN EmploymentContractPj pj ON pj.id = ec.id
        LEFT JOIN EmploymentContractClt clt ON clt.id = ec.id
    """)
    Page<EmploymentContractOutputDTO> getAllContractsPaged(PageRequest pageable);
}
