package com.kyleduo.rabbits.navigator;

import com.kyleduo.rabbits.Target;

import java.util.List;

/**
 * Interface for Navigator factory.
 * <p>
 * Created by kyle on 2016/12/7.
 */

public interface INavigatorFactory {
    AbstractNavigator createNavigator(Object from, Target target, List<INavigationInterceptor> interceptors);

    AbstractPageNotFoundHandler createPageNotFoundHandler(Object from, Target target, List<INavigationInterceptor> interceptors);
}
