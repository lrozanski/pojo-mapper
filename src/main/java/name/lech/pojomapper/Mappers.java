package name.lech.pojomapper;

import java.util.function.Function;

@SuppressWarnings("unused")
public final class Mappers {

    private Mappers() {
    }

    public static <T, U> Function<T, String> mapToString(Function<T, U> valueGetter) {
        return fromInstance -> {
            U value = valueGetter.apply(fromInstance);
            return value == null
                    ? ""
                    : String.valueOf(value);
        };
    }
}
