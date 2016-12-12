package com.kyleduo.rabbits.demo.navigation;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.kyleduo.rabbits.Rabbit;
import com.kyleduo.rabbits.demo.base.BaseFragment;
import com.kyleduo.rabbits.navigator.DefaultNavigator;
import com.kyleduo.rabbits.navigator.INavigationInterceptor;

import java.util.List;

import me.yokeyword.fragmentation.SupportActivity;

/**
 * Created by kyle on 2016/12/12.
 */

public class DemoNavigator extends DefaultNavigator {
	public DemoNavigator(Uri uri, Object from, Object to, String tag, int flags, Bundle extras, List<INavigationInterceptor> interceptors) {
		super(uri, from, to, tag, flags, extras, interceptors);
	}

	@Override
	public boolean start() {
		if (mTo instanceof BaseFragment) {
			BaseFragment f = (BaseFragment) obtain();
			if (mFrom instanceof SupportActivity) {
				if (((SupportActivity) mFrom).getTopFragment() == null) {
					Rabbit.from(mFrom)
							.to("/common")
							.putString(Rabbit.KEY_ORIGIN_URI, mExtras.getString(Rabbit.KEY_ORIGIN_URI))
							.mergeExtras(mExtras)
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
		if (mTo instanceof Fragment) {
			final Fragment f = (Fragment) mTo;
			if (mExtras != null) {
				f.setArguments(mExtras);
			}
			return f;
		}
		return super.obtain();
	}

	@Override
	public boolean startForResult(int requestCode) {
		if (mTo instanceof BaseFragment) {
			BaseFragment f = (BaseFragment) obtain();
			if (mFrom instanceof SupportActivity) {
				if (((SupportActivity) mFrom).getTopFragment() == null) {
					Rabbit.from(mFrom)
							.to("/common")
							.putString(Rabbit.KEY_ORIGIN_URI, mExtras.getString(Rabbit.KEY_ORIGIN_URI))
							.mergeExtras(mExtras)
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
