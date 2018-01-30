package com.kyleduo.rabbits;

/**
 * Created by kyle on 30/01/2018.
 */

public class PatternInterceptor implements Interceptor {

    private Interceptor mInterceptor;
    private String pattern;

    public PatternInterceptor(Interceptor interceptor, String pattern) {
        mInterceptor = interceptor;
        this.pattern = pattern;
    }

    @Override
    public DispatchResult intercept(Dispatcher dispatcher) {
        if (dispatcher.action().getUri().toString().matches(this.pattern)) {
            return mInterceptor.intercept(dispatcher);
        }
        return dispatcher.dispatch(dispatcher.action());
    }
}
