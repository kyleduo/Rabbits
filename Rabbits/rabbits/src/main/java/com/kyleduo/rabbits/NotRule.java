package com.kyleduo.rabbits;

/**
 * Created by kyle on 01/03/2018.
 */

public class NotRule implements Rule {

    private Rule mRule;

    public NotRule(Rule rule) {
        mRule = rule;
    }

    @Override
    public boolean verify(Action action) {
        return !mRule.verify(action);
    }
}
