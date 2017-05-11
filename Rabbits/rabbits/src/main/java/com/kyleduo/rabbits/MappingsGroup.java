package com.kyleduo.rabbits;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Hold the content of mapping.
 * Created by kyleduo on 2017/5/9.
 */

public class MappingsGroup {
    private static final String MAPPING_KEY_MAPPINGS = "mappings";
    private static final String MAPPING_KEY_ALLOWED_HOSTS = "allowed_hosts";

    private ArrayList<String> mAllowedHosts;
    private LinkedHashMap<String, String> mMappings;

    /**
     * origin json if created with {@link MappingsGroup#fromJson(java.lang.String)}
     */
    private String mOriginJson;

    private MappingsGroup() {
    }

    MappingsGroup(MappingsGroup origin) {
        this.merge(origin, true);
    }

    public LinkedHashMap<String, String> getMappings() {
        return mMappings;
    }

    ArrayList<String> getAllowedHosts() {
        return mAllowedHosts;
    }

    boolean valid() {
        return mMappings != null;
    }

    static MappingsGroup fromJson(String json) {
        MappingsGroup mappingsGroup = new MappingsGroup();
        mappingsGroup.mOriginJson = json;

        try {
            JSONObject jo = new JSONObject(json);
            JSONArray allowed_hosts = jo.optJSONArray(MAPPING_KEY_ALLOWED_HOSTS);
            if (allowed_hosts != null && allowed_hosts.length() > 0) {
                mappingsGroup.mAllowedHosts = new ArrayList<>();
                for (int i = 0; i < allowed_hosts.length(); i++) {
                    mappingsGroup.mAllowedHosts.add(allowed_hosts.optString(i));
                }
            }
            LinkedHashMap<String, String> temp = new LinkedHashMap<>();
            JSONObject mappings = jo.optJSONObject(MAPPING_KEY_MAPPINGS);
            Iterator<String> uris = mappings.keys();
            while (uris.hasNext()) {
                String uri = uris.next();
                String page = mappings.optString(uri);
                temp.put(uri, page);
            }
            mappingsGroup.mMappings = temp;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return mappingsGroup;
    }

    String toJson() {
        if (mOriginJson != null) {
            return mOriginJson;
        }
        String json = null;
        if (mMappings.size() > 0) {
            try {
                JSONObject wrapper = new JSONObject();
                if (mAllowedHosts != null && mAllowedHosts.size() == 0) {
                    JSONArray allowed = new JSONArray();
                    for (String h : mAllowedHosts) {
                        allowed.put(h);
                    }
                    wrapper.put(MAPPING_KEY_ALLOWED_HOSTS, allowed);
                }
                JSONObject mappings = new JSONObject();
                Set<Map.Entry<String, String>> entries = mMappings.entrySet();
                for (Map.Entry<String, String> entry : entries) {
                    mappings.put(entry.getKey(), entry.getValue());
                }
                wrapper.put(MAPPING_KEY_MAPPINGS, mappings);
                json = wrapper.toString();
            } catch (JSONException e) {
                e.printStackTrace();
                throw new IllegalStateException("mapping can not be parsed to json", e);
            }
        }
        return json;
    }

    /**
     * merge another mapping to the current one.
     *
     * @param another  mapping
     * @param override whether fullyUpdate the current one. If true, current mappings' content will
     *                 be replaced by the another.
     *                 {@param fullyUpdate} should always be false while loading from multiple assets.
     *                 And may be true when update.
     */
    void merge(MappingsGroup another, boolean override) {
        if (another == null) {
            return;
        }
        if (mAllowedHosts == null) {
            mAllowedHosts = another.mAllowedHosts;
        } else if (another.mAllowedHosts != null) {
            if (override) {
                mAllowedHosts = another.mAllowedHosts;
            } else {
                for (String host : another.mAllowedHosts) {
                    if (!mAllowedHosts.contains(host)) {
                        mAllowedHosts.add(host);
                    }
                }
            }
        }

        if (mMappings == null) {
            mMappings = another.mMappings;
        } else if (another.mMappings != null) {
            if (override) {
                mMappings = another.mMappings;
            } else {
                mMappings.putAll(another.mMappings);
            }
        }
    }
}
