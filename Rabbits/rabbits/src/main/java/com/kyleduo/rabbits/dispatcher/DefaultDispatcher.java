package com.kyleduo.rabbits.dispatcher;

import com.kyleduo.rabbits.Target;

/**
 * Created by kyle on 19/12/2017.
 */

public class DefaultDispatcher implements IDispatcher {

    @Override
    public DispatchResult dispatch(Target target) {
        final Object from = target.getFrom();


        return null;
    }
}
