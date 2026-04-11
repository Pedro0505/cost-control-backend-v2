package pedro.cost.control.domain.cost.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pedro.cost.control.domain.cost.enums.CostCalculationType;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PreviewRecurrentCostsForImportOutPutDTO {
    private Long id;
    private CostCalculationType calculationType;
    private BigDecimal amount;
    private BigDecimal percentage;
    private String description;
}
