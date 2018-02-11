package com.kyleduo.rabbits;

import android.net.Uri;

/**
 * Rule for query.
 *
 * Created by kyle on 11/02/2018.
 */

public class QueryRule extends RuleImpl {

    private String mKey;

    QueryRule(String key) {
        mKey = key;
    }

    @Override
    public Rule startsWith(String value) {
        throw new IllegalStateException("Not supported by Query");
    }

    @Override
    public Rule endsWith(String value) {
        throw new IllegalStateException("Not supported by Query");
    }

    @Override
    public Rule contains(String value) {
        throw new IllegalStateException("Not supported by Query");
    }

    @Override
    public boolean valid(Uri uri) {
        String q = uri.getQueryParameter(mKey);
        return super.valid(q);
    }
}
