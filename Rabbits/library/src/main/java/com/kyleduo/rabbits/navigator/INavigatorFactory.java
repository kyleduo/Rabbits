package com.kyleduo.rabbits.navigator;

import android.net.Uri;
import android.os.Bundle;

import java.util.List;

/**
 * Interface for Navigator factory.
 *
 * Created by kyle on 2016/12/7.
 */

public interface INavigatorFactory {
	AbstractNavigator createNavigator(Uri uri, Object from, Object to, String tag, int flags, Bundle extras, List<INavigationInterceptor> interceptors);

	AbstractPageNotFoundHandler createPageNotFoundHandler(Object from, Uri uri, String tag, int flags, Bundle extras, List<INavigationInterceptor> interceptors);
}
