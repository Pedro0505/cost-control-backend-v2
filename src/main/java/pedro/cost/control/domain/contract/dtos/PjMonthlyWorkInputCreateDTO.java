package pedro.cost.control.domain.contract.dtos;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class PjMonthlyWorkInputCreateDTO {
    @NotNull
    private Integer businessDays;

    @Min(value = 1)
    @Max(value = 12)
    private Integer referenceMonth;

    @NotNull
    private Integer referenceYear;
}
