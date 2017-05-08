package com.kyleduo.rabbits.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation for a single page.
 * Created by kyle on 2016/12/07.
 */

@Retention(RetentionPolicy.CLASS)
public @interface Page {
	String name();

	PageType type() default PageType.ACTIVITY;

	String parent() default "";

	String[] stringExtras() default {};

	String[] floatExtras() default {};

	String[] intExtras() default {};

	String[] doubleExtras() default {};
}
