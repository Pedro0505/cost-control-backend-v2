package pedro.cost.control.domain.contract.dtos;

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
public class EmploymentContractOutputDTO {
    private Long id;
    private LocalDate initDate;
    private LocalDate endDate;
    private String contractType;
    private BigDecimal hourlyRate;
    private BigDecimal grossSalary;
    private BigDecimal netSalary;
}
