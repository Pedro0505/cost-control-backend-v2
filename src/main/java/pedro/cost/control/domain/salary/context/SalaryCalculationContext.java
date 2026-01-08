package pedro.cost.control.domain.salary.context;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@Getter
@RequiredArgsConstructor
public class SalaryCalculationContext {
    private final BigDecimal netSalary;
    private final BigDecimal hourlyRate;
    private final Integer businessDays;
    private final Integer hoursPerDay;
}
