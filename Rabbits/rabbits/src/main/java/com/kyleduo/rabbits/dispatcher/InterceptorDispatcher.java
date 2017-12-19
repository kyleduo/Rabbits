package com.kyleduo.rabbits.dispatcher;

import android.support.annotation.NonNull;

import com.kyleduo.rabbits.Target;
import com.kyleduo.rabbits.interceptor.IInterceptor;

/**
 * Created by kyle on 19/12/2017.
 */

public class InterceptorDispatcher implements IDispatcher {

    private IInterceptor mInterceptor;
    private IDispatcher mDispatcher;

    public InterceptorDispatcher(@NonNull IInterceptor interceptor, @NonNull IDispatcher dispatcher) {
        mInterceptor = interceptor;
        mDispatcher = dispatcher;
    }

    @Override
    public DispatchResult dispatch(Target target) {
        return mInterceptor.dispatch(mDispatcher, target);
    }
}
