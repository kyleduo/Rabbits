package com.kyleduo.rabbits.navigator;

import android.net.Uri;
import android.os.Bundle;

import java.util.List;

/**
 * Factory to create Navigator or NotFoundHandler.
 *
 * Created by kyle on 2016/12/7.
 */

public class DefaultNavigatorFactory implements INavigatorFactory {
	@Override
	public AbstractNavigator createNavigator(Uri uri, Object from, Object to, String tag, int flags, Bundle extras, List<INavigationInterceptor> interceptors) {
		return new DefaultNavigator(uri, from, to, tag, flags, extras, interceptors);
	}

	@Override
	public AbstractPageNotFoundHandler createPageNotFoundHandler(Object from, Uri uri, String tag, int flags, Bundle extras, List<INavigationInterceptor> interceptors) {
		return null;
	}
}
