package pedro.cost.control.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class NumberUtils {
    private NumberUtils() {}

    public static double round(double valor) {
        BigDecimal bd = BigDecimal.valueOf(valor).setScale(2, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
