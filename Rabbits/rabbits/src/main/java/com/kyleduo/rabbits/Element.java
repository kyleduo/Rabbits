package com.kyleduo.rabbits;

/**
 * Operators used when create a Rule.
 *
 * Created by kyle on 11/02/2018.
 */

@SuppressWarnings("unused")
public interface Element {
    /**
     * Check whether the source exits.
     *
     * @return a Rule instance
     */
    Rule exists();

    /**
     * Check whether the source equals to the {@param value}
     * using {@link java.lang.String#equals} method.
     *
     * @param value value
     * @return a Rule instance
     */
    Rule is(String value);

    /**
     * Check whether the source NOT exists or NOT equals to the {@param value}
     * using {@link java.lang.String#equals} method.
     *
     * @param value value
     * @return a Rule instance
     */
    Rule not(String value);

    /**
     * Check whether the source has the {@param value} as prefix
     * using {@link java.lang.String#startsWith} method.
     *
     * @param value value
     * @return a Rule instance
     */
    Rule startsWith(String value);

    /**
     * Check whether the source has the {@param value} as suffix
     * using {@link java.lang.String#endsWith} method.
     *
     * @param value value
     * @return a Rule instance
     */
    Rule endsWith(String value);

    /**
     * Check whether the source contains the {@param value} as a substring
     * using {@link java.lang.String#endsWith} method.
     *
     * @param value value
     * @return a Rule instance
     */
    Rule contains(String value);

    /**
     * Check whether the source is one of the {@param values}.
     *
     * @param values value
     * @return a Rule instance
     */
    Rule in(String... values);
}
