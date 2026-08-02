package pedro.cost.control.domain.creditcard.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "credit_card_expense")
public class CreditCardExpense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String rawDescription;

    @Column
    private String normalizedDescription;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false)
    private Integer invoiceReferenceMonth;

    @Column(nullable = false)
    private Integer invoiceReferenceYear;

    @Column(nullable = false)
    private LocalDate expenseDate;

    @Column(name = "is_installment", nullable = false)
    private boolean installment;
}
