package com.kyleduo.rabbits.navigator;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import java.util.List;

/**
 * Default implements of AbstractNavigator witch has ability of navigate from activities or fragments to Activities.
 *
 * Created by kyle on 2016/12/7.
 */

public class DefaultNavigator extends AbstractNavigator {

	public DefaultNavigator(Uri uri, Object from, Object to, String tag, int flags, Bundle extras, List<INavigationInterceptor> interceptors) {
		super(uri, from, to, tag, flags, extras, interceptors);
	}

	private Intent buildIntent() {
		Context context = null;
		if (mFrom instanceof Context) {
			context = (Context) mFrom;
			if (!(mFrom instanceof Activity)) {
				newTask();
			}
		} else if (mFrom instanceof Fragment) {
			context = ((Fragment) mFrom).getActivity();
		} else if (mFrom instanceof android.app.Fragment) {
			context = ((android.app.Fragment) mFrom).getActivity();
		}
		if (context == null) {
			return null;
		}
		Intent intent = new Intent(context, (Class<?>) mTo);
		intent.setFlags(mIntentFlags);
		if (mExtras != null) {
			intent.putExtras(mExtras);
		}
		return intent;
	}

	@Override
	public boolean start() {
		assert mTo != null;
		if (mInterceptors != null) {
			for (INavigationInterceptor i : mInterceptors) {
				if (i.intercept(mUri, mFrom, mTo, mTag, mIntentFlags, mExtras)) {
					return true;
				}
			}
		}
		if (mTo instanceof Class<?> && Activity.class.isAssignableFrom(((Class<?>) mTo))) {
			Intent intent = buildIntent();
			if (mFrom instanceof Context) {
				((Context) mFrom).startActivity(intent);
				return true;
			} else if (mFrom instanceof Fragment) {
				((Fragment) mFrom).startActivity(intent);
				return true;
			} else if (mFrom instanceof android.app.Fragment) {
				((android.app.Fragment) mFrom).startActivity(intent);
				return true;
			}
		} else if (mTo instanceof AbstractNavigator) {
			return ((AbstractNavigator) mTo).setFrom(mFrom)
					.setIntentFlags(mIntentFlags)
					.mergeExtras(mExtras)
					.start();
		}
		return false;
	}

	@Override
	public Object obtain() {
		assert mTo != null;
		if (mTo instanceof Class<?> && Activity.class.isAssignableFrom(((Class<?>) mTo))) {
			return buildIntent();
		}
		return mTo;
	}

	@Override
	public boolean startForResult(int requestCode) {
		assert mTo != null;
		if (mInterceptors != null) {
			for (INavigationInterceptor i : mInterceptors) {
				if (i.intercept(mUri, mFrom, mTo, mTag, mIntentFlags, mExtras)) {
					return true;
				}
			}
		}
		if (mTo instanceof Class<?> && Activity.class.isAssignableFrom(((Class<?>) mTo))) {
			Intent intent = buildIntent();
			if (mFrom instanceof Activity) {
				((Activity) mFrom).startActivityForResult(intent, requestCode);
				return true;
			} else if (mFrom instanceof Fragment) {
				((Fragment) mFrom).startActivityForResult(intent, requestCode);
				return true;
			}
		}
		return false;
	}
}
