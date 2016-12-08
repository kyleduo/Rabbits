package com.kyleduo.rabbits;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import com.kyleduo.rabbits.annotations.utils.NameParser;
import com.kyleduo.rabbits.navigator.AbstractNavigator;
import com.kyleduo.rabbits.navigator.AbstractPageNotFoundHandler;
import com.kyleduo.rabbits.navigator.DefaultNavigatorFactory;
import com.kyleduo.rabbits.navigator.INavigationInterceptor;
import com.kyleduo.rabbits.navigator.INavigatorFactory;
import com.kyleduo.rabbits.navigator.IProvider;
import com.kyleduo.rabbits.navigator.MuteNavigator;

import java.io.File;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Rabbit instance can be obtained by {@link com.kyleduo.rabbits.Rabbit#from(Object)} method.
 * Normal usage likes this.
 * <pre>
 * Rabbit.from(activity)
 * 		.to("http://com.kyleduo.rabbits/some/path")
 * 		.start();
 * </pre>
 * <p>
 * Created by kyle on 2016/12/7.
 */

public class Rabbit {
	private static IRouter sRouter;
	private static String sAppScheme;
	private static INavigatorFactory sNavigatorFactory;
	private static List<INavigationInterceptor> sInterceptors;

	private Object mFrom;
	private List<INavigationInterceptor> mInterceptors;

	private Rabbit(Object from) {
		mFrom = from;
		if (sRouter == null) {
			sRouter = (IRouter) Proxy.newProxyInstance(IRouter.class.getClassLoader(), new Class[]{IRouter.class}, new InvocationHandler() {

				Class<?> clz;
				Map<String, Method> methods = new LinkedHashMap<>();

				@Override
				public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
					if (clz == null) {
						clz = Class.forName("com.kyleduo.rabbits.Router");
					}
					String page = (String) objects[0];
					String name = method.getName();

					String key = name + "-" + page;

					Method m = methods.get(key);
					if (m != null) {
						return m.invoke(null);
					} else {
						if (name.equals(IRouter.METHOD_ROUTE)) {
							String methodName = NameParser.parseRoute(page);
							m = clz.getMethod(methodName);
							if (m != null) {
								methods.put(key, m);
								return m.invoke(null);
							}
						} else if (name.equals(IRouter.METHOD_OBTAIN)) {
							String methodName = NameParser.parseObtain(page);
							m = clz.getMethod(methodName);
							if (m != null) {
								methods.put(key, m);
								return m.invoke(null);
							}
						}
					}

					return null;
				}
			});
		}
	}

	public static void asyncSetup(Context context, String scheme, Runnable callback) {
		asyncSetup(context, scheme, new DefaultNavigatorFactory(), callback);
	}

	public static void asyncSetup(Context context, String scheme, INavigatorFactory navigatorFactory, Runnable callback) {
		setup(context, scheme, navigatorFactory, true, callback);
	}

	public static void setup(Context context, String scheme) {
		setup(context, scheme, new DefaultNavigatorFactory());
	}

	public static void setup(Context context, String scheme, INavigatorFactory navigatorFactory) {
		setup(context, scheme, navigatorFactory, false, null);
	}

	private static void setup(Context context, String scheme, INavigatorFactory navigatorFactory, boolean async, Runnable callback) {
		sAppScheme = scheme;
		sNavigatorFactory = navigatorFactory;
		Mappings.setup(context, async, callback);
	}

	public static void updateMappings(Context context, File file) {
		Mappings.update(context, file);
	}

	public static void updateMappings(Context context, String json) {
		Mappings.update(context, json);
	}

	public static Rabbit from(Object from) {
		return new Rabbit(from);
	}

	public static void addGlobalInterceptor(INavigationInterceptor interceptor) {
		if (sInterceptors == null) {
			sInterceptors = new ArrayList<>();
		}
		sInterceptors.add(interceptor);
	}

	public Rabbit addInterceptor(INavigationInterceptor interceptor) {
		if (mInterceptors == null) {
			mInterceptors = new ArrayList<>();
		}
		mInterceptors.add(interceptor);
		return this;
	}

	/**
	 * Used for obtain page object. Intent or Fragment instance.
	 *
	 * @param uri uri
	 * @return IProvider
	 */
	public IProvider obtain(Uri uri) {
		String path = uri.buildUpon().clearQuery().build().toString();
		String page = Mappings.match(path);
		Object to = null;
		if (!TextUtils.isEmpty(page)) {
			to = sRouter.obtain(page);
		}
		int flags = parseFlags(uri);
		if (to == null) {
			AbstractPageNotFoundHandler pageNotFoundHandler = sNavigatorFactory.createPageNotFoundHandler(mFrom, uri, page, flags, null, assembleInterceptor());
			if (pageNotFoundHandler != null) {
				return pageNotFoundHandler;
			}
		}
		return sNavigatorFactory.createNavigator(uri, mFrom, to, page, flags, parseParams(uri), assembleInterceptor());
	}

	/**
	 * Used for obtain page object. Intent or Fragment instance.
	 *
	 * @param uriStr uriStr
	 * @return IProvider
	 */
	public IProvider obtain(String uriStr) {
		Uri uri = Uri.parse(uriStr);
		return obtain(uri);
	}

	/**
	 * Navigate to page, or perform a not found strategy.
	 *
	 * @param uri uri
	 * @return AbstractNavigator
	 */
	public AbstractNavigator to(Uri uri) {
		String path = uri.buildUpon().clearQuery().build().toString();
		String page = Mappings.match(path);
		Object to = null;
		if (!TextUtils.isEmpty(page)) {
			to = sRouter.route(page);
		}
		int flags = parseFlags(uri);
		if (to == null) {
			AbstractPageNotFoundHandler pageNotFoundHandler = sNavigatorFactory.createPageNotFoundHandler(mFrom, uri, page, flags, null, assembleInterceptor());
			if (pageNotFoundHandler != null) {
				return pageNotFoundHandler;
			}
		}
		return sNavigatorFactory.createNavigator(uri, mFrom, to, page, flags, parseParams(uri), assembleInterceptor());
	}

	/**
	 * Navigate to page, or perform a not found strategy.
	 *
	 * @param uriStr uri string
	 * @return AbstractNavigator
	 */
	public AbstractNavigator to(String uriStr) {
		Uri uri = Uri.parse(uriStr);
		return to(uri);
	}

	/**
	 * Navigate to page, or just return null if not found.
	 *
	 * @param uriStr uri
	 * @return AbstractNavigator
	 */
	public AbstractNavigator tryTo(String uriStr) {
		Uri uri = Uri.parse(uriStr);
		return tryTo(uri);
	}

	/**
	 * First replace scheme to app scheme.
	 * <p>
	 * Navigate to page, or just return null if not found.
	 *
	 * @param uri uri
	 * @return AbstractNavigator
	 */
	public AbstractNavigator tryTo(Uri uri) {
		String path = uri.buildUpon().clearQuery().scheme(sAppScheme).build().toString();
		String page = Mappings.match(path);
		Object to = null;
		if (!TextUtils.isEmpty(page)) {
			to = sRouter.route(page);
		}
		int flags = parseFlags(uri);
		if (to == null) {
			return new MuteNavigator(uri, mFrom, null, page, flags, null, mInterceptors);
		}
		return sNavigatorFactory.createNavigator(uri, mFrom, to, page, flags, parseParams(uri), mInterceptors);
	}

	private static int parseFlags(Uri uri) {
		String mode = uri.getQueryParameter(Mappings.MAPPING_QUERY_MODE);
		int flags = 0;
		if (TextUtils.isEmpty(mode)) {
			flags = 0;
		} else {
			if (mode.contains(Mappings.MODE_CLEAR_TOP)) {
				flags |= Intent.FLAG_ACTIVITY_CLEAR_TOP;
			}
			if (mode.contains(Mappings.MODE_NEW_TASK)) {
				flags |= Intent.FLAG_ACTIVITY_NEW_TASK;
			}
		}
		return flags;
	}

	private static Bundle parseParams(Uri uri) {
		Set<String> keys = uri.getQueryParameterNames();
		if (keys == null || keys.size() == 0) {
			return null;
		}
		Bundle bundle = new Bundle();
		for (String key : keys) {
			String params = uri.getQueryParameter(key);
			bundle.putString(key, params);
		}
		return bundle;
	}

	/**
	 * Assemble interceptors and static interceptors.
	 * order static interceptors after instance's interceptors.
	 *
	 * @return
	 */
	private List<INavigationInterceptor> assembleInterceptor() {
		if (sInterceptors == null) {
			return mInterceptors;
		}
		if (mInterceptors == null) {
			mInterceptors = new ArrayList<>();
		}
		mInterceptors.addAll(sInterceptors);
		return mInterceptors;
	}
}
