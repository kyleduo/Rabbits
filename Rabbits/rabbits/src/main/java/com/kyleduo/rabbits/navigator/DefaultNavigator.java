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
				int[] anim = mTarget.getTransitionAnimations();
				if (anim != null && anim.length == 2) {
					activity.overridePendingTransition(anim[0], anim[1]);
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
					int[] anim = mTarget.getTransitionAnimations();
					if (anim != null && anim.length == 2) {
						fragment.getActivity().overridePendingTransition(anim[0], anim[1]);
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
						int[] anim = mTarget.getTransitionAnimations();
						if (anim != null && anim.length == 2) {
							fragment.getActivity().overridePendingTransition(anim[0], anim[1]);
						}
					}
				}
				return true;
			}
		} else if (to instanceof AbstractNavigator) {
			boolean ret;
			AbstractNavigator navigator = ((AbstractNavigator) to).setFrom(mFrom)
					.setIntentFlags(mTarget.getFlags())
					.mergeExtras(mTarget.getExtras());
			if (requestCode >= 0) {
				ret = navigator.startForResult(requestCode);
			} else {
				ret = navigator.start();
				int[] anim = mTarget.getTransitionAnimations();
				if (mTarget.shouldFinishPrevious()) {
					if (mFrom instanceof Activity) {
						((Activity) mFrom).finish();
						if (anim != null && anim.length == 2) {
							((Activity) mFrom).overridePendingTransition(anim[0], anim[1]);
						}
					} else if (mFrom instanceof Fragment) {
						((Fragment) mFrom).getActivity().finish();
						if (anim != null && anim.length == 2) {
							((Fragment) mFrom).getActivity().overridePendingTransition(anim[0], anim[1]);
						}
					} else if (mFrom instanceof android.app.Fragment) {
						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
							((android.app.Fragment) mFrom).getActivity().finish();
							if (anim != null && anim.length == 2) {
								((android.app.Fragment) mFrom).getActivity().overridePendingTransition(anim[0], anim[1]);
							}
						}
					}
				}
			}
			return ret;
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
