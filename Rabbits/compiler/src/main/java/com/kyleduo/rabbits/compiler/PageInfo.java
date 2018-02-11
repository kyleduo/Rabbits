package com.kyleduo.rabbits.compiler;

import com.squareup.javapoet.ClassName;

/**
 * Information used in compiler.
 *
 * Created by kyle on 30/01/2018.
 */

class PageInfo {
    String url;
    ClassName target;
    int type;
    int flag;
    String alias;
    boolean main;

    PageInfo(String url, ClassName target, int type, int flag, String alias, boolean main) {
        this.url = url;
        this.target = target;
        this.type = type;
        this.flag = flag;
        this.alias = alias;
        this.main = main;
    }
}
