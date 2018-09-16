package name.lech.pojomapper.generation;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;

class MethodMatcher {

    private GetterValidator getterValidator = GetterValidator.instance();

    private static MethodMatcher instance;

    private MethodMatcher() {
    }

    static MethodMatcher instance() {
        if (instance == null) {
            instance = new MethodMatcher();
        }
        return instance;
    }

    Optional<MappingPair> match(Method setter, Collection<Method> getters) {
        if (setter.getParameterCount() != 1
                || !setter.getReturnType().equals(Void.TYPE)) {
            return Optional.empty();
        }
        Set<String> expectedGetterNames = Sets.newHashSet(
                setter.getName().replaceFirst("set", "get"),
                setter.getName().replaceFirst("set", "is"),
                setter.getName().replaceFirst("set", "has")
        );
        Class<?> expectedReturnType = setter.getParameterTypes()[0];

        return getters
                .stream()
                .filter(getter -> getterValidator.isValidGetter(getter, setter, expectedGetterNames, expectedReturnType))
                .filter(getter -> getterValidator.hasValidReturnType(getter, setter, expectedReturnType))
                .findFirst()
                .map(getter -> new MappingPair(getter, setter));
    }
}
