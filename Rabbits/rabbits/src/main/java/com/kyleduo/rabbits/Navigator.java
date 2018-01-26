package com.kyleduo.rabbits;

/**
 * Created by kyle on 26/01/2018.
 */

public interface Navigator {

    DispatchResult perform(Action action, DispatchResult result);

}
