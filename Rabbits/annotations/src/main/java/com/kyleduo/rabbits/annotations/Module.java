package com.kyleduo.rabbits.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * for Module
 * Created by kyleduo on 2017/5/8.
 */

@Retention(RetentionPolicy.CLASS)
public @interface Module {
    String name() default "";

    String[] subModules() default {};

    boolean standalone() default true;

    String srcPath() default "main";
}
