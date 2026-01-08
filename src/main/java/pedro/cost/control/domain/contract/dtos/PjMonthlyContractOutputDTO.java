package pedro.cost.control.domain.contract.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@NoArgsConstructor
@Getter
@Setter
@Builder
public class PjMonthlyContractOutputDTO {
    private Long id;
    private Integer referenceMonth;
    private Integer referenceYear;
    private Integer businessDays;
    private BigDecimal hourlyRate;

    public PjMonthlyContractOutputDTO(Long id, Integer referenceMonth, Integer referenceYear, Integer businessDays, BigDecimal hourlyRate) {
        this.id = id;
        this.referenceMonth = referenceMonth;
        this.referenceYear = referenceYear;
        this.businessDays = businessDays;
        this.hourlyRate = hourlyRate;
    }
}
