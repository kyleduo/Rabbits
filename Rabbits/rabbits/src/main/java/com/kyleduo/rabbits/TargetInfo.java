package com.kyleduo.rabbits;

import java.util.Map;

/**
 * Representing a page in route table.
 *
 * Created by kyle on 26/01/2018.
 */
@SuppressWarnings("WeakerAccess")
public class TargetInfo {
    static final int TYPE_NONE = 0;
    public static final int TYPE_ACTIVITY = 1;
    public static final int TYPE_FRAGMENT = 2;
    public static final int TYPE_FRAGMENT_V4 = 3;

    String pattern;
    Class<?> target;
    int type;
    int flags;
    //params from REST url.
    Map<String, Object> params;

    public TargetInfo(String pattern, Class<?> target, int type, int flags) {
        this.pattern = pattern;
        this.target = target;
        this.type = type;
        this.flags = flags;
    }

    TargetInfo(TargetInfo info) {
        this.pattern = info.pattern;
        this.target = info.target;
        this.type = info.type;
        this.flags = info.flags;
    }
}
