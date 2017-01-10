package com.kyleduo.rabbits.demo.navigation;

import android.support.v4.app.Fragment;

import com.kyleduo.rabbits.Rabbit;
import com.kyleduo.rabbits.Target;
import com.kyleduo.rabbits.demo.base.BaseFragment;
import com.kyleduo.rabbits.navigator.AbstractNavigator;
import com.kyleduo.rabbits.navigator.DefaultNavigator;
import com.kyleduo.rabbits.navigator.INavigationInterceptor;

import java.util.List;

import me.yokeyword.fragmentation.SupportActivity;

/**
 * Created by kyle on 2016/12/12.
 */

class DemoNavigator extends DefaultNavigator {
	DemoNavigator(Object from, Target target, List<INavigationInterceptor> interceptors) {
		super(from, target, interceptors);
	}

	@Override
	public boolean handleStart(int requestCode) {
		final Object to = mTarget.getTo();
		if (to instanceof BaseFragment) {
			BaseFragment f = (BaseFragment) obtain();
			if (mFrom instanceof SupportActivity) {
				SupportActivity activity = (SupportActivity) mFrom;
				if (activity.getTopFragment() == null) {
					AbstractNavigator navigator = Rabbit.from(mFrom)
							.to("/common")
							.putExtra(Rabbit.KEY_ORIGIN_URI, mTarget.getExtras().getString(Rabbit.KEY_ORIGIN_URI))
							.mergeExtras(mTarget.getExtras());
					if (requestCode >= 0) {
						navigator.startForResult(requestCode);
					} else {
						navigator.start();
					}
				} else {
					activity.start(f);
				}
				return true;
			} else if (mFrom instanceof BaseFragment) {
				BaseFragment fragment = (BaseFragment) mFrom;
				if (requestCode >= 0) {
					fragment.startForResult(f, requestCode);
				} else {
					fragment.start(f);
				}
				return true;
			}
		}
		return super.handleStart(requestCode);
	}

	@Override
	public Object obtain() {
		final Object to = mTarget.getTo();
		if (to instanceof Fragment) {
			final Fragment f = (Fragment) to;
			if (mTarget.getExtras() != null) {
				if (f.getArguments() != null) {
					f.getArguments().putAll(mTarget.getExtras());
				} else {
					f.setArguments(mTarget.getExtras());
				}
			}
			return f;
		}
		return super.obtain();
	}
}
