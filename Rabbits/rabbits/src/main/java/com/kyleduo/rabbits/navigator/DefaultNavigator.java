package com.kyleduo.rabbits.navigator;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.kyleduo.rabbits.Target;

import java.util.List;

/**
 * Default implements of AbstractNavigator witch has ability of navigate from activities or fragments to Activities.
 * <p>
 * Created by kyle on 2016/12/7.
 */

public class DefaultNavigator extends AbstractNavigator {

	public DefaultNavigator(Object from, Target target, List<INavigationInterceptor> interceptors) {
		super(from, target, interceptors);
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
		Intent intent = new Intent(context, (Class<?>) mTarget.getTo());
		intent.setFlags(mTarget.getFlags());
		if (mTarget.getExtras() != null) {
			intent.putExtras(mTarget.getExtras());
		}
		return intent;
	}

	@Override
	public boolean start() {
		final Object to = mTarget.getTo();
		if (to == null) {
			return false;
		}
		if (mInterceptors != null) {
			for (INavigationInterceptor i : mInterceptors) {
				if (i.intercept(mFrom, mTarget)) {
					return true;
				}
			}
		}
		if (to instanceof Class<?> && Activity.class.isAssignableFrom(((Class<?>) to))) {
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
		} else if (to instanceof AbstractNavigator) {
			return ((AbstractNavigator) to).setFrom(mFrom)
					.setIntentFlags(mTarget.getFlags())
					.mergeExtras(mTarget.getExtras())
					.start();
		}
		return false;
	}

	@Override
	public Object obtain() {
		final Object to = mTarget.getTo();
		if (to == null) {
			return null;
		}
		if (to instanceof Class<?> && Activity.class.isAssignableFrom(((Class<?>) to))) {
			return buildIntent();
		}
		return to;
	}

	@Override
	public boolean startForResult(int requestCode) {
		final Object to = mTarget.getTo();
		if (to == null) {
			return false;
		}
		if (mInterceptors != null) {
			for (INavigationInterceptor i : mInterceptors) {
				if (i.intercept(mFrom, mTarget)) {
					return true;
				}
			}
		}
		if (to instanceof Class<?> && Activity.class.isAssignableFrom(((Class<?>) to))) {
			Intent intent = buildIntent();
			if (mFrom instanceof Activity) {
				((Activity) mFrom).startActivityForResult(intent, requestCode);
				return true;
			} else if (mFrom instanceof Fragment) {
				((Fragment) mFrom).startActivityForResult(intent, requestCode);
				return true;
			}
		} else if (to instanceof AbstractNavigator) {
			return ((AbstractNavigator) to).setFrom(mFrom)
					.setIntentFlags(mTarget.getFlags())
					.mergeExtras(mTarget.getExtras())
					.startForResult(requestCode);
		}
		return false;
	}
}
