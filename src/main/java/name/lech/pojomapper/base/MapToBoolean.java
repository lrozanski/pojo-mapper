package name.lech.pojomapper.base;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MapToBoolean {

    /**
     * The annotation is only active while mapping to one of the target classes.
     */
    Class<?>[] targetClasses();
}
