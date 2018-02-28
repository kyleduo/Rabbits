package com.kyleduo.rabbits;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Storing the routes, this class is responsible for find the proper page that matches the uri.
 * <p>
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
    private static boolean sHasCompletePattern = false;

    public static void map(String path, TargetInfo targetInfo) {
        sMappings.put(path, targetInfo);
        if (path.contains("://")) {
            sHasCompletePattern = true;
        }
        Logger.d("Route rule inserted. PATTERN: " + path + " TARGET: " + targetInfo.target.getCanonicalName());
    }

    static TargetInfo match(Uri uri) {
        if (uri == null) {
            return null;
        }
        Logger.d("Start matching. URI: " + uri.toString());
        // 并不能先判断scheme 和 domain，因为可能有固定匹配的模式，特殊scheme和domain。
        TargetInfo to = null;
        if (valid(uri)) {
            Logger.d("[01] Matching use path.");
            to = sMappings.get(uri.getPath());
        }
        if (to == null) {
            Logger.d("[02] Matching use pure uri.");
            // 处理完全匹配的模式
            // 不能用完整uri来匹配，如果有参数，要先移除参数.
            Uri pure = uri.buildUpon().query(null).build();
            to = sMappings.get(pure.toString());
        }
        if (to == null) {
            Logger.d("[03] Deeply matching.");
            to = deepMatch(uri);
        }
        if (Rabbit.sDebug) {
            if (to != null) {
                Logger.d("[04] Target found: " + to.target.getCanonicalName());
            } else {
                Logger.d("[04] Not match.");
            }
        }
        return to;
    }

    private static TargetInfo deepMatch(Uri uri) {
        boolean valid = valid(uri);
        if (!sHasCompletePattern && !valid) {
            // Only execute deep match when there complete pattern and the uri is verify.
            return null;
        }
        String path = uri.getPath();
        String[] segs = path.split("/");

        outer:
        for (Map.Entry<String, TargetInfo> entry : sMappings.entrySet()) {
            String pattern = entry.getKey();
            String patternPath;
            if (pattern.contains("://")) {
                Uri patternUri = Uri.parse(pattern);
                if (TextUtils.equals(patternUri.getScheme(), uri.getScheme()) && TextUtils.equals(patternUri.getAuthority(), uri.getAuthority())) {
                    // If one page has been annotated with a complete url, it can only be opened by
                    // url with same scheme and domain.
                    patternPath = Uri.parse(pattern).getPath();
                } else {
                    continue;
                }
            } else if (valid) {
                patternPath = pattern;
            } else {
                break;
            }
            if (path.equals(patternPath)) {
                // scheme和domain不同，但是pattern相同，认为匹配
                return entry.getValue();
            }
            if (!patternPath.contains("{")) {
                continue;
            }
            String[] pSegs = patternPath.split("/");
            if (segs.length != pSegs.length) {
                continue;
            }

            Map<String, Object> params = null;

            for (int i = 0; i < pSegs.length; i++) {
                String part = Utils.decode(segs[i]);
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

    static String dump() {
        StringBuilder dump = new StringBuilder();
        for (Map.Entry<String, TargetInfo> e : sMappings.entrySet()) {
            dump.append("pattern: ").append(e.getKey()).append("\n")
                    .append("class: ").append(e.getValue().target.getCanonicalName()).append("\n\n");
        }
        return dump.toString();
    }
}
