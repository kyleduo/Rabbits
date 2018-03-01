package com.kyleduo.rabbits;

/**
 * Created by kyle on 01/03/2018.
 */

public class TargetFlagsRule implements Rule {

    private int mMask;

    public Rule has(int mask) {
        this.mMask = mask;
        return this;
    }

    @Override
    public boolean verify(Action action) {
        return (action.getTargetFlags() & mMask) > 0;
    }
}
