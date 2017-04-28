package com.kyleduo.rabbits.navigator;

/**
 * Interface for Provider.
 * <p>
 * Created by kyle on 2016/12/7.
 */

public interface IProvider {

    /**
     * Just obtain the intermediates but not perform navigation.
     *
     * @return Intent for Activity type, Fragment instance for Fragment type.
     */
    Object obtain();
}
