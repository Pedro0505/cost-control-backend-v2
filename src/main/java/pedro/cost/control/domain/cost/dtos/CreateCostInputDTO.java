package pedro.cost.control.domain.cost.dtos;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pedro.cost.control.domain.cost.enums.CostCalculationType;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class CreateCostInputDTO {
    @Min(value = 1, message = "Os meses permitidos são entre 1-12")
    @Max(value = 12, message = "Os meses permitidos são entre 1-12")
    @NotNull
    private Integer referenceMonth;

    @NotNull
    private Integer referenceYear;

    @NotNull
    @NotBlank
    private String description;

    @NotNull
    private Boolean recurrent;

    @NotNull
    private Boolean paid;

    @NotNull
    private CostCalculationType calculationType;

    @NotNull
    @Min(value = 0, message = "A porcentagem mínima é zero")
    private BigDecimal percentage;

    @NotNull
    @Min(value = 0, message = "O total mínimo é zero")
    private BigDecimal amount;
}
