package com.kyleduo.rabbits.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * for Rabbits
 * Created by kyleduo on 2017/5/8.
 */

@Retention(RetentionPolicy.CLASS)
public @interface Rabbits {
    String module() default "";

    String[] subModules() default {};

    String srcPath() default "main";
}
