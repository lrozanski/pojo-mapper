package name.lech.pojomapper.base;

import java.util.function.Function;

@SuppressWarnings("unused")
public final class Mappers {

    private Mappers() {
    }

    public static <T, U> Function<T, String> mapToString(Function<T, U> valueGetter) {
        return fromInstance -> {
            U value = valueGetter.apply(fromInstance);
            return value == null
                    ? null
                    : String.valueOf(value);
        };
    }

    public static <T, U> Function<T, Long> mapToLong(Function<T, U> valueGetter) {
        return fromInstance -> {
            U value = valueGetter.apply(fromInstance);
            String stringValue = value == null
                    ? null
                    : String.valueOf(value);

            return value == null
                    ? null
                    : Long.valueOf(stringValue);
        };
    }

    public static <T, U> Function<T, Boolean> mapToBoolean(Function<T, U> valueGetter) {
        return fromInstance -> {
            U value = valueGetter.apply(fromInstance);
            String stringValue = value == null
                    ? null
                    : String.valueOf(value);

            return value == null
                    ? null
                    : Boolean.valueOf(stringValue);
        };
    }
}
