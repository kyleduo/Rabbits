package com.kyleduo.rabbits;

import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.kyleduo.rabbits.annotations.TargetInfo;

/**
 * Assemble all the start information to target, which is an Intent or a Fragment object.
 * <p>
 * Created by kyle on 26/01/2018.
 */

public class AssembleInterceptor implements Interceptor {
    @Override
    public DispatchResult intercept(Dispatcher dispatcher) {
        Action action = dispatcher.action();

        TargetInfo targetInfo = action.getTargetInfo();
        if (targetInfo == null) {
            // Handled by NavigationInterceptor
            return dispatcher.dispatch(action);
        }

        // TODO: 30/01/2018 Add Rabbits custom content params


        // assemble Intent or Fragment instance.
        Object target = null;
        if (targetInfo.type == TargetInfo.TYPE_ACTIVITY) {
            Intent intent = new Intent((Context) action.getFrom(), targetInfo.target);
            intent.setFlags(action.getIntentFlags());
            intent.putExtras(action.getExtras());
            target = intent;
        } else if (targetInfo.type == TargetInfo.TYPE_FRAGMENT) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    android.app.Fragment fragment = (android.app.Fragment) targetInfo.target.newInstance();
                    fragment.setArguments(action.getExtras());
                    target = fragment;
                }
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } else if (targetInfo.type == TargetInfo.TYPE_FRAGMENT_V4) {
            try {
                android.support.v4.app.Fragment fragment = (android.support.v4.app.Fragment) targetInfo.target.newInstance();
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
