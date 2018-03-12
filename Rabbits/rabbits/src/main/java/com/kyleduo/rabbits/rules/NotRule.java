package com.kyleduo.rabbits.rules;

import com.kyleduo.rabbits.Action;

/**
 * Not
 *
 * Created by kyle on 01/03/2018.
 */

public class NotRule implements Rule {

    private Rule mRule;

    NotRule(Rule rule) {
        mRule = rule;
    }

    @Override
    public boolean verify(Action action) {
        return !mRule.verify(action);
    }
}
