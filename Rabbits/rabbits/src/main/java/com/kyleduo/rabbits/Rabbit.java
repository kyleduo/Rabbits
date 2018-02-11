package com.kyleduo.rabbits;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.util.SparseArray;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

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
    private static final String ROUTER_CLASS_NAME = PACKAGE + ".Router";
    private static final String GENERATE_METHOD_NAME = "generate";

    private List<String> mSchemes;
    private List<String> mDomains;
    private List<Interceptor> mInterceptors = new ArrayList<>();
    private SparseArray<Navigator> mNavigators = new SparseArray<>();

    private static Rabbit sInstance;

    private Rabbit(RConfig config) {
        this.registerNavigator(TargetInfo.TYPE_ACTIVITY, new ActivityNavigator());
        this.mSchemes = config.getSchemes();
        this.mDomains = config.getDomains();

        try {
            Class<?> routerClass = Class.forName(ROUTER_CLASS_NAME);
            Method generateMethod = routerClass.getMethod(GENERATE_METHOD_NAME);
            generateMethod.invoke(routerClass);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalStateException("Generate route table failed.");
        }

    }

    public List<String> getDomains() {
        return mDomains;
    }

    public List<String> getSchemes() {
        return mSchemes;
    }

    static Rabbit get() {
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

    public Rabbit registerFallbackNavigator(Navigator navigator) {
        mNavigators.put(TargetInfo.TYPE_NONE, navigator);
        return this;
    }

    public Rabbit addInterceptor(Interceptor interceptor, Rule rule) {
        mInterceptors.add(new PatternInterceptor(interceptor, rule));
        return this;
    }

    DispatchResult dispatch(Navigation navigation) {
        final Action action = navigation.action();

        // interceptors

        List<Interceptor> interceptors = new ArrayList<>();
        interceptors.add(new ActionParser());

        if (!action.isIgnoreInterceptors()) {
            // custom interceptors
            interceptors.addAll(mInterceptors);

            // action specific interceptors
            if (navigation.interceptors() != null) {
                interceptors.addAll(navigation.interceptors());
            }
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
