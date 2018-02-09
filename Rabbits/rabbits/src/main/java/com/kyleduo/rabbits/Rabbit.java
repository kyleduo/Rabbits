package com.kyleduo.rabbits;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.SparseArray;

import com.kyleduo.rabbits.annotations.TargetInfo;
import com.kyleduo.rabbits.annotations.utils.NameParser;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
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
public final class Rabbit {
    private static final String TAG = Rabbit.class.getSimpleName();
    private static final String PACKAGE = "com.kyleduo.rabbits";
    private static final String ROUTER_CLASS = PACKAGE + ".Router";
    private static final String ROUTERS_CLASS = PACKAGE + ".Routers";
    private static final String ROUTERS_FIELD_CLASS = "routers";

    /**
     * URI used in the origin of this navigation.
     */
    public static final String KEY_ORIGIN_URI = "rabbits_origin_uri";
    /**
     * URI used for latest Navigator. If navigate to a page(origin uri) depending on another page(source uri),
     * you will finally open the second page and you can get the origin uri through {@link Rabbit#KEY_ORIGIN_URI}
     */
    public static final String KEY_SOURCE_URI = "rabbits_source_uri";

    private List<String> mSchemes;
    private List<String> mDomains;
    private List<Interceptor> mInterceptors = new ArrayList<>();
    private SparseArray<Navigator> mNavigators = new SparseArray<>();

    private static class RabbitInvocationHandler implements InvocationHandler {

        private List<Class<?>> mClasses;
        private Map<String, Method> methods = new HashMap<>();

        @Override
        public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
            if (mClasses == null) {
                mClasses = new ArrayList<>();
                Class cls;
                try {
                    cls = Class.forName(ROUTERS_CLASS);
                } catch (ClassNotFoundException e) {
                    cls = Class.forName(ROUTER_CLASS);
                    mClasses.add(cls);
                }
                if (mClasses.size() == 0) { // means using Routers class, so we fill the Array
                    Field field = cls.getField(ROUTERS_FIELD_CLASS);
                    String[] names = (String[]) field.get(null);
                    for (String name : names) {
                        try {
                            mClasses.add(Class.forName(PACKAGE + "." + name));
                        } catch (ClassNotFoundException e) {
                            Log.e(TAG, "Can not found class of name: " + PACKAGE + "." + name);
                        }
                    }
                }
            }
            String page = (String) objects[0];
            if (page == null || page.length() == 0) {
                return null;
            }
            String name = method.getName();

            String key = name + "-" + page;

            Method m = methods.get(key);
            if (m != null) {
                return m.invoke(null);
            } else {
                boolean findObtain = true;
                if (name.equals(IRouter.METHOD_OBTAIN)) {
                    String methodName = NameParser.parseObtain(page);
                    for (Class<?> clz : mClasses) {
                        try {
                            m = clz.getMethod(methodName);
                            break;
                        } catch (NoSuchMethodException e) {
                            // do nothing
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    if (m != null) {
                        methods.put(key, m);
                        return m.invoke(null);
                    }
                    findObtain = false;
                }
                if (name.equals(IRouter.METHOD_ROUTE) || !findObtain) {
                    String methodName = NameParser.parseRoute(page);
                    for (Class<?> clz : mClasses) {
                        try {
                            m = clz.getMethod(methodName);
                            break;
                        } catch (NoSuchMethodException e) {
                            // do nothing
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    if (m != null) {
                        methods.put(key, m);
                        return m.invoke(null);
                    }
                }
            }
            return null;
        }
    }

    private static  Rabbit sInstance;

    private Rabbit(RConfig config) {
        this.registerNavigator(TargetInfo.TYPE_ACTIVITY, new ActivityNavigator());
        this.mSchemes = config.getSchemes();
        this.mDomains = config.getDomains();
    }

    public List<String> getDomains() {
        return mDomains;
    }

    public List<String> getSchemes() {
        return mSchemes;
    }

    public static Rabbit get() {
        return sInstance;
    }

    public static synchronized Rabbit init(RConfig config) {
        if (!config.valid()) {
            throw new IllegalArgumentException("Config object not valid");
        }
        if (sInstance == null) {
            sInstance = new Rabbit(config);
        }
        return sInstance;
    }

    /**
     * Create a rabbit who has ability to navigate through your pages.
     *
     * @param from Whether an Activity or a Fragment instance.
     * @return Rabbit instance.
     */
    public static Navigation from(Object from) {
        if (get() == null) {
            throw new IllegalStateException("Rabbit has not been initialized properly");
        }
        if (!(from instanceof Activity) && !(from instanceof Fragment || from instanceof android.app.Fragment) && !(from instanceof Context)) {
            throw new IllegalArgumentException("From object must be whether an Activity or a Fragment instance.");
        }
        Action action = new Action();
        action.setFrom(from);
        return new NavigationImpl(get(), action);
    }

    /**
     * Add an interceptor used for this navigation. This is useful when you want to check whether a
     * uri matches a specific page using method.
     *
     * @param interceptor Interceptor instance.
     * @return Rabbit instance.
     */
    public Rabbit addInterceptor(Interceptor interceptor) {
        if (mInterceptors == null) {
            mInterceptors = new ArrayList<>();
        }
        mInterceptors.add(interceptor);
        return this;
    }

    public Rabbit registerNavigator(int type, Navigator navigator) {
        mNavigators.put(type, navigator);
        return this;
    }

    public Rabbit addInterceptor(Interceptor interceptor, String pattern) {
        mInterceptors.add(new PatternInterceptor(interceptor, pattern));
        return this;
    }

    DispatchResult dispatch(Navigation navigation) {
        final Action action = navigation.action();

        // interceptors

        List<Interceptor> interceptors = new ArrayList<>();
        interceptors.add(new ActionParser());

        // custom interceptors
        interceptors.addAll(mInterceptors);

        // action specific interceptors
        if (navigation.interceptors() != null) {
            interceptors.addAll(navigation.interceptors());
        }

        interceptors.add(new TargetAssembler());
        interceptors.add(new NavigatorInterceptor(mNavigators));

        RealDispatcher dispatcher = new RealDispatcher(action, interceptors, 0);

        DispatchResult result = dispatcher.dispatch(action);
        if (result == null) {
            return DispatchResult.notFinished();
        }
        return result;
    }
}
