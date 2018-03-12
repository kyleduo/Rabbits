package com.kyleduo.rabbits;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * Utility methods for url.
 *
 * Created by kyle on 26/02/2018.
 */

class URLEncodeUtils {
    static String decode(String origin) {
        String out;
        try {
            out = URLDecoder.decode(origin, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            out = origin;
        }
        return out;
    }

    static String encode(String origin) {
        String out;
        try {
            out = URLEncoder.encode(origin, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            out = origin;
        }
        return out;
    }
}
