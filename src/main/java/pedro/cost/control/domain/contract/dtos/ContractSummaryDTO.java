package pedro.cost.control.domain.contract.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import pedro.cost.control.domain.contract.entities.EmploymentContract;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class ContractSummaryDTO {
    private Long id;
    private String contractType;
    private BigDecimal hourlyRate;
    private BigDecimal netSalary;
    private EmploymentContract employmentContract;

    public ContractSummaryDTO(Long id, String contractType, BigDecimal hourlyRate, BigDecimal netSalary, EmploymentContract employmentContract) {
        this.id = id;
        this.contractType = contractType;
        this.hourlyRate = hourlyRate;
        this.netSalary = netSalary;
        this.employmentContract = employmentContract;
    }
}
