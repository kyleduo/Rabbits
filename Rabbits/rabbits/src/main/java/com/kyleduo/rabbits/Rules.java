package com.kyleduo.rabbits;

import java.util.Arrays;

/**
 * Factory class of {@link com.kyleduo.rabbits.Rule}
 * <p>
 * Created by kyle on 11/02/2018.
 */

public final class Rules {

    public static UriRule scheme() {
        return new SchemeRule();
    }

    public static UriRule domain() {
        return new DomainRule();
    }

    public static UriRule path() {
        return new PathRule();
    }

    public static UriRule query(String key) {
        return new QueryRule(key);
    }

    public static TargetFlagsRule flags() {
        return new TargetFlagsRule();
    }

    public static NotRule not(Rule rule) {
        return new NotRule(rule);
    }

    public static Rule set(RuleSet.Relation relation, Rule... rules) {
        return new RuleSet(Arrays.asList(rules), relation);
    }
}
