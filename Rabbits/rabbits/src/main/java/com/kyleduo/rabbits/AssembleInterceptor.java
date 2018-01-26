package com.kyleduo.rabbits;

/**
 * Created by kyle on 26/01/2018.
 */

public class AssembleInterceptor implements Interceptor {
    @Override
    public DispatchResult intercept(Dispatcher dispatcher) {
        Action action = dispatcher.action();


        return dispatcher.dispatch(action);
    }
}
