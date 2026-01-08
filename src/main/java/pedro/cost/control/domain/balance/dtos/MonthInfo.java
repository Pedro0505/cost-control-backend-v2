package pedro.cost.control.domain.balance.dtos;

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
public class MonthInfo {
    private Integer value;
    private String name;

    public MonthInfo(Integer value) {
        this.value = value;
        this.name = MonthEnum.getMonthById(value).getMonthName();
    }
}
