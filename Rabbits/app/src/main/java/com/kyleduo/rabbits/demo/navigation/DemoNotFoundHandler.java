package com.kyleduo.rabbits.demo.navigation;

import android.net.Uri;
import android.os.Bundle;

import com.kyleduo.rabbits.navigator.AbstractPageNotFoundHandler;
import com.kyleduo.rabbits.navigator.INavigationInterceptor;

import java.util.List;

/**
 * Created by kyle on 2016/12/12.
 */

public class DemoNotFoundHandler extends AbstractPageNotFoundHandler {

	public DemoNotFoundHandler(Object from, Uri uri, String tag, int flags, Bundle extras, List<INavigationInterceptor> interceptors) {
		super(from, uri, tag, flags, extras, interceptors);
	}

	@Override
	public boolean start() {
		return false;
	}

	@Override
	public boolean startForResult(int requestCode) {
		return false;
	}

	@Override
	public Object obtain() {
		return null;
	}
}
