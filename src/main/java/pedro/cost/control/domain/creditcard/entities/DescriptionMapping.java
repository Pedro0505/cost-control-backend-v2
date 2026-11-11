package pedro.cost.control.domain.creditcard.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pedro.cost.control.domain.creditcard.emuns.DescriptionMappingType;
import pedro.cost.control.domain.user.entities.User;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "description_mapping",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_description_mapping_user_position",
                columnNames = {"user_id", "sort_position"}
        )
)
public class DescriptionMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500)
    private String pattern;

    @Column(name = "normalized_value")
    private String value;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private DescriptionMappingType type;

    @Column(name = "sort_position", nullable = false)
    private Integer position;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
