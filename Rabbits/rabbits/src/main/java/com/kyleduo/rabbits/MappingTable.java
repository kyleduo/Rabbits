package com.kyleduo.rabbits;

import android.net.Uri;

import com.kyleduo.rabbits.annotations.TargetInfo;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by kyle on 24/01/2018.
 */

public class MappingTable {
    private static Map<String, TargetInfo> sMappings = new LinkedHashMap<>();

    public static void map(String path, TargetInfo targetInfo) {
        sMappings.put(path, targetInfo);
    }

    public static TargetInfo match(Uri uri) {
        if (Rules.valid(uri)) {
            TargetInfo to = sMappings.get(uri.getPath());
            if (to == null) {
                return deepMatch(uri);
            }
            return to;
        }
        return null;
    }

    private static TargetInfo deepMatch(Uri uri) {
        return null;
    }
}
