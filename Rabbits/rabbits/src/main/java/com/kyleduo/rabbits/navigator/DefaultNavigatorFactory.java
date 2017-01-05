package com.kyleduo.rabbits.navigator;

import com.kyleduo.rabbits.Target;

import java.util.List;

/**
 * Factory to create Navigator or NotFoundHandler.
 *
 * Created by kyle on 2016/12/7.
 */

public class DefaultNavigatorFactory implements INavigatorFactory {
	@Override
	public AbstractNavigator createNavigator(Object from, Target target, List<INavigationInterceptor> interceptors) {
		return new DefaultNavigator(from, target, interceptors);
	}

	@Override
	public AbstractPageNotFoundHandler createPageNotFoundHandler(Object from, Target target, List<INavigationInterceptor> interceptors) {
		return null;
	}
}
