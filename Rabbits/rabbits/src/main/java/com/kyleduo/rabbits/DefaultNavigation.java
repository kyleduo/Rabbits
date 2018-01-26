package com.kyleduo.rabbits;

/**
 * Created by kyle on 26/01/2018.
 */

public class DefaultNavigation extends AbstractNavigation {

    DefaultNavigation(Rabbit rabbit, Action action) {
        super(rabbit, action);
    }

    @Override
    public DispatchResult start() {
        return rabbit.dispatch(this);
    }

    @Override
    public DispatchResult obtain() {
        justObtain(true);
        return rabbit.dispatch(this);
    }
}
