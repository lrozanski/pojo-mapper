package name.lech.pojomapper.generation;

import java.lang.reflect.Method;

public class MappingPair {

    private Method getter;
    private Method setter;

    MappingPair(Method getter, Method setter) {
        this.getter = getter;
        this.setter = setter;
    }

    public Method getGetter() {
        return getter;
    }

    public Method getSetter() {
        return setter;
    }

    @Override
    public String toString() {
        return "MappingPair{" +
                "getter=" + (getter == null ? "" : getter.getName()) +
                ", setter=" + (setter == null ? "" : setter.getName()) +
                '}';
    }
}
