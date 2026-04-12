package pedro.cost.control.contract.mocks;

import pedro.cost.control.domain.contract.entities.EmploymentContractClt;

import java.math.BigDecimal;
import java.time.LocalDate;

public class EmploymentContractCltTestBuilder {

    private BigDecimal grossSalary;
    private BigDecimal netSalary;
    private LocalDate contractInitDate;
    private LocalDate contractEndDate;
    private String contractType;

    public static EmploymentContractCltTestBuilder builder() {
        return new EmploymentContractCltTestBuilder();
    }

    public EmploymentContractCltTestBuilder withGrossSalary(BigDecimal grossSalary) {
        this.grossSalary = grossSalary;
        return this;
    }

    public EmploymentContractCltTestBuilder withNetSalary(BigDecimal netSalary) {
        this.netSalary = netSalary;
        return this;
    }

    public EmploymentContractCltTestBuilder withContractInitDate(LocalDate contractInitDate) {
        this.contractInitDate = contractInitDate;
        return this;
    }

    public EmploymentContractCltTestBuilder withContractEndDate(LocalDate contractEndDate) {
        this.contractEndDate = contractEndDate;
        return this;
    }

    public EmploymentContractCltTestBuilder withContractType(String contractType) {
        this.contractType = contractType;
        return this;
    }

    public EmploymentContractClt build() {
        EmploymentContractClt clt = new EmploymentContractClt();
        clt.setGrossSalary(grossSalary);
        clt.setNetSalary(netSalary);
        clt.setInitDate(contractInitDate);
        clt.setEndDate(contractEndDate);
        clt.setContractType(contractType);
        return clt;
    }
}