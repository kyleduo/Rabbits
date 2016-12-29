package com.kyleduo.rabbits.navigator;

import android.net.Uri;
import android.os.Bundle;

import com.kyleduo.rabbits.Target;

import java.util.List;

/**
 * Abstract handler for not found page.
 * Project should implement this and handle not found situation.
 *
 * Created by kyle on 2016/12/7.
 */

public abstract class AbstractPageNotFoundHandler extends AbstractNavigator {

	public AbstractPageNotFoundHandler(Object from, Target target, List<INavigationInterceptor> interceptors) {
		super(from, target, interceptors);
	}
}
