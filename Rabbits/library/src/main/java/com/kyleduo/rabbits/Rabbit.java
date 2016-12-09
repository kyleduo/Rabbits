package com.kyleduo.rabbits;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

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
	private static final String TAG = "Rabbit";
	private static final String ROUTER_CLASS = "com.kyleduo.rabbits.Router";
	public static final String KEY_ORIGIN_URI = "Rabbits_Origin_Uri";

	private static IRouter sRouter;
	static String sAppScheme;
	static String sDefaultHost;
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
						clz = Class.forName(ROUTER_CLASS);
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

	public static void asyncSetup(Context context, String scheme, String defaultHost, Runnable callback) {
		asyncSetup(context, scheme, defaultHost, new DefaultNavigatorFactory(), callback);
	}

	public static void asyncSetup(Context context, String scheme, String defaultHost, INavigatorFactory navigatorFactory, Runnable callback) {
		setup(context, scheme, defaultHost, navigatorFactory, true, callback);
	}

	public static void setup(Context context, String scheme, String defaultHost) {
		setup(context, scheme, defaultHost, new DefaultNavigatorFactory());
	}

	public static void setup(Context context, String scheme, String defaultHost, INavigatorFactory navigatorFactory) {
		setup(context, scheme, defaultHost, navigatorFactory, false, null);
	}

	private static void setup(Context context, String scheme, String defaultHost, INavigatorFactory navigatorFactory, boolean async, Runnable callback) {
		sAppScheme = scheme;
		sDefaultHost = defaultHost;
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
	 * @param uriStr uriStr
	 * @return IProvider
	 */
	public IProvider obtain(String uriStr) {
		Uri uri = Uri.parse(uriStr);
		return obtain(uri);
	}

	/**
	 * Used for obtain page object. Intent or Fragment instance.
	 *
	 * @param uri uri
	 * @return IProvider
	 */
	public IProvider obtain(Uri uri) {
		Target target = Mappings.match(uri).obtain(sRouter);
		return dispatch(target, false);
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
	 * Navigate to page, or perform a not found strategy.
	 *
	 * @param uri uri
	 * @return AbstractNavigator
	 */
	public AbstractNavigator to(Uri uri) {
		Target target = Mappings.match(uri).route(sRouter);
		return dispatch(target, false);
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
		Target target = Mappings.match(uri).route(sRouter);
		return dispatch(target, true);
	}

	/**
	 * Handle the dispatch operation.
	 *
	 * @param target
	 * @param mute
	 * @return
	 */
	private AbstractNavigator dispatch(Target target, boolean mute) {
		Log.d(TAG, target.toString());
		if (!target.hasMatched()) {
			if (!mute) {
				AbstractPageNotFoundHandler pageNotFoundHandler = sNavigatorFactory.createPageNotFoundHandler(mFrom, target.getUri(), target.getPage(), target.getFlags(), target.getExtras(), assembleInterceptor());
				if (pageNotFoundHandler != null) {
					return pageNotFoundHandler;
				}
			} else if (target.getTo() == null) {
				return new MuteNavigator(target.getUri(), mFrom, null, target.getPage(), target.getFlags(), null, mInterceptors);
			}
		}
		return sNavigatorFactory.createNavigator(target.getUri(), mFrom, target.getTo(), target.getPage(), target.getFlags(), target.getExtras(), assembleInterceptor());
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
