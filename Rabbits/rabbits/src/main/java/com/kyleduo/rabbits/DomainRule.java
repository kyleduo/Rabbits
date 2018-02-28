package com.kyleduo.rabbits;

import android.net.Uri;

/**
 * Rule for Domain.
 *
 * Created by kyle on 11/02/2018.
 */

public class DomainRule extends RuleImpl {
    DomainRule() {
    }

    @Override
    public boolean verify(Uri uri) {
        return valid(uri.getAuthority());
    }

    @Override
    public String toString() {
        return "(Domain " + super.toString();
    }
}
