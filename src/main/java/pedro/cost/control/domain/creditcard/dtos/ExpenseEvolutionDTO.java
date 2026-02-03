package pedro.cost.control.domain.creditcard.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ExpenseEvolutionDTO {
    private String category;
    private Integer year;
    private Integer month;
    private BigDecimal total;
}
