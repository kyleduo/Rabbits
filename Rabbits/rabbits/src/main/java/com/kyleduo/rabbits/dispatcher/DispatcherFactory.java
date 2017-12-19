package com.kyleduo.rabbits.dispatcher;

/**
 * Created by kyle on 19/12/2017.
 */

public class DispatcherFactory {
    public IDispatcher createDispatcher() {
        return new DefaultDispatcher();
    }
}
