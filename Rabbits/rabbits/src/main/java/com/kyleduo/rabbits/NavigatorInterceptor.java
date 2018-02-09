package com.kyleduo.rabbits;

import android.util.SparseArray;

import com.kyleduo.rabbits.annotations.TargetInfo;

/**
 * Created by kyle on 26/01/2018.
 */

class NavigatorInterceptor implements Interceptor {

    private SparseArray<Navigator> mNavigators;

    NavigatorInterceptor(SparseArray<Navigator> navigators) {
        mNavigators = navigators;
    }

    @Override
    public DispatchResult intercept(Dispatcher dispatcher) {
        if (mNavigators == null) {
            throw new NullPointerException("No valid navigator");
        }
        Action action = dispatcher.action();
        boolean notFound = false;
        if (action.getTarget() == null || action.getTargetType() == TargetInfo.TYPE_NOT_FOUND) {
            notFound = true;
            if (action.isIgnoreFallback()) {
                return DispatchResult.notFound(action.getOriginUrl());
            }
        }
        // Normal navigation or fallback navigation all handled here.
        Navigator navigator = mNavigators.get(action.getTargetType());
        if (navigator == null) {
            if (notFound) {
                // need to be handled by fallback.
                // fallback handler isn't set.
                return DispatchResult.notFound(action.getOriginUrl());
            } else {
                throw new IllegalStateException("Navigator not found");
            }
        }
        return navigator.perform(action);
    }
}
