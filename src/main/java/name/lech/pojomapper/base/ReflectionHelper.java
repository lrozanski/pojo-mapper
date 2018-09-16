package name.lech.pojomapper.base;

final class ReflectionHelper {

    private ReflectionHelper() {
    }

    static <T> T newInstance(Class<T> instanceClass) {
        try {
            return instanceClass.newInstance();
        } catch (IllegalAccessException | InstantiationException e) {
            throw new RuntimeException("Failed to create instance of class " + instanceClass.getName(), e);
        }
    }
}
