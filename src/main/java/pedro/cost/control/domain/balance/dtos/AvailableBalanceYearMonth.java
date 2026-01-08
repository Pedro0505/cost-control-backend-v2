package pedro.cost.control.domain.balance.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class AvailableBalanceYearMonth {
    private Integer availableYear;
    private List<MonthInfo> availableMonth;
}
