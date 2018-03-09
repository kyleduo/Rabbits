package com.kyleduo.rabbits;

import java.util.List;

/**
 * Created by kyle on 24/01/2018.
 */

public class RealDispatcher implements Interceptor.Dispatcher {

    private Action mAction;
    private List<Interceptor> mInterceptors;
    private int mIndex;

    RealDispatcher(Action action, List<Interceptor> interceptors, int index) {
        mAction = action;
        mInterceptors = interceptors;
        mIndex = index;
    }

    @Override
    public RabbitResult dispatch(Action action) {
        if (mIndex >= mInterceptors.size()) {
            return RabbitResult.error("Action has not been performed.");
        }

        Interceptor interceptor = mInterceptors.get(mIndex);
        if (action.isIgnoreInterceptors()) {
            while (!(interceptor instanceof InternalInterceptor)) {
                interceptor = mInterceptors.get(++mIndex);
            }
        }
        if (!(interceptor instanceof InternalInterceptor)) {
            Logger.d("Interceptor: " + interceptor.toString());
        }

        RealDispatcher next = new RealDispatcher(mAction, mInterceptors, mIndex + 1);

        //noinspection UnnecessaryLocalVariable
        RabbitResult result = interceptor.intercept(next);

        return result;
    }

    @Override
    public Action action() {
        return mAction;
    }
}
