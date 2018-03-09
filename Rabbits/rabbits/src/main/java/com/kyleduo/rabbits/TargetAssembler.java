package com.kyleduo.rabbits;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

/**
 * Assemble all the start information to target, which is an Intent or a Fragment object.
 * <p>
 * Created by kyle on 26/01/2018.
 */

public class TargetAssembler implements InternalInterceptor {
    @Override
    public RabbitResult intercept(Dispatcher dispatcher) {
        Logger.i("[!] Assembling...");
        Action action = dispatcher.action();
        int targetType = action.getTargetType();

        if (targetType == TargetInfo.TYPE_NONE) {
            // process on
            return dispatcher.dispatch(action);
        }

        // assemble Intent or Fragment instance.
        Object target = null;
        if (targetType == TargetInfo.TYPE_ACTIVITY) {
            Intent intent;
            if (action.getFrom() instanceof Context) {
                intent = new Intent((Context) action.getFrom(), action.getTargetClass());
            } else if (action.getFrom() instanceof Fragment) {
                intent = new Intent(((Fragment) action.getFrom()).getActivity(), action.getTargetClass());
            } else if (action.getFrom() instanceof android.app.Fragment) {
                intent = new Intent(((android.app.Fragment) action.getFrom()).getActivity(), action.getTargetClass());
            } else {
                return RabbitResult.error("From object must be whether an Context or a Fragment instance.");
            }
            intent.setFlags(action.getIntentFlags());
            intent.putExtras(action.getExtras());
            target = intent;
        } else if (targetType == TargetInfo.TYPE_FRAGMENT) {
            try {
                android.app.Fragment fragment = (android.app.Fragment) action.getTargetClass().newInstance();
                fragment.setArguments(action.getExtras());
                target = fragment;
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } else if (targetType == TargetInfo.TYPE_FRAGMENT_V4) {
            try {
                Fragment fragment = (Fragment) action.getTargetClass().newInstance();
                fragment.setArguments(action.getExtras());
                target = fragment;
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        action.setTarget(target);

        return dispatcher.dispatch(action);
    }
}
