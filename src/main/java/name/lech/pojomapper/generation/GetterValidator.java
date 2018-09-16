package name.lech.pojomapper.generation;

import name.lech.pojomapper.base.MapToBoolean;
import name.lech.pojomapper.base.MapToLong;
import name.lech.pojomapper.base.MapToString;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

class GetterValidator {

    private static final Set<String> GETTER_PREFIXES = Sets.newHashSet("get", "is", "has");
    private static GetterValidator instance;

    private GetterValidator() {
    }

    static GetterValidator instance() {
        if (instance == null) {
            instance = new GetterValidator();
        }
        return instance;
    }

    boolean isGetter(Method getter) {
        if (getter.getReturnType().equals(boolean.class) || getter.getReturnType().equals(Boolean.class)) {
            String prefix = getter
                    .getName()
                    .substring(0, 3);
            return GETTER_PREFIXES.contains(prefix);
        }
        return getter
                .getName()
                .startsWith("get");
    }

    boolean isValidGetter(Method getter, Method setter, Collection<String> expectedGetterNames, Class<?> expectedReturnType) {
        return expectedGetterNames.contains(getter.getName())
                && getter.getParameterCount() == 0
                && hasValidReturnType(getter, setter, expectedReturnType);
    }

    boolean hasValidReturnType(Method getter, Method setter, Class<?> expectedReturnType) {
        if (getter.isAnnotationPresent(MapToString.class)) {
            MapToString annotation = getter.getAnnotation(MapToString.class);
            return expectedReturnType.equals(String.class)
                    && isTargetClass(setter, annotation.targetClasses());
        }
        if (getter.isAnnotationPresent(MapToLong.class)) {
            MapToLong annotation = getter.getAnnotation(MapToLong.class);
            return expectedReturnType.equals(Long.class)
                    && isTargetClass(setter, annotation.targetClasses());
        }
        if (getter.isAnnotationPresent(MapToBoolean.class)) {
            MapToBoolean annotation = getter.getAnnotation(MapToBoolean.class);
            return (expectedReturnType.equals(Boolean.class)
                    || expectedReturnType.equals(boolean.class))
                    && isTargetClass(setter, annotation.targetClasses());
        }
        return getter.getReturnType().equals(expectedReturnType);
    }

    private boolean isTargetClass(Method setter, Class[] targetClasses) {
        return Arrays
                .stream(targetClasses)
                .anyMatch(targetClass -> targetClass.equals(setter.getDeclaringClass()));
    }
}
