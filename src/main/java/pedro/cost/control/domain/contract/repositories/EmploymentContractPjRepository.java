package pedro.cost.control.domain.contract.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pedro.cost.control.domain.contract.entities.EmploymentContractPj;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface EmploymentContractPjRepository extends JpaRepository<EmploymentContractPj, Long> {
    @Query("""
    SELECT ec
    FROM EmploymentContractPj ec
    WHERE ec.user.id = :userId
    AND ec.initDate <= :referenceDate
    AND (
        ec.endDate IS NULL
        OR ec.endDate >= :referenceDate
    )
""")
    Optional<EmploymentContractPj> findEmploymentContractByYearAndMonth(
            @Param("referenceDate") LocalDate referenceDate,
            @Param("userId") Long userId
    );
}
