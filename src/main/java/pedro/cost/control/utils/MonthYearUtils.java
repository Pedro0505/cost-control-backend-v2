package pedro.cost.control.utils;

import pedro.cost.control.config.exceptions.BadRequestException;

import java.time.LocalDate;

public class MonthYearUtils {
    private MonthYearUtils() {}

    public static void validateMonthAndYear(Integer year, Integer month) {
        Integer currentYear = LocalDate.now().getYear();

        if (month < 1 || month > 12) {
            throw new BadRequestException("O mês " + month + " é inválido");
        }

        if (year > currentYear || year < 2000) {
            throw new BadRequestException("O ano " + year + " é inválido");
        }
    }
}
