package name.lech.pojomapper.base;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface CreateMappersForClasses {

    /**
     * Target classes for which mappers should be created.
     * The annotated class is always the source.
     */
    Class<?>[] targetClasses() default {};
}
