package com.kyleduo.rabbits.compiler;

import com.squareup.javapoet.ClassName;

/**
 * Created by kyle on 30/01/2018.
 */

public class PageInfo {
    public String url;
    public ClassName target;
    public int type;
    public int flag;

    public PageInfo(String url, ClassName target, int type, int flag) {
        this.url = url;
        this.target = target;
        this.type = type;
        this.flag = flag;
    }

    @Override
    public String toString() {
        return "PageInfo{" +
                "url='" + url + '\'' +
                ", target=" + target +
                ", type=" + type +
                ", flag=" + flag +
                '}';
    }
}
