package com.kyleduo.rabbits.interceptor;

import com.kyleduo.rabbits.Target;
import com.kyleduo.rabbits.dispatcher.DispatchResult;
import com.kyleduo.rabbits.dispatcher.IDispatcher;

/**
 * Created by kyle on 19/12/2017.
 */

public class Interceptor implements IDispatcher {

    protected IDispatcher mNext;

    public Interceptor(IDispatcher next) {
        mNext = next;
    }

    @Override
    public DispatchResult dispatch(Target target) {
        return mNext.dispatch(target);
    }
}
