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
public class UpdateCostInputDTO {
    @NotNull
    @NotBlank
    private String description;

    @NotNull
    private CostCalculationType calculationType;

    @NotNull
    @Min(value = 0, message = "O total mínimo é zero")
    private BigDecimal amount;

    @NotNull
    @Min(value = 0, message = "A porcentagem deve ser entre zero e cem")
    @Max(value = 100, message = "A porcentagem deve ser entre zero e cem")
    private BigDecimal percentage;

    @NotNull
    private Boolean recurrent;

    @NotNull
    private Boolean paid;
}
