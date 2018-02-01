package com.kyleduo.rabbits;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.kyleduo.rabbits.annotations.TargetInfo;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by kyle on 24/01/2018.
 */

@SuppressWarnings("WeakerAccess")
public class RouteTable {
    private static Map<String, TargetInfo> sMappings = new LinkedHashMap<>();

    public static void map(String path, TargetInfo targetInfo) {
        sMappings.put(path, targetInfo);
    }

    static TargetInfo match(Uri uri) {
        if (uri == null) {
            return null;
        }
        // 并不能先判断scheme 和 domain，因为可能有固定匹配的模式，特殊scheme和domain。
        TargetInfo to = sMappings.get(uri.getPath());
        if (to == null) {
            // 处理完全匹配的模式
            to = sMappings.get(uri.toString());
        }
        if (to == null && valid(uri)) {
            return deepMatch(uri);
        }
        return to;
    }

    private static TargetInfo deepMatch(Uri uri) {
        // TODO: 01/02/2018 deep match
        return null;
    }

    private static boolean valid(@NonNull Uri uri) {
        String scheme = uri.getScheme();
        String domain = uri.getAuthority();

        return Rabbit.get().getSchemes().contains(scheme)
                && Rabbit.get().getDomains().contains(domain);
    }
}
