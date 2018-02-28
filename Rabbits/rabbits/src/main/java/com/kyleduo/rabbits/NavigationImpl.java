package com.kyleduo.rabbits;

import android.support.annotation.NonNull;

/**
 * Implementation of navigation.
 *
 * Created by kyle on 26/01/2018.
 */

public class NavigationImpl extends AbstractNavigation {
    protected Rabbit rabbit;

    NavigationImpl(Rabbit rabbit, Action action) {
        super(action);
        this.rabbit = rabbit;
        Logger.d("Navigation created. FROM: " + action.getFrom().toString() + " URL: " + action.getOriginUrl());
    }

    @NonNull
    @Override
    public RabbitResult start() {
        return rabbit.dispatch(this);
    }

    @NonNull
    @Override
    public RabbitResult startForResult(int requestCode) {
        forResult(requestCode);
        return start();
    }

    @NonNull
    @Override
    public RabbitResult obtain() {
        justObtain();
        return rabbit.dispatch(this);
    }
}
