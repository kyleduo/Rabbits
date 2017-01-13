package com.kyleduo.rabbits.demo.navigation;

import com.kyleduo.rabbits.Rabbit;
import com.kyleduo.rabbits.Target;
import com.kyleduo.rabbits.navigator.AbstractNavigator;
import com.kyleduo.rabbits.navigator.AbstractPageNotFoundHandler;
import com.kyleduo.rabbits.navigator.INavigationInterceptor;

import java.util.List;

/**
 * Created by kyle on 2016/12/12.
 */

class DemoNotFoundHandler extends AbstractPageNotFoundHandler {
	private static final String HTTP_SCHEME = "https";

	DemoNotFoundHandler(Object from, Target target, List<INavigationInterceptor> interceptors) {
		super(from, target, interceptors);
	}

	@Override
	protected boolean handleStart(int requestCode) {
		String httpUrl = mTarget.getUri().buildUpon().scheme(HTTP_SCHEME).build().toString();
		AbstractNavigator navigator = Rabbit.from(mFrom)
				.to("/web")
				.putExtra("url", httpUrl)
				.mergeExtras(mTarget.getExtras());
		if (requestCode >= 0) {
			navigator.startForResult(requestCode);
		} else {
			navigator.start();
		}
		return false;
	}

	@Override
	public Object obtain() {
		String httpUrl = mTarget.getUri().buildUpon().scheme(HTTP_SCHEME).build().toString();
		return Rabbit.from(mFrom)
				.obtain("/web")
				.putExtra("url", httpUrl)
				.mergeExtras(mTarget.getExtras())
				.obtain();
	}
}
