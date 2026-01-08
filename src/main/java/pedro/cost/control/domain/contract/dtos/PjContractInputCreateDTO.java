package pedro.cost.control.domain.contract.dtos;

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
public class PjContractInputCreateDTO {
    private BigDecimal hourlyRate;
    private LocalDate contractInitDate;
    private LocalDate contractEndDate;
}
