package com.kyleduo.rabbits;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.kyleduo.rabbits.annotations.utils.NameParser;
import com.kyleduo.rabbits.navigator.AbstractNavigator;
import com.kyleduo.rabbits.navigator.AbstractPageNotFoundHandler;
import com.kyleduo.rabbits.navigator.DefaultNavigatorFactory;
import com.kyleduo.rabbits.navigator.INavigationInterceptor;
import com.kyleduo.rabbits.navigator.INavigatorFactory;
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
 * 		.to("http://rabbits.kyleduo.com/some/path")
 * 		.start();
 * </pre>
 * <p>
 * Created by kyle on 2016/12/7.
 */

@SuppressWarnings({"WeakerAccess", "unused"})
public class Rabbit {
	private static final String TAG = "Rabbit";
	private static final String ROUTER_CLASS = "com.kyleduo.rabbits.Router";
	public static final String KEY_ORIGIN_URI = "rabbits_origin_uri";

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

	public static String dumpMappings() {
		return Mappings.dump();
	}

	/**
	 * Initial rabbits with basic elements, using default Navigators.
	 *
	 * @param scheme      Scheme for this application.
	 * @param defaultHost Default host when try to match uri without a host.
	 */
	public static void init(String scheme, String defaultHost) {
		init(scheme, defaultHost, new DefaultNavigatorFactory());
	}

	/**
	 * Initial rabbits with basic elements.
	 *
	 * @param scheme           Scheme for this application.
	 * @param defaultHost      Default host when try to match uri without a host.
	 * @param navigatorFactory Custom navigator factory.
	 */
	public static void init(String scheme, String defaultHost, INavigatorFactory navigatorFactory) {
		sAppScheme = scheme;
		sDefaultHost = defaultHost;
		sNavigatorFactory = navigatorFactory;
	}

	/**
	 * Setup in sub-thread.
	 *
	 * @param context  Used for io operation.
	 * @param callback callback run after setup finished.
	 */
	public static void asyncSetup(Context context, Runnable callback) {
		setup(context, true, callback);
	}

	/**
	 * Synchronously setup.
	 *
	 * @param context Used for io operation.
	 */
	public static void setup(Context context) {
		setup(context, false, null);
	}

	private static void setup(Context context, boolean async, Runnable callback) {
		Mappings.setup(context, async, callback);
	}

	/**
	 * Update mappings from a file.
	 *
	 * @param context Used for io operation.
	 * @param file    Json file.
	 */
	public static void updateMappings(Context context, File file) {
		Mappings.update(context, file);
	}

	/**
	 * Update mappings using a json string.
	 *
	 * @param context Used for io operation.
	 * @param json    Json string.
	 */
	public static void updateMappings(Context context, String json) {
		Mappings.update(context, json);
	}

	/**
	 * Create a rabbit who has ability to navigate through your pages.
	 *
	 * @param from Whether an Activity or a Fragment instance.
	 * @return Rabbit instance.
	 */
	public static Rabbit from(Object from) {
		if (!(from instanceof Activity) && !(from instanceof Fragment || from instanceof android.app.Fragment)) {
			throw new IllegalArgumentException("From object must be whether an Activity or a Fragment instance.");
		}
		return new Rabbit(from);
	}

	/**
	 * Add global interceptor. These Interceptors' methods will be invoked in every navigation.
	 *
	 * @param interceptor Interceptor instance.
	 */
	public static void addGlobalInterceptor(INavigationInterceptor interceptor) {
		if (sInterceptors == null) {
			sInterceptors = new ArrayList<>();
		}
		sInterceptors.add(interceptor);
	}

	/**
	 * Add an interceptor used for this navigation. This is useful when you want to check whether a
	 * uri matches a specific page using {@link com.kyleduo.rabbits.Rabbit#tryTo(Uri)} method.
	 *
	 * @param interceptor Interceptor instance.
	 * @return Rabbit instance.
	 */
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
	 * @return AbstractNavigator
	 */
	public AbstractNavigator obtain(String uriStr) {
		Uri uri = Uri.parse(uriStr);
		return obtain(uri);
	}

	/**
	 * Used for obtain page object. Intent or Fragment instance.
	 *
	 * @param uri uri
	 * @return AbstractNavigator
	 */
	public AbstractNavigator obtain(Uri uri) {
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
	 * Only if there was a mapping from uri with app scheme and given path exists a valid navigator
	 * will returned.
	 *
	 * @param uri uri
	 * @return AbstractNavigator
	 */
	public AbstractNavigator tryTo(Uri uri) {
		uri = uri.buildUpon().scheme(sAppScheme).build();
		Target target = Mappings.match(uri).route(sRouter);
		return dispatch(target, true);
	}

	/**
	 * Handle the dispatch operation.
	 *
	 * @param target target
	 * @param mute   whether mute
	 * @return navigator
	 */
	private AbstractNavigator dispatch(Target target, boolean mute) {
		Log.d(TAG, target.toString());
		if (!target.hasMatched()) {
			if (!mute) {
				AbstractPageNotFoundHandler pageNotFoundHandler = sNavigatorFactory.createPageNotFoundHandler(mFrom, target, assembleInterceptor());
				if (pageNotFoundHandler != null) {
					return pageNotFoundHandler;
				}
			} else if (target.getTo() == null) {
				return new MuteNavigator(mFrom, target, assembleInterceptor());
			}
		}
		return sNavigatorFactory.createNavigator(mFrom, target, assembleInterceptor());
	}

	/**
	 * Assemble interceptors and static interceptors.
	 * order static interceptors after instance's interceptors.
	 *
	 * @return a list of valid navigation interceptor
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
