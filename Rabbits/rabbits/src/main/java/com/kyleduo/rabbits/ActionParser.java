package com.kyleduo.rabbits;

import android.net.Uri;
import android.os.Bundle;

import java.util.Map;
import java.util.Set;

/**
 * Find TargetInfo matching the url.
 * <p>
 * Created by kyle on 30/01/2018.
 */

public class ActionParser implements InternalInterceptor {
    @Override
    public RabbitResult intercept(Dispatcher dispatcher) {
        Logger.i("[!] Parsing...");
        Action action = dispatcher.action();
        Uri uri = action.getUri();

        // generate uri
        if (uri == null) {
            String url = action.getOriginUrl();
            if (url.contains(":")) {
                uri = Uri.parse(url);
            } else {
                if (!url.startsWith("/")) {
                    url = "/" + url;
                }
                url = Rabbit.get().getSchemes().get(0) + "://" + Rabbit.get().getDomains().get(0) + url;
                uri = Uri.parse(url);
            }
            action.setUri(uri);
        }

        TargetInfo target = RouteTable.match(action.getUri());

        action.setTargetType(target == null ? TargetInfo.TYPE_NONE : target.type);
        action.setTargetFlags(target == null ? 0 : target.flags);
        action.setTargetPattern(target == null ? null : target.pattern);
        action.setTargetClass(target == null ? null : target.target);

        if (target != null) {
            Bundle bundle = new Bundle();

            // params from REST url
            Map<String, Object> urlParams = target.params;
            if (urlParams != null) {
                for (Map.Entry<String, Object> entry : urlParams.entrySet()) {
                    String key = entry.getKey();
                    Object value = entry.getValue();
                    if (value instanceof Integer) {
                        bundle.putInt(key, (Integer) value);
                    } else if (value instanceof Float) {
                        bundle.putFloat(key, (Float) value);
                    } else if (value instanceof Double) {
                        bundle.putDouble(key, (Double) value);
                    } else if (value instanceof String) {
                        bundle.putString(key, (String) value);
                    } else {
                        bundle.putString(key, value.toString());
                    }
                }
            }

            // query params
            Set<String> keys;
            keys = uri.getQueryParameterNames();
            if (keys != null && keys.size() > 0) {
                for (String key : keys) {
                    String params = uri.getQueryParameter(key);
                    bundle.putString(key, params);
                }
            }

            if (action.getExtras() != null) {
                bundle.putAll(action.getExtras());
            }

            // Rabbits param, high priority.
            bundle.putString(Rabbit.KEY_ORIGIN_URL, action.getOriginUrl());
            bundle.putString(Rabbit.KEY_PATTERN, target.pattern);

            action.setExtras(bundle);
        } else {
            if (action.getExtras() == null) {
                action.setExtras(new Bundle());
            }
        }

        return dispatcher.dispatch(action);
    }
}
