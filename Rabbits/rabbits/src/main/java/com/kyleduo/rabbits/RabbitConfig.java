package com.kyleduo.rabbits;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

/**
 * Config object for Rabbits
 * Created by kyleduo on 2017/5/10.
 */

@SuppressWarnings("WeakerAccess")
public final class RabbitConfig {
    private List<String> mSchemes;
    private List<String> mDomains;
    private boolean mDebug;

    public static RabbitConfig get() {
        return new RabbitConfig();
    }

    public RabbitConfig schemes(String... schemes) {
        mSchemes = new ArrayList<>(new HashSet<>(Arrays.asList(schemes)));
        return this;
    }

    public RabbitConfig domains(String... domains) {
        mDomains = new ArrayList<>(new HashSet<>(Arrays.asList(domains)));
        return this;
    }

    public RabbitConfig debug(boolean debug) {
        mDebug = debug;
        return this;
    }

    public boolean valid() {
        return mSchemes != null && mSchemes.size() > 0 && mDomains != null && mDomains.size() > 0;
    }

    public List<String> getSchemes() {
        return mSchemes;
    }

    public List<String> getDomains() {
        return mDomains;
    }

    public boolean isDebug() {
        return mDebug;
    }
}
