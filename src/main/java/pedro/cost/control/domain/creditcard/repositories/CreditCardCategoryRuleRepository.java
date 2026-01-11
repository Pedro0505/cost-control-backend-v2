package pedro.cost.control.domain.creditcard.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pedro.cost.control.domain.creditcard.entities.CreditCardCategoryRule;

import java.util.List;
import java.util.Optional;

public interface CreditCardCategoryRuleRepository extends JpaRepository<CreditCardCategoryRule, Long> {

    @Query("""
        SELECT cr FROM CreditCardCategoryRule cr
        WHERE cr.active IS TRUE
        ORDER BY cr.priority ASC
    """)
    List<CreditCardCategoryRule> findByActiveTrue();
}
