package com.kyleduo.rabbits.navigator;

import com.kyleduo.rabbits.Target;

import java.util.List;

/**
 * Navigator without processing navigation.
 * <p>
 * Created by kyle on 2016/12/7.
 */

public class MuteNavigator extends AbstractNavigator {

	public MuteNavigator(Object from, Target target, List<INavigationInterceptor> interceptors) {
		super(from, target, interceptors);
	}

	@Override
	protected boolean handleStart(int requestCode) {
		return false;
	}

	@Override
	public Object obtain() {
		return null;
	}
}
