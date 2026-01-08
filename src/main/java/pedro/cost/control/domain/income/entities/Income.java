package pedro.cost.control.domain.income.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pedro.cost.control.domain.balance.entities.MonthlyBalance;
import pedro.cost.control.domain.contract.entities.EmploymentContract;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "income")
public class Income {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "reference_date", nullable = false)
    private LocalDate referenceDate;

    @ManyToOne
    @JoinColumn(name = "employment_contract_id")
    private EmploymentContract employmentContract;

    @ManyToOne
    @JoinColumn(name = "monthly_balance_id", nullable = false)
    private MonthlyBalance monthlyBalance;
}
