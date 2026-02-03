package pedro.cost.control.common;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class YearMonthSummary {
    private Integer year;
    private Integer month;

    public YearMonthSummary(Integer year, Integer month) {
        this.year = year;
        this.month = month;
    }
}
