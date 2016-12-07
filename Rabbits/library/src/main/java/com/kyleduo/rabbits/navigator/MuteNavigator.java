package com.kyleduo.rabbits.navigator;

import android.net.Uri;
import android.os.Bundle;

import java.util.List;

/**
 * Navigator without processing navigation.
 *
 * Created by kyle on 2016/12/7.
 */

public class MuteNavigator extends AbstractNavigator {

	public MuteNavigator(Uri uri, Object from, Object to, String tag, int flags, Bundle extras, List<INavigationInterceptor> interceptors) {
		super(uri, from, to, tag, flags, extras, interceptors);
	}

	@Override
	public boolean start() {
		if (mInterceptors != null) {
			for (INavigationInterceptor i : mInterceptors) {
				if (i.intercept(mUri, mFrom, mTo, mTag, mIntentFlags, mExtras)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean startForResult(int requestCode) {
		if (mInterceptors != null) {
			for (INavigationInterceptor i : mInterceptors) {
				if (i.intercept(mUri, mFrom, mTo, mTag, mIntentFlags, mExtras)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public Object obtain() {
		return null;
	}
}
