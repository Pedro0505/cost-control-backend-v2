package pedro.cost.control.builders.entities;

import pedro.cost.control.builders.dto.TestUser;
import pedro.cost.control.domain.contract.entities.EmploymentContractClt;
import pedro.cost.control.domain.user.entities.User;

import java.math.BigDecimal;
import java.time.LocalDate;

public class EmploymentContractCltTestBuilder {

    private BigDecimal grossSalary;
    private BigDecimal netSalary;
    private LocalDate contractInitDate;
    private LocalDate contractEndDate;
    private String contractType;
    private User user;

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

    public EmploymentContractCltTestBuilder withUser(TestUser user) {
        this.user = User.builder()
                .id(user.user().getId())
                .email(user.user().getEmail())
                .username(user.user().getUsername())
                .password(user.user().getPassword())
                .build();

        return this;
    }

    public EmploymentContractClt build() {
        EmploymentContractClt clt = new EmploymentContractClt();
        clt.setGrossSalary(grossSalary);
        clt.setNetSalary(netSalary);
        clt.setInitDate(contractInitDate);
        clt.setEndDate(contractEndDate);
        clt.setContractType(contractType);
        clt.setUser(user);
        return clt;
    }
}