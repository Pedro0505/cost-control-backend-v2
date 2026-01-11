package pedro.cost.control.domain.creditcard.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "credit_card_category_rule")
public class CreditCardCategoryRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private CreditCardCategory category;

    @Column(nullable = false)
    private String pattern;

    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "rule_priority_seq")
    @SequenceGenerator(name = "rule_priority_seq", sequenceName = "rule_priority_seq", allocationSize = 1)
    private Long priority;

    @Column(nullable = false)
    private Boolean active = true;
}
