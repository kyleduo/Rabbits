package com.kyleduo.rabbits.navigator;

import com.kyleduo.rabbits.Target;

import java.util.List;

/**
 * Navigator without processing navigation.
 *
 * Created by kyle on 2016/12/7.
 */

public class MuteNavigator extends AbstractNavigator {

	public MuteNavigator(Object from, Target target, List<INavigationInterceptor> interceptors) {
		super(from, target, interceptors);
	}

	@Override
	public boolean start() {
		if (mInterceptors != null) {
			for (INavigationInterceptor i : mInterceptors) {
				if (i.intercept(mFrom, mTarget)) {
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
				if (i.intercept(mFrom, mTarget)) {
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
