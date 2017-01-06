package com.kyleduo.rabbits.demo;

import android.app.Application;

import com.kyleduo.rabbits.Rabbit;
import com.kyleduo.rabbits.Target;
import com.kyleduo.rabbits.demo.navigation.DemoNavigatorFactory;
import com.kyleduo.rabbits.demo.utils.UriUtils;
import com.kyleduo.rabbits.navigator.INavigationInterceptor;

/**
 * Created by kyle on 2016/12/8.
 */

public class DemoApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();

		Rabbit.init("demo", "rabbits.kyleduo.com", new DemoNavigatorFactory());

		// syc setup
//		Rabbit.setup(this);

		// async setup
		Rabbit.asyncSetup(this, new Runnable() {
			@Override
			public void run() {

			}
		});

		Rabbit.addGlobalInterceptor(new INavigationInterceptor() {
			@Override
			public boolean intercept(Object from, Target target) {
				if (UriUtils.matchPath(target.getUri(), "/intercept/dump")) {
					Rabbit.from(from)
							.to("/dump")
							.mergeExtras(target.getExtras())
							.start();
					return true;
				}
				return false;
			}
		});
	}
}
