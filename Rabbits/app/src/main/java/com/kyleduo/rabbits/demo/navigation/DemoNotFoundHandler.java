package com.kyleduo.rabbits.demo.navigation;

import com.kyleduo.rabbits.Rabbit;
import com.kyleduo.rabbits.Target;
import com.kyleduo.rabbits.navigator.AbstractPageNotFoundHandler;
import com.kyleduo.rabbits.navigator.INavigationInterceptor;

import java.util.List;

/**
 * Created by kyle on 2016/12/12.
 */

public class DemoNotFoundHandler extends AbstractPageNotFoundHandler {
	public static final String HTTP_SCHEME = "https";

	public DemoNotFoundHandler(Object from, Target target, List<INavigationInterceptor> interceptors) {
		super(from, target, interceptors);
	}

	@Override
	public boolean start() {
		String httpUrl = mTarget.getUri().buildUpon().scheme(HTTP_SCHEME).build().toString();
		Rabbit.from(mFrom)
				.to("/web")
				.putString("url", httpUrl)
				.start();
		return true;
	}

	@Override
	public boolean startForResult(int requestCode) {
		String httpUrl = mTarget.getUri().buildUpon().scheme(HTTP_SCHEME).build().toString();
		Rabbit.from(mFrom)
				.to("/web")
				.putString("url", httpUrl)
				.startForResult(requestCode);
		return true;
	}

	@Override
	public Object obtain() {
		String httpUrl = mTarget.getUri().buildUpon().scheme(HTTP_SCHEME).build().toString();
		return Rabbit.from(mFrom)
				.obtain("/web")
				.putString("url", httpUrl)
				.obtain();
	}
}
