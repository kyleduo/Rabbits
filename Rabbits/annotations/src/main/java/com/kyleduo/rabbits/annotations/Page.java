package com.kyleduo.rabbits.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for a single page.
 * Created by kyle on 2016/12/07.
 */

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.CLASS)
public @interface Page {
    String value();

    String parent() default "";

    String[] stringExtras() default {};

    String[] floatExtras() default {};

    String[] intExtras() default {};

    String[] doubleExtras() default {};
}
