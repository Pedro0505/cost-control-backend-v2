package pedro.cost.control.domain.income.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@NoArgsConstructor
@Getter
@Setter
@Builder
public class IncomeOutputDTO {
    private Long id;
    private BigDecimal amount;
    private String description;
    private LocalDate referenceDate;
    private String contractType;

    public IncomeOutputDTO(Long id, BigDecimal amount, String description, LocalDate referenceDate, String contractType) {
        this.id = id;
        this.amount = amount;
        this.description = description;
        this.referenceDate = referenceDate;
        this.contractType = contractType;
    }
}
