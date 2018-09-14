package name.lech.pojomapper;

import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

@SuppressWarnings({"WeakerAccess", "unused"})
public abstract class Mapper<T, R> {

    private final Class<R> toClass;
    private final List<BiConsumer<T, R>> fieldConsumers;

    @SuppressWarnings("unchecked")
    public Mapper() {
        this.toClass = (Class<R>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[1];
        this.fieldConsumers = getFieldConsumers();
    }

    protected abstract List<BiConsumer<T, R>> getFieldConsumers();

    public R map(T fromInstance) {
        R toInstance = ReflectionHelper.newInstance(toClass);
        fieldConsumers.forEach(consumer -> consumer.accept(fromInstance, toInstance));
        return toInstance;
    }

    protected <U> BiConsumer<T, R> map(BiConsumer<R, U> consumer, Function<T, U> valueGetter) {
        return (T fromInstance, R toInstance) -> consumer.accept(toInstance, valueGetter.apply(fromInstance));
    }
}
