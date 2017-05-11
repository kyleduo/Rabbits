package com.kyleduo.rabbits;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Router mapping.
 * <p>
 * Created by kyle on 2016/12/7.
 */

class Mappings {
    @SuppressWarnings("unused")
    private static final String TAG = "Module.Mappings";

    private static final String MAPPING_QUERY_MODE = "rabbitsMode";
    private static final String MODE_CLEAR_TOP = "clearTop";
    private static final String MODE_NEW_TASK = "newTask";

    private static final String PARAM_KEY_INT = "i";
    private static final String PARAM_KEY_LONG = "l";
    private static final String PARAM_KEY_FLOAT = "f";
    private static final String PARAM_KEY_DOUBLE = "d";
    private static final String PARAM_KEY_BOOLEAN = "b";
    private static final String PARAM_KEY_STRING = "s";

    private static final String MAPPING_QUERY_FREE = "LetItGo";

    private static MappingsLoader sMappingsLoader = new MappingsLoader();
    private static MappingsGroup sMappingsGroup;

    static boolean FORCE_UPDATE_PERSIST = false;

    /**
     * Load mappings to memory.
     *
     * @param context  context
     * @param async    whether the operation run in work thread
     * @param callback callback used for async task.
     */
    static void setup(final Context context, boolean async, final MappingsLoaderCallback callback) {
        if (async) {
            sMappingsLoader.loadAsync(context, MappingsSource.getDefault(), FORCE_UPDATE_PERSIST, new MappingsLoaderCallback() {
                @Override
                public void onMappingsLoaded(MappingsGroup mappings) {
                    sMappingsGroup = mappings;
                    if (callback != null) {
                        callback.onMappingsLoaded(mappings);
                    }
                }

                @Override
                public void onMappingsLoadFail() {
                    if (callback != null) {
                        callback.onMappingsLoadFail();
                    }
                }

                @Override
                public void onMappingsPersisted(boolean success) {
                    if (callback != null) {
                        callback.onMappingsPersisted(success);
                    }
                }
            });
        } else {
            sMappingsGroup = sMappingsLoader.load(context, MappingsSource.getDefault(), FORCE_UPDATE_PERSIST);
        }
    }

    /**
     * Update mappings using a file.
     *
     * @param context context
     * @param source  mappings source
     */
    static void update(Context context, MappingsSource source) {
        if (context == null || source == null) {
            throw new NullPointerException();
        }
        // If not fully update, we provide origin mappings to merge.
        if (!source.shouldFullyUpdate()) {
            source.setOriginMappings(sMappingsGroup);
        }
        sMappingsLoader.load(context, source, true);
    }

    static Target match(Uri uri) {
        if (sMappingsGroup == null) {
            throw new IllegalStateException("Rabbits does not fully setup.");
        }
        Uri.Builder builder = uri.buildUpon();
        if (uri.getScheme() == null) {
            builder.scheme(Rabbit.sAppScheme);
        }
        if (uri.getHost() == null) {
            builder.authority(Rabbit.sDefaultHost);
        }
        uri = builder.build();
        Uri pureUri;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            pureUri = builder.fragment(null).query(null).build();
        } else {
            pureUri = builder.fragment(null).clearQuery().build();
        }

        // Try to completely match.
        String page = sMappingsGroup.getMappings().get(pureUri.toString());
        Bundle bundle = null;
        if (page == null) {
            // Deep match.
            bundle = new Bundle();
            page = deepMatch(pureUri, bundle);
        }
        bundle = parseParams(uri, bundle);
        String queryFree = bundle.getString(MAPPING_QUERY_FREE);
        final boolean free = queryFree != null && queryFree.equals("1");
        if (page != null && !free) {
            // Match.
            Target target = new Target(uri);
            target.setPage(page);
            target.setExtras(bundle);
            target.setFlags(parseFlags(uri));
            return target;
        } else {
            Target target = new Target(uri);
            target.setExtras(bundle);
            return target;
        }
    }

    /**
     * Deep checking whether a match uri exist with support for REST uri.
     * <p>
     * To support REST uri, witch may contains params in path, using "{key:type}"
     * format representing a params.
     * <p>
     * Either KEY or TYPE part should contains none blank characters and TYPE part just support
     * these ones (case matters):
     * <pre>
     * 		"i" : an int params.
     * 		"l" : a long params.
     * 		"d" : a double params.
     * 		"B" : a boolean params.
     * 		"s" : a String params.
     * </pre>
     *
     * @param pureUri uri need to be matched, without queries.
     * @param bundle  bundle object used to store params of REST uri.
     * @return page name when found a match.
     */
    private static String deepMatch(Uri pureUri, Bundle bundle) {
        Set<String> uris = sMappingsGroup.getMappings().keySet();
        String[] source = pureUri.toString().split("(://|/)");
        UriLoop:
        for (String uri : uris) {
            // Check match for each uri.
            String[] template = uri.split("(://|/)");
            if (!template[0].equals(source[0]) || template.length != source.length) {
                continue;
            }
            if (!template[1].equals(source[1]) && (sMappingsGroup.getAllowedHosts() == null || !sMappingsGroup.getAllowedHosts().contains(source[1]))) {
                continue;
            }
            // Compare each part, parse params.
            for (int i = 2; i < source.length; i++) {
                String s = source[i];
                String t = template[i];
                if (t.equals(s)) {
                    continue;
                }
                // Check whether a param field.
                if (t.matches("\\{\\S+(:\\S+)?\\}")) {
                    try {
                        formatParam(t, s, bundle);
                    } catch (NumberFormatException e) {
                        continue UriLoop;
                    }
                    continue;
                }
                continue UriLoop;
            }
            return sMappingsGroup.getMappings().get(uri);
        }
        return null;
    }

    /**
     * format param and put it in bundle.
     *
     * @param t      template segment
     * @param param  param value
     * @param bundle bundle
     * @throws NumberFormatException parse string to number error.
     */
    private static void formatParam(String t, String param, Bundle bundle) throws NumberFormatException {
        String[] tt = t.substring(1, t.length() - 1).split(":");
        String key = tt[0];
        String value = decode(param);
        String type = tt.length == 2 ? tt[1].toLowerCase() : "";
        switch (type) {
            case PARAM_KEY_INT:
                bundle.putInt(key, Integer.parseInt(value));
                break;
            case PARAM_KEY_LONG:
                bundle.putLong(key, Long.parseLong(value));
                break;
            case PARAM_KEY_FLOAT:
                bundle.putFloat(key, Float.parseFloat(value));
                break;
            case PARAM_KEY_DOUBLE:
                bundle.putDouble(key, Double.parseDouble(value));
                break;
            case PARAM_KEY_BOOLEAN:
                bundle.putBoolean(key, Boolean.parseBoolean(value));
                break;
            case PARAM_KEY_STRING:
            default:
                bundle.putString(key, value);
                break;
        }
    }

    /**
     * Fetch params for uri.
     *
     * @param uri    target uri.
     * @param bundle bundle object where the params should be stored.
     * @return A bundle object witch store the params.
     */
    private static Bundle parseParams(Uri uri, Bundle bundle) {
        if (bundle == null) {
            bundle = new Bundle();
        }
        bundle.putString(Rabbit.KEY_ORIGIN_URI, uri.toString());
        Set<String> keys;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
            keys = uri.getQueryParameterNames();
        } else {
            keys = getQueryParameterNames(uri);
        }
        if (keys == null || keys.size() == 0) {
            return bundle;
        }
        for (String key : keys) {
            String params = uri.getQueryParameter(key);
            bundle.putString(key, params);
        }
        return bundle;
    }

    /**
     * Fetch keys from an Uri object. The code is from SDK 25.
     *
     * @param uri the Uri
     * @return A set of keys.
     */
    private static Set<String> getQueryParameterNames(Uri uri) {
        if (uri.isOpaque()) {
            throw new UnsupportedOperationException("This isn't a hierarchical URI.");
        }

        String query = uri.getEncodedQuery();
        if (query == null) {
            return Collections.emptySet();
        }

        Set<String> names = new LinkedHashSet<>();
        int start = 0;
        do {
            int next = query.indexOf('&', start);
            int end = (next == -1) ? query.length() : next;

            int separator = query.indexOf('=', start);
            if (separator > end || separator == -1) {
                separator = end;
            }

            String name = query.substring(start, separator);
            names.add(decode(name));

            // Move start to end of name.
            start = end + 1;
        } while (start < query.length());

        return Collections.unmodifiableSet(names);
    }

    /**
     * Fetch flags from uri.
     *
     * @param uri target uri.
     * @return flags fetched from uri.
     */
    private static int parseFlags(Uri uri) {
        String mode = uri.getQueryParameter(Mappings.MAPPING_QUERY_MODE);
        int flags = 0;
        if (TextUtils.isEmpty(mode)) {
            flags = 0;
        } else {
            if (mode.contains(Mappings.MODE_CLEAR_TOP)) {
                flags |= Intent.FLAG_ACTIVITY_CLEAR_TOP;
            }
            if (mode.contains(Mappings.MODE_NEW_TASK)) {
                flags |= Intent.FLAG_ACTIVITY_NEW_TASK;
            }
        }
        return flags;
    }

    static String dump() {
        StringBuilder sb = new StringBuilder();
        Set<Map.Entry<String, String>> entries = sMappingsGroup.getMappings().entrySet();
        sb.append("mappings : ");
        sb.append("{").append("\n");
        for (Map.Entry<String, String> e : entries) {
            sb.append(e.getKey()).append(" -> ").append(decode(e.getValue())).append("\n");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append("}");
        if (sMappingsGroup.getAllowedHosts() != null && sMappingsGroup.getAllowedHosts().size() > 0) {
            sb.append("\n\n").append("allowed hosts : ");
            sb.append("{").append("\n");
            for (String h : sMappingsGroup.getAllowedHosts()) {
                sb.append("\t").append(h).append("\n\n");
            }
            sb.deleteCharAt(sb.length() - 1);
            sb.append("}");
        }
        return sb.toString();
    }

    private static String decode(String origin) {
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
