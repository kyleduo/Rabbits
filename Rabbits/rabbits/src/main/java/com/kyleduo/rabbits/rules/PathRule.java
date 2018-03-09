package com.kyleduo.rabbits.rules;

import android.net.Uri;

/**
 * Rule for Path.
 *
 * Created by kyle on 11/02/2018.
 */

@SuppressWarnings("unused")
public class PathRule extends UriRule {
    PathRule() {
    }

    @Override
    public boolean verify(Uri uri) {
        if (uri.isOpaque()) {
            return false;
        }
        return verify(uri.getPath());
    }

    @Override
    public String toString() {
        return "(Path " + super.toString();
    }
}
