package com.kyleduo.rabbits.demo.navigation;

import android.support.v4.app.Fragment;

import com.kyleduo.rabbits.Rabbit;
import com.kyleduo.rabbits.Target;
import com.kyleduo.rabbits.demo.base.BaseFragment;
import com.kyleduo.rabbits.navigator.DefaultNavigator;
import com.kyleduo.rabbits.navigator.INavigationInterceptor;

import java.util.List;

import me.yokeyword.fragmentation.SupportActivity;

/**
 * Created by kyle on 2016/12/12.
 */

public class DemoNavigator extends DefaultNavigator {
	public DemoNavigator(Object from, Target target, List<INavigationInterceptor> interceptors) {
		super(from, target, interceptors);
	}

	@Override
	public boolean start() {
		final Object to = mTarget.getTo();
		if (to instanceof BaseFragment) {
			BaseFragment f = (BaseFragment) obtain();
			if (mFrom instanceof SupportActivity) {
				if (((SupportActivity) mFrom).getTopFragment() == null) {
					Rabbit.from(mFrom)
							.to("/common")
							.putString(Rabbit.KEY_ORIGIN_URI, mTarget.getExtras().getString(Rabbit.KEY_ORIGIN_URI))
							.mergeExtras(mTarget.getExtras())
							.start();
				} else {
					((SupportActivity) mFrom).start(f);
				}
				return true;
			} else if (mFrom instanceof BaseFragment) {
				((BaseFragment) mFrom).start(f);
				return true;
			}
		}
		return super.start();
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

	@Override
	public boolean startForResult(int requestCode) {
		final Object to = mTarget.getTo();
		if (to instanceof BaseFragment) {
			BaseFragment f = (BaseFragment) obtain();
			if (mFrom instanceof SupportActivity) {
				if (((SupportActivity) mFrom).getTopFragment() == null) {
					Rabbit.from(mFrom)
							.to("/common")
							.putString(Rabbit.KEY_ORIGIN_URI, mTarget.getExtras().getString(Rabbit.KEY_ORIGIN_URI))
							.mergeExtras(mTarget.getExtras())
							.startForResult(requestCode);
				} else {
					((SupportActivity) mFrom).startForResult(f, requestCode);
				}
				return true;
			} else if (mFrom instanceof BaseFragment) {
				((BaseFragment) mFrom).startForResult(f, requestCode);
				return true;
			}
		}
		return super.startForResult(requestCode);
	}
}
