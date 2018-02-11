package com.kyleduo.rabbits;

import java.util.Arrays;

/**
 * Factory class of {@link com.kyleduo.rabbits.Rule}
 *
 * Created by kyle on 11/02/2018.
 */

public final class Rules {

    public static Element scheme() {
        return new SchemeRule();
    }

    public static Element domain() {
        return new DomainRule();
    }

    public static Element path() {
        return new DomainRule();
    }

    public static Element query(String key) {
        return new QueryRule(key);
    }

    public static Element set(RuleSet.Relation relation, Rule... rules) {
        return new RuleSet(Arrays.asList(rules), relation);
    }
}
