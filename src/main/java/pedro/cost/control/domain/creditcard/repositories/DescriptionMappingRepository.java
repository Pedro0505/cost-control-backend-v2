package pedro.cost.control.domain.creditcard.repositories;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pedro.cost.control.domain.creditcard.entities.DescriptionMapping;

import java.util.List;

@Repository
public interface DescriptionMappingRepository extends JpaRepository<DescriptionMapping, Long> {

    @Query("""
        SELECT dm FROM DescriptionMapping dm
        WHERE dm.user.id = :userId
        ORDER BY dm.position ASC
    """)
    List<DescriptionMapping> findAllByUserIdOrdered(@Param("userId") Long userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT dm FROM DescriptionMapping dm
        WHERE dm.user.id = :userId
        ORDER BY dm.position ASC
    """)
    List<DescriptionMapping> findAllByUserIdOrderedForUpdate(@Param("userId") Long userId);
}
