package com.kyleduo.rabbits;

import android.net.Uri;

/**
 * Rules used when matching url.
 *
 * Created by kyle on 11/02/2018.
 */

public interface Rule {
    enum Operator {
        IS, STARTS_WITH, ENDS_WITH, IN, CONTAINS, EXISTS
    }

    boolean verify(Uri uri);
}
