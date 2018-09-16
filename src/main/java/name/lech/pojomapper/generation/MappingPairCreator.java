package name.lech.pojomapper.generation;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class MappingPairCreator {

    private GetterValidator getterValidator = GetterValidator.instance();
    private MethodMatcher methodMatcher = MethodMatcher.instance();

    private static MappingPairCreator instance;

    public static MappingPairCreator instance() {
        if (instance == null) {
            instance = new MappingPairCreator();
        }
        return instance;
    }

    public <T, R> List<MappingPair> createMappingPairs(Class<T> fromClass, Class<R> toClass) {
        List<Method> getterMethods = Arrays.stream(fromClass.getDeclaredMethods())
                .filter(getterValidator::isGetter)
                .collect(Collectors.toList());
        List<Method> setterMethods = Arrays.stream(toClass.getDeclaredMethods())
                .filter(method -> method.getName().startsWith("set"))
                .collect(Collectors.toList());

        return createMappingPairs(getterMethods, setterMethods);
    }

    private List<MappingPair> createMappingPairs(List<Method> setterMethods, List<Method> getterMethods) {
        return Collections.unmodifiableList(
                setterMethods
                        .stream()
                        .map(setter -> methodMatcher.match(setter, getterMethods))
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .collect(Collectors.toList())
        );
    }
}
