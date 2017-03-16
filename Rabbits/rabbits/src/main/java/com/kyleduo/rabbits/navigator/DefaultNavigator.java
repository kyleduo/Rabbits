package com.kyleduo.rabbits.navigator;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
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
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				context = ((android.app.Fragment) mFrom).getActivity();
			}
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
	public boolean handleStart(int requestCode) {
		final Object to = mTarget.getTo();
		if (to instanceof Class<?> && Activity.class.isAssignableFrom(((Class<?>) to))) {
			Intent intent = buildIntent();
			if (mFrom instanceof Activity) {
				Activity activity = (Activity) mFrom;
				if (requestCode >= 0) {
					activity.startActivityForResult(intent, requestCode);
				} else {
					activity.startActivity(intent);
				}
				if (mTarget.shouldFinishPrevious()) {
					activity.finish();
				}
				return true;
			} else if (mFrom instanceof Context) {
				((Context) mFrom).startActivity(intent);
				return true;
			} else if (mFrom instanceof Fragment) {
				Fragment fragment = (Fragment) mFrom;
				if (requestCode >= 0) {
					fragment.startActivityForResult(intent, requestCode);
				} else {
					fragment.startActivity(intent);
					if (mTarget.shouldFinishPrevious() && fragment.getActivity() != null) {
						fragment.getActivity().finish();
					}
				}
				return true;
			} else if (mFrom instanceof android.app.Fragment) {
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
					android.app.Fragment fragment = (android.app.Fragment) mFrom;
					if (requestCode >= 0) {
						fragment.startActivityForResult(intent, requestCode);
					} else {
						fragment.startActivity(intent);
						if (mTarget.shouldFinishPrevious() && fragment.getActivity() != null) {
							fragment.getActivity().finish();
						}
					}
				}
				return true;
			}
		} else if (to instanceof AbstractNavigator) {
			AbstractNavigator navigator = ((AbstractNavigator) to).setFrom(mFrom)
					.setIntentFlags(mTarget.getFlags())
					.mergeExtras(mTarget.getExtras());
			if (requestCode >= 0) {
				navigator.startForResult(requestCode);
			} else {
				navigator.start();
				if (mTarget.shouldFinishPrevious()) {
					if (mFrom instanceof Activity) {
						((Activity) mFrom).finish();
					} else if (mFrom instanceof Fragment) {
						((Fragment) mFrom).getActivity().finish();
					} else if (mFrom instanceof android.app.Fragment) {
						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
							((android.app.Fragment) mFrom).getActivity().finish();
						}
					}
				}
			}
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
}
