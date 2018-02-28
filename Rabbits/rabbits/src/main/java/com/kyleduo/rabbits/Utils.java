package com.kyleduo.rabbits;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * Utility methods.
 *
 * Created by kyle on 26/02/2018.
 */

class Utils {
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
}
