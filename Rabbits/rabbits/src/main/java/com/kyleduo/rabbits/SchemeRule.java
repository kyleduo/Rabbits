package com.kyleduo.rabbits;

import android.net.Uri;

/**
 * Rule for Scheme.
 *
 * Created by kyle on 11/02/2018.
 */

public class SchemeRule extends RuleImpl {

    SchemeRule() {
    }

    @Override
    public boolean verify(Uri uri) {
        return valid(uri.getScheme());
    }
}
