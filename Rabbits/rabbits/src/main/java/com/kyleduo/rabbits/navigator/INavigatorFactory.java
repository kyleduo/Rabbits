package com.kyleduo.rabbits.navigator;

import android.net.Uri;
import android.os.Bundle;

import com.kyleduo.rabbits.Target;

import java.util.List;

/**
 * Interface for Navigator factory.
 *
 * Created by kyle on 2016/12/7.
 */

public interface INavigatorFactory {
	AbstractNavigator createNavigator(Object from, Target target, List<INavigationInterceptor> interceptors);

	AbstractPageNotFoundHandler createPageNotFoundHandler(Object from, Target target, List<INavigationInterceptor> interceptors);
}
