package pedro.cost.control.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class YearMonthSummary {
    private Integer year;
    private Integer month;
}
