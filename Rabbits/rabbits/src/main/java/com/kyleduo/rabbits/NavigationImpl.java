package com.kyleduo.rabbits;

import android.support.annotation.NonNull;

/**
 *
 * Created by kyle on 26/01/2018.
 */

public class NavigationImpl extends AbstractNavigation {
    protected Rabbit rabbit;

    NavigationImpl(Rabbit rabbit, Action action) {
        super(action);
        this.rabbit = rabbit;
    }

    @NonNull
    @Override
    public DispatchResult start() {
        return rabbit.dispatch(this);
    }

    @NonNull
    @Override
    public DispatchResult startForResult(int requestCode) {
        forResult(requestCode);
        return start();
    }

    @NonNull
    @Override
    public DispatchResult obtain() {
        justObtain();
        return rabbit.dispatch(this);
    }
}
