package name.lech.pojomapper.generation;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

final class Sets {

    private Sets() {
    }

    @SafeVarargs
    static <T> Set<T> newHashSet(T... values) {
        return Arrays
                .stream(values)
                .collect(Collectors.toCollection(HashSet::new));
    }
}
