package pedro.cost.control.common;

import java.math.RoundingMode;

public final class MonetaryCalculationRules {
    private MonetaryCalculationRules() {}

    public static final int MONEY_SCALE = 2;
    public static final RoundingMode MONEY_ROUNDING = RoundingMode.HALF_EVEN;
}
