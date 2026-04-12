package pedro.cost.control.domain.contract.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CltContractInputCreateDTO {
    @NotNull
    private BigDecimal netSalary;

    @NotNull
    private BigDecimal grossSalary;

    @NotNull
    private LocalDate contractInitDate;

    @NotNull
    private LocalDate contractEndDate;
}
