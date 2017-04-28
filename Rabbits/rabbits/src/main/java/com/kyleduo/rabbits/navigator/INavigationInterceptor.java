package com.kyleduo.rabbits.navigator;

import com.kyleduo.rabbits.Target;

/**
 * Interface of interceptor.
 * <p>
 * Created by kyle on 2016/12/7.
 */

public interface INavigationInterceptor {
    boolean intercept(Object from, Target target);
}
