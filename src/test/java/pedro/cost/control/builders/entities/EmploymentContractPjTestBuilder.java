package pedro.cost.control.builders.entities;

import pedro.cost.control.builders.dto.TestUser;
import pedro.cost.control.domain.contract.entities.EmploymentContractPj;
import pedro.cost.control.domain.user.entities.User;

import java.math.BigDecimal;
import java.time.LocalDate;

public class EmploymentContractPjTestBuilder {

    private BigDecimal hourlyRate;
    private LocalDate contractInitDate;
    private LocalDate contractEndDate;
    private String contractType;
    private User user;

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

    public EmploymentContractPjTestBuilder withUser(TestUser user) {
        this.user = User.builder()
                .id(user.user().getId())
                .email(user.user().getEmail())
                .username(user.user().getUsername())
                .password(user.user().getPassword())
                .build();

        return this;
    }

    public EmploymentContractPj build() {
        EmploymentContractPj pj = new EmploymentContractPj();
        pj.setHourlyRate(hourlyRate);
        pj.setInitDate(contractInitDate);
        pj.setEndDate(contractEndDate);
        pj.setContractType(contractType);
        pj.setUser(user);
        return pj;
    }
}