package com.kyleduo.rabbits;

import android.text.TextUtils;

import com.kyleduo.rabbits.navigator.INavigatorFactory;

/**
 * Config object for Rabbits
 * Created by kyleduo on 2017/5/10.
 */

@SuppressWarnings("WeakerAccess")
public class RConfig {
    private boolean mForceUpdatePersist;
    private String mScheme;
    private String mHost;
    private INavigatorFactory mNavigatorFactory;

    private RConfig() {
        mForceUpdatePersist = false;
    }

    public static RConfig get() {
        return new RConfig();
    }

    public RConfig scheme(String scheme) {
        mScheme = scheme;
        return this;
    }

    public RConfig defaultHost(String host) {
        mHost = host;
        return this;
    }

    public RConfig navigatorFactory(INavigatorFactory navigatorFactory) {
        mNavigatorFactory = navigatorFactory;
        return this;
    }

    public RConfig forceUpdatePersist(boolean forcePersist) {
        mForceUpdatePersist = forcePersist;
        return this;
    }

    public boolean valid() {
        return !(TextUtils.isEmpty(mHost) || TextUtils.isEmpty(mScheme));
    }

    public boolean shouldForceUpdatePersist() {
        return mForceUpdatePersist;
    }

    public String getScheme() {
        return mScheme;
    }

    public String getHost() {
        return mHost;
    }

    public INavigatorFactory getNavigatorFactory() {
        return mNavigatorFactory;
    }
}
