package com.kyleduo.rabbits;

import android.net.Uri;
import android.support.annotation.NonNull;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Storing the routes, this class is responsible for find the proper page that matches the uri.
 *
 * Created by kyle on 24/01/2018.
 */

@SuppressWarnings("WeakerAccess")
public class RouteTable {
    private static final String PARAM_KEY_INT = "i";
    private static final String PARAM_KEY_LONG = "l";
    private static final String PARAM_KEY_FLOAT = "f";
    private static final String PARAM_KEY_DOUBLE = "d";
    private static final String PARAM_KEY_BOOLEAN = "b";
    private static final String PARAM_KEY_STRING = "s";

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
        String path = uri.getPath();
        String[] segs = path.split("/");

        outer:
        for (Map.Entry<String, TargetInfo> entry : sMappings.entrySet()) {
            String pattern = entry.getKey();
            if (!pattern.contains("{")) {
                continue;
            }
            String[] pSegs = pattern.split("/");
            if (segs.length != pSegs.length) {
                continue;
            }

            Map<String, Object> params = null;

            for (int i = 0; i < pSegs.length; i++) {
                String part = segs[i];
                String patternPart = pSegs[i];

                if (patternPart.startsWith("{")) {
                    String[] paramFormat = patternPart.substring(1, patternPart.length() - 1).split(":");
                    if (params == null) {
                        params = new HashMap<>();
                    }
                    if (paramFormat.length == 1) {
                        params.put(paramFormat[0], part);
                    } else if (paramFormat.length == 2) {
                        String type = paramFormat[1].toLowerCase();
                        switch (type) {
                            case PARAM_KEY_INT:
                                params.put(paramFormat[0], part);
                                break;
                            case PARAM_KEY_LONG:
                                params.put(paramFormat[0], Long.parseLong(part));
                                break;
                            case PARAM_KEY_FLOAT:
                                params.put(paramFormat[0], Float.parseFloat(part));
                                break;
                            case PARAM_KEY_DOUBLE:
                                params.put(paramFormat[0], Double.parseDouble(part));
                                break;
                            case PARAM_KEY_BOOLEAN:
                                params.put(paramFormat[0], Boolean.parseBoolean(part));
                                break;
                            case PARAM_KEY_STRING:
                            default:
                                params.put(paramFormat[0], part);
                                break;
                        }
                    }
                } else if (!patternPart.equals(part)) {
                    continue outer;
                }
            }

            TargetInfo info = new TargetInfo(entry.getValue());
            info.params = params;
            return info;
        }

        return null;
    }

    private static boolean valid(@NonNull Uri uri) {
        String scheme = uri.getScheme();
        String domain = uri.getAuthority();

        return Rabbit.get().getSchemes().contains(scheme)
                && Rabbit.get().getDomains().contains(domain);
    }
}
