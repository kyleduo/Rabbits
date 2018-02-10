package com.kyleduo.rabbits.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for a single page.
 *
 * Created by kyle on 2016/12/07.
 */

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.CLASS)
public @interface Page {
    /**
     * Main url pattern, whole url or path.
     * <p>
     * 1. /foo/bar
     * 2. foo://foo.bar/foo/bar
     * 3. /foo/{bar:i}
     *
     * @return url pattern
     */
    String value();

    /**
     * Used when this page can match multiple urls.
     * Exp.
     * /seg1/seg2/{id}
     * /seg1/seg2
     * /seg1/seg2/view
     * /seg1/seg2/detail
     *
     * @return other urls that can mark this page than the main one.
     */
    String[] variety() default {};

    /**
     * Flags related to this Page, like Intent flags. You can give meanings to each bit and
     * response to different flags in your interceptors.
     *
     * @return flags in int value
     */
    int flags() default 0;

    /**
     * Used to generate a constant you can use in your code against writing hard-coded urls.
     *
     * @return alias
     */
    String alias() default "";
}
