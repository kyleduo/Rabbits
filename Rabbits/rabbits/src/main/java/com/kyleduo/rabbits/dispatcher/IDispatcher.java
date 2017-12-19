package com.kyleduo.rabbits.dispatcher;

import com.kyleduo.rabbits.Target;

/**
 * Created by kyle on 19/12/2017.
 */

public interface IDispatcher {
    DispatchResult dispatch(Target target);
}
