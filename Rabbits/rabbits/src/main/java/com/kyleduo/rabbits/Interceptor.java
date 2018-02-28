package com.kyleduo.rabbits;

/**
 * Abstract interceptor used in mAction process chain.
 * Every process in the chain is an interceptor.
 *
 * Created by kyle on 19/12/2017.
 */

public interface Interceptor {
    RabbitResult intercept(Dispatcher dispatcher);

    interface Dispatcher {
        RabbitResult dispatch(Action action);

        Action action();
    }
}
