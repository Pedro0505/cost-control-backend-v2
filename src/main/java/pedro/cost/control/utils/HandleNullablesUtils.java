package pedro.cost.control.utils;

import java.util.Objects;

public class HandleNullablesUtils {
    private HandleNullablesUtils() {}

    public static <T> T getValueOrDefault(T value, T defaultValue) {
        return Objects.requireNonNullElse(value, defaultValue);
    }
}
