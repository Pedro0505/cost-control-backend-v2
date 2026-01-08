package pedro.cost.control.domain.cost.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pedro.cost.control.domain.cost.enums.CostCalculationType;

import java.math.BigDecimal;

@NoArgsConstructor
@Getter
@Setter
public class CostOutputDTO {
    private Long id;
    private CostCalculationType calculationType;
    private BigDecimal amount;
    private BigDecimal percentage;
    private String description;
    private Boolean recurrent;
    private Boolean paid;

    public CostOutputDTO(Long id, CostCalculationType calculationType, BigDecimal amount, BigDecimal percentage, String description, Boolean recurrent, Boolean paid) {
        this.id = id;
        this.calculationType = calculationType;
        this.amount = amount;
        this.description = description;
        this.recurrent = recurrent;
        this.paid = paid;
        this.percentage = percentage;
    }
}
