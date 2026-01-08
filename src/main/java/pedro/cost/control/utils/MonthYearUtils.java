package pedro.cost.control.utils;

import pedro.cost.control.common.YearMonthSummary;

import java.time.LocalDate;

public class MonthYearUtils {
    private MonthYearUtils() {}

    public static YearMonthSummary decreaseMonth(Integer year, Integer month) {
        int dayOfMonth = 1;

        LocalDate localDate = LocalDate.of(year, month, dayOfMonth);

        LocalDate localDateMinusOneMonth = localDate.minusMonths(1);

        return YearMonthSummary
                .builder()
                .month(localDateMinusOneMonth.getMonthValue())
                .year(localDateMinusOneMonth.getYear())
                .build();
    }
}
