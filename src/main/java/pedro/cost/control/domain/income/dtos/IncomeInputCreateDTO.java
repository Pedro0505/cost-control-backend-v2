package pedro.cost.control.domain.income.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class IncomeInputCreateDTO {
    private BigDecimal amount;
    private String description;
    private LocalDate referenceDate;
}
