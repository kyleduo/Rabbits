package com.kyleduo.rabbits;

/**
 * Used for Interceptor which wants to be invoked when the url match the pattern
 *
 * Created by kyle on 30/01/2018.
 */

final class PatternInterceptor implements Interceptor {

    private Interceptor mInterceptor;
    private String pattern;

    // TODO: 09/02/2018 more elegant match rule
    PatternInterceptor(Interceptor interceptor, String pattern) {
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
