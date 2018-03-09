package com.kyleduo.rabbits.rules;

import android.net.Uri;

/**
 * Rule for query.
 *
 * Created by kyle on 11/02/2018.
 */

public class QueryRule extends UriRule {

    private String mKey;

    QueryRule(String key) {
        mKey = key;
    }

    @Override
    public boolean verify(Uri uri) {
        if (uri.isOpaque()) {
            return false;
        }
        String q = uri.getQueryParameter(mKey);
        return super.verify(q);
    }

    @Override
    public String toString() {
        return "(" + mKey + " " + super.toString();
    }
}
