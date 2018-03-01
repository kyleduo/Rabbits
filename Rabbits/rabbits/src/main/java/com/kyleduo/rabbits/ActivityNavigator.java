package com.kyleduo.rabbits;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

/**
 * Navigate to an Activity.
 * <p>
 * Created by kyle on 26/01/2018.
 */

public class ActivityNavigator implements Navigator {
    @Override
    public RabbitResult perform(Action action) {
        Object target = action.getTarget();
        if (target == null) {
            return RabbitResult.notFound(action.getOriginUrl());
        }
        if (action.isJustObtain()) {
            return RabbitResult.success(target);
        }

        Object from = action.getFrom();
        if (from == null) {
            return RabbitResult.error("The \"from\" can not be null");
        }
        final int requestCode = action.getRequestCode();
        final Intent intent = (Intent) action.getTarget();

        Activity activity = null;
        
        if (requestCode > 0) {
            if (from instanceof Activity) {
                ((Activity) from).startActivityForResult(intent, requestCode);
            } else if (from instanceof Fragment) {
                ((Fragment) from).startActivityForResult(intent, requestCode);
            } else if (from instanceof android.app.Fragment) {
                ((android.app.Fragment) from).startActivityForResult(intent, requestCode);
            } else if (from instanceof Context) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ((Context) from).startActivity(intent);
            } else {
                return RabbitResult.error("Invalid from.");
            }
        } else {
            if (from instanceof Activity) {
                activity = (Activity) from;
            } else if (from instanceof Fragment) {
                activity = ((Fragment) from).getActivity();
            } else if (from instanceof android.app.Fragment) {
                activity = ((android.app.Fragment) from).getActivity();
            }
            if (activity != null) {
                activity.startActivity((Intent) action.getTarget());
            } else if (from instanceof Context) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ((Context) from).startActivity(intent);
            } else {
                return RabbitResult.error("Invalid from.");
            }
        }

        return RabbitResult.success();
    }
}
