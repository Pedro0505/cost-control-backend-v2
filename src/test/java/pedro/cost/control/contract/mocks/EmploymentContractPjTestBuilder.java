package pedro.cost.control.contract.mocks;

import pedro.cost.control.domain.contract.entities.EmploymentContractPj;

import java.math.BigDecimal;
import java.time.LocalDate;

public class EmploymentContractPjTestBuilder {

    private BigDecimal hourlyRate;
    private LocalDate contractInitDate;
    private LocalDate contractEndDate;
    private String contractType;

    public static EmploymentContractPjTestBuilder builder() {
        return new EmploymentContractPjTestBuilder();
    }

    public EmploymentContractPjTestBuilder withHourlyRate(BigDecimal hourlyRate) {
        this.hourlyRate = hourlyRate;
        return this;
    }

    public EmploymentContractPjTestBuilder withContractInitDate(LocalDate contractInitDate) {
        this.contractInitDate = contractInitDate;
        return this;
    }

    public EmploymentContractPjTestBuilder withContractEndDate(LocalDate contractEndDate) {
        this.contractEndDate = contractEndDate;
        return this;
    }

    public EmploymentContractPjTestBuilder withContractType(String contractType) {
        this.contractType = contractType;
        return this;
    }

    public EmploymentContractPj build() {
        EmploymentContractPj pj = new EmploymentContractPj();
        pj.setHourlyRate(hourlyRate);
        pj.setInitDate(contractInitDate);
        pj.setEndDate(contractEndDate);
        pj.setContractType(contractType);
        return pj;
    }
}