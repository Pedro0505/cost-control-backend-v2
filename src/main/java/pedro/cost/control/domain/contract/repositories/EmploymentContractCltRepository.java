package pedro.cost.control.domain.contract.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pedro.cost.control.domain.contract.entities.EmploymentContractClt;

@Repository
public interface EmploymentContractCltRepository extends JpaRepository<EmploymentContractClt, Long> {
}
