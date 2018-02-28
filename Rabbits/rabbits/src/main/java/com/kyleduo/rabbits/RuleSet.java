package com.kyleduo.rabbits;

import android.net.Uri;

import java.util.List;

/**
 * Represents a set of rules which should be validated in AND or OR relationship.
 *
 * Created by kyle on 11/02/2018.
 */

public class RuleSet extends RuleImpl {
    public enum Relation {
        AND, OR
    }

    private List<Rule> mRules;
    private Relation mRelation;

    RuleSet(List<Rule> rules, Relation relation) {
        mRules = rules;
        mRelation = relation;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public boolean verify(Uri uri) {
        if (mRelation == Relation.AND) {
            for (Rule rule : mRules) {
                if (!rule.verify(uri)) {
                    return false;
                }
            }
            return true;
        } else if (mRelation == Relation.OR) {
            for (Rule rule : mRules) {
                if (rule.verify(uri)) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }
}
