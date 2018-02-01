package com.kyleduo.rabbits;

import java.util.Arrays;
import java.util.List;

/**
 * Config object for Rabbits
 * Created by kyleduo on 2017/5/10.
 */

@SuppressWarnings("WeakerAccess")
public class RConfig {
    private List<String> mSchemes;
    private List<String> mDomains;

    public static RConfig get() {
        return new RConfig();
    }

    public RConfig schemes(String... schemes) {
        mSchemes = Arrays.asList(schemes);
        return this;
    }

    public RConfig domains(String... domains) {
        mDomains = Arrays.asList(domains);
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
}
