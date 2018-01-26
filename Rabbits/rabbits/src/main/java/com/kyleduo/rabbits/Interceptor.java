package com.kyleduo.rabbits;

import com.kyleduo.rabbits.Action;
import com.kyleduo.rabbits.DispatchResult;

/**
 * Created by kyle on 19/12/2017.
 */

public interface Interceptor {
    DispatchResult intercept(Dispatcher dispatcher);

    interface Dispatcher {
        DispatchResult dispatch(Action action);

        Action action();
    }
}
