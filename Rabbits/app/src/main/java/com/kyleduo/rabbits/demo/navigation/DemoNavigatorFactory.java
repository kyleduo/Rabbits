package com.kyleduo.rabbits.demo.navigation;

import android.net.Uri;
import android.os.Bundle;

import com.kyleduo.rabbits.navigator.AbstractNavigator;
import com.kyleduo.rabbits.navigator.AbstractPageNotFoundHandler;
import com.kyleduo.rabbits.navigator.DefaultNavigatorFactory;
import com.kyleduo.rabbits.navigator.INavigationInterceptor;

import java.util.List;

/**
 * Created by kyle on 2016/12/12.
 */

public class DemoNavigatorFactory extends DefaultNavigatorFactory {
	@Override
	public AbstractNavigator createNavigator(Uri uri, Object from, Object to, String tag, int flags, Bundle extras, List<INavigationInterceptor> interceptors) {
		return new DemoNavigator(uri, from, to, tag, flags, extras, interceptors);
	}

	@Override
	public AbstractPageNotFoundHandler createPageNotFoundHandler(Object from, Uri uri, String tag, int flags, Bundle extras, List<INavigationInterceptor> interceptors) {
		return new DemoNotFoundHandler(from, uri, tag, flags, extras, interceptors);
	}
}
