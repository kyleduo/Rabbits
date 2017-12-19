package com.kyleduo.rabbits;

import com.kyleduo.rabbits.dispatcher.DispatchResult;

/**
 * Created by kyle on 19/12/2017.
 */

public interface Callback {
    void callback(DispatchResult result);
}
