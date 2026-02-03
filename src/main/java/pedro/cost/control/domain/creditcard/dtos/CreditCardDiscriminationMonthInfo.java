package pedro.cost.control.domain.creditcard.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pedro.cost.control.enums.MonthEnum;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class CreditCardDiscriminationMonthInfo {
    private Integer value;
    private String name;

    public CreditCardDiscriminationMonthInfo(Integer value) {
        this.value = value;
        this.name = MonthEnum.getMonthById(value).getMonthName();
    }
}
