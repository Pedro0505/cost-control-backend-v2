package pedro.cost.control.domain.creditcard.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pedro.cost.control.domain.creditcard.emuns.CreditCardCategoryType;
import pedro.cost.control.domain.creditcard.entities.CreditCardCategory;

import java.util.Optional;

public interface CreditCardCategoryRepository extends JpaRepository<CreditCardCategory, Long> {
    @Query("""
        SELECT cc FROM CreditCardCategory cc
        WHERE cc.type = :cardCategoryType
    """)
    Optional<CreditCardCategory> findByCategoryType(@Param("cardCategoryType")CreditCardCategoryType cardCategoryType);
}
