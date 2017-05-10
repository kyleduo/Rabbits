package com.kyleduo.rabbits;

/**
 * for Module
 * Created by kyleduo on 2017/5/9.
 */

@SuppressWarnings({"WeakerAccess", "unused"})
public class MappingsSource {
    static final int TYPE_DEFAULT = 0;
    static final int TYPE_ASSETS = 1;
    static final int TYPE_PERSIST = 2;
    static final int TYPE_JSON = 3;
    static final int TYPE_FILE = 4;

    private int mType;
    private String mValue;
    /**
     * True for fully update. False will lead to a merge operation.
     */
    private boolean mFullyUpdate;
    private MappingsGroup mOriginMappings;

    private MappingsSource(int type, String value, boolean fullyUpdate) {
        mType = type;
        mValue = value;
        mFullyUpdate = fullyUpdate;
    }

    public int getType() {
        return mType;
    }

    public String getValue() {
        return mValue;
    }

    public boolean shouldFullyUpdate() {
        return mFullyUpdate;
    }

    public MappingsSource fullyUpdate(boolean fullyUpdate) {
        mFullyUpdate = fullyUpdate;
        return this;
    }

    MappingsGroup getOriginMappings() {
        return mOriginMappings;
    }

    MappingsSource setOriginMappings(MappingsGroup origin) {
        mOriginMappings = origin;
        return this;
    }

    public static MappingsSource getDefault() {
        return new MappingsSource(TYPE_DEFAULT, null, true);
    }

    public static MappingsSource fromAssets() {
        return new MappingsSource(TYPE_ASSETS, null, true);
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
