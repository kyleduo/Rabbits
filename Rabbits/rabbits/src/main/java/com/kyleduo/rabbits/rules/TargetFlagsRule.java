package com.kyleduo.rabbits.rules;

import com.kyleduo.rabbits.Action;

/**
 * Rule for targetFlags from Action
 *
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
