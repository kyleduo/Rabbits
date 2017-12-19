package com.kyleduo.rabbits.interceptor;

import com.kyleduo.rabbits.Target;
import com.kyleduo.rabbits.dispatcher.DispatchResult;
import com.kyleduo.rabbits.dispatcher.IDispatcher;

/**
 * Created by kyle on 19/12/2017.
 */

public interface IInterceptor {
    DispatchResult dispatch(IDispatcher dispatcher, Target target);
}
