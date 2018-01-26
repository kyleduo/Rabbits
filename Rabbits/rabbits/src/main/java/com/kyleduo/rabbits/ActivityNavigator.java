package com.kyleduo.rabbits;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.Fragment;

/**
 * Navigate to an Activity.
 * <p>
 * Created by kyle on 26/01/2018.
 */

public class ActivityNavigator implements Navigator {
    @Override
    public DispatchResult perform(Action action, DispatchResult result) {
        if (result.getStatus() == DispatchResult.STATUS_NOT_FOUND) {
            return result;
        }
        Object from = action.getFrom();
        if (from == null) {
            return result.error("From can not be null");
        }
        Activity activity = null;
        if (from instanceof Activity) {
            activity = (Activity) from;
        } else if (from instanceof Fragment) {
            activity = ((Fragment) from).getActivity();
        } else if (from instanceof android.app.Fragment) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                activity = ((android.app.Fragment) from).getActivity();
            }
        }
        if (activity != null) {
            if (action.getRequestCode() > 0) {
                activity.startActivityForResult((Intent) action.getTarget(), action.getRequestCode());
            } else {
                activity.startActivity((Intent) action.getTarget());
            }
        } else {
            if (from instanceof Context) {
                Intent intent = (Intent) action.getTarget();
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ((Context) from).startActivity(intent);
            } else {
                return result.error("Invalid from.");
            }
        }

        int[] animations = action.getTransitionAnimations();

        if (activity != null
                && animations != null
                && animations.length == 2) {
            activity.overridePendingTransition(animations[0], animations[1]);
        }

        return result.success();
    }
}
