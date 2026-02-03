package pedro.cost.control.domain.creditcard.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
public class ExpenseByCategoryDTO {
    private String category;
    private BigDecimal total;
}
