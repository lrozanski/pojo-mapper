package name.lech.pojomapper;

import java.lang.reflect.Method;

public class MappingPair {

    private Method getter;
    private Method setter;

    public MappingPair(Method getter, Method setter) {
        this.getter = getter;
        this.setter = setter;
    }

    public Method getGetter() {
        return getter;
    }

    public Method getSetter() {
        return setter;
    }
}
