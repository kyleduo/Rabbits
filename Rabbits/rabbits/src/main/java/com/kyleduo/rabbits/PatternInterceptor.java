package com.kyleduo.rabbits;

/**
 * Used for Interceptor which wants to be invoked when the url match the pattern
 *
 * Created by kyle on 30/01/2018.
 */

final class PatternInterceptor implements Interceptor {

    private Interceptor mInterceptor;
    private Rule mRule;

    PatternInterceptor(Interceptor interceptor, Rule rule) {
        mInterceptor = interceptor;
        this.mRule = rule;
    }

    @Override
    public RabbitResult intercept(Dispatcher dispatcher) {
        final Action action = dispatcher.action();
        if (mRule != null && mRule.verify(action)) {
            return mInterceptor.intercept(dispatcher);
        }
        return dispatcher.dispatch(dispatcher.action());
    }

    @Override
    public String toString() {
        return "Rule: " + mRule.toString() + " Interceptor: " + mInterceptor.toString();
    }
}
