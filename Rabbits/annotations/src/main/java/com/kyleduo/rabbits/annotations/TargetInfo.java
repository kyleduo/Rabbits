package com.kyleduo.rabbits.annotations;

/**
 * Created by kyle on 26/01/2018.
 */

public class TargetInfo {
    public static final int TYPE_NOT_FOUND = 0;
    public static final int TYPE_ACTIVITY = 1;
    public static final int TYPE_FRAGMENT = 2;
    public static final int TYPE_FRAGMENT_V4 = 3;

    public String pattern;
    public Class<?> target;
    public int type;
    public int flags;

    public TargetInfo(String pattern, Class<?> target, int type, int flags) {
        this.pattern = pattern;
        this.target = target;
        this.type = type;
        this.flags = flags;
    }
}
