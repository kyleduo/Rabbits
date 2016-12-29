package com.kyleduo.rabbits.demo.navigation;

import com.kyleduo.rabbits.Target;
import com.kyleduo.rabbits.navigator.AbstractNavigator;
import com.kyleduo.rabbits.navigator.AbstractPageNotFoundHandler;
import com.kyleduo.rabbits.navigator.INavigationInterceptor;
import com.kyleduo.rabbits.navigator.INavigatorFactory;

import java.util.List;

/**
 * Created by kyle on 2016/12/12.
 */

public class DemoNavigatorFactory implements INavigatorFactory {
	@Override
	public AbstractNavigator createNavigator(Object from, Target target, List<INavigationInterceptor> interceptors) {
		return new DemoNavigator(from, target, interceptors);
	}

	@Override
	public AbstractPageNotFoundHandler createPageNotFoundHandler(Object from, Target target, List<INavigationInterceptor> interceptors) {
		return new DemoNotFoundHandler(from, target, interceptors);
	}
}
