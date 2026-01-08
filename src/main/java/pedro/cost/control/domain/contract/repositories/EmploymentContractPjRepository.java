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
    @Query(value = """
        SELECT ec.id, ec.init_date, ec.end_date, ec.contract_type, ecp.hourly_rate
        FROM employment_contract ec
        INNER JOIN employment_contract_pj ecp ON ecp.id = ec.id
        WHERE STR_TO_DATE(CONCAT(:year, '-', LPAD(:month, 2, '0'), '-01'), '%Y-%m-%d') BETWEEN ec.init_date AND COALESCE(ec.end_date, '9999-12-31')
    """, nativeQuery = true
    )
    EmploymentContractPj findEmploymentContractByYearAndMonth(@Param("year") Integer year, @Param("month") Integer month);

    @Query(value = """
            SELECT ec.id, ec.init_date, ec.end_date, ec.contract_type, ecp.hourly_rate
            FROM employment_contract ec
            INNER JOIN employment_contract_pj ecp ON ecp.id = ec.id
            WHERE (:init_date BETWEEN ec.init_date AND COALESCE(ec.end_date, LAST_DAY(ec.init_date)))
            OR (:end_date BETWEEN ec.init_date AND COALESCE(ec.end_date, LAST_DAY(ec.init_date)))
    """, nativeQuery = true
    )
    Optional<EmploymentContractPj> findContractPjOverlap(@Param("init_date") LocalDate initDate, @Param("end_date") LocalDate endDate);

    @Query(value = """
                SELECT ec.id, ec.init_date, ec.end_date, ec.contract_type, ecp.hourly_rate
                FROM employment_contract ec
                INNER JOIN employment_contract_pj ecp ON ecp.id = ec.id
                WHERE end_date IS NULL
            """, nativeQuery = true
    )
    Optional<EmploymentContractPj> findEmploymentContractOpenedByContractType();
}
