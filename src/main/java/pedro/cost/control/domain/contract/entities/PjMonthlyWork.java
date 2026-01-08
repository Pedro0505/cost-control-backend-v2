package pedro.cost.control.domain.contract.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "pj_monthly_work",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"employment_contract_id", "reference_month", "reference_year"}
        )
)
public class PjMonthlyWork {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "reference_month", nullable = false)
    private Integer referenceMonth;

    @Column(name = "reference_year", nullable = false)
    private Integer referenceYear;

    @Column(name = "business_days", nullable = false)
    private Integer businessDays;

    @ManyToOne
    @JoinColumn(name = "employment_contract_id", nullable = false)
    private EmploymentContract employmentContract;
}
