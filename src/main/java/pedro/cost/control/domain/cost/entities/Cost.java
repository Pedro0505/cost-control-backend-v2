package pedro.cost.control.domain.cost.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pedro.cost.control.domain.balance.entities.MonthlyBalance;
import pedro.cost.control.domain.cost.enums.CostCalculationType;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "cost")
public class Cost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "amount", precision = 15, scale = 2, nullable = false)
    private BigDecimal amount;

    @Column(name = "percentage", precision = 5, scale = 2, nullable = false)
    private BigDecimal percentage;

    @Enumerated(EnumType.STRING)
    @Column(name = "calculation_type", nullable = false)
    private CostCalculationType calculationType;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "paid", nullable = false)
    private Boolean paid = false;

    @Column(name = "is_recurrent", nullable = false)
    private Boolean recurrent;

    @ManyToOne
    @JoinColumn(name = "monthly_balance_id", nullable = false)
    private MonthlyBalance monthlyBalance;
}