package com.kyleduo.rabbits;

/**
 * Abstract interceptor used in action process chain.
 * Every process in the chain is an interceptor.
 *
 * Created by kyle on 19/12/2017.
 */

public interface Interceptor {
    DispatchResult intercept(Dispatcher dispatcher);

    interface Dispatcher {
        DispatchResult dispatch(Action action);

        Action action();
    }
}
