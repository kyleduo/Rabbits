package com.kyleduo.rabbits;

import android.net.Uri;

/**
 * Rule for Domain.
 *
 * Created by kyle on 11/02/2018.
 */

public class DomainRule extends UriRule {
    DomainRule() {
    }

    @Override
    public boolean verify(Uri uri) {
        return verify(uri.getAuthority());
    }

    @Override
    public String toString() {
        return "(Domain " + super.toString();
    }
}
