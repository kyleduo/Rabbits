package com.kyleduo.rabbits;

/**
 * for Module
 * Created by kyleduo on 2017/5/9.
 */

public class MappingsSource {
    static final int TYPE_DEFAULT = 0;
    static final int TYPE_ASSETS = 1;
    static final int TYPE_PERSIST = 2;
    static final int TYPE_JSON = 3;
    static final int TYPE_FILE = 4;

    private int mType;
    private String mValue;
    private boolean mOverride;

    private MappingsSource(int type, String value, boolean override) {
        mType = type;
        mValue = value;
        mOverride = override;
    }

    public int getType() {
        return mType;
    }

    public String getValue() {
        return mValue;
    }

    public MappingsSource override(boolean override) {
        mOverride = override;
        return this;
    }

    public static MappingsSource getDefault() {
        return new MappingsSource(TYPE_DEFAULT, null, false);
    }

    public static MappingsSource fromAssets() {
        return new MappingsSource(TYPE_ASSETS, null, false);
    }

    /**
     * Override current mappings by default.
     *
     * @param filepath absolute file path.
     * @return MappingsSource
     */
    public static MappingsSource fromFile(String filepath) {
        return new MappingsSource(TYPE_FILE, filepath, true);
    }

    /**
     * Override current mappings by default.
     *
     * @param json json.
     * @return MappingsSource
     */
    public static MappingsSource fromJson(String json) {
        return new MappingsSource(TYPE_JSON, json, true);
    }
}
