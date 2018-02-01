package com.kyleduo.rabbits.demo;

import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import com.kyleduo.rabbits.Action;
import com.kyleduo.rabbits.DispatchResult;
import com.kyleduo.rabbits.Interceptor;
import com.kyleduo.rabbits.Navigator;
import com.kyleduo.rabbits.RConfig;
import com.kyleduo.rabbits.Rabbit;
import com.kyleduo.rabbits.Router;
import com.kyleduo.rabbits.annotations.TargetInfo;
import com.kyleduo.rabbits.demo.base.BaseActivity;
import com.kyleduo.rabbits.demo.base.BaseFragment;

/**
 * Created by kyle on 2016/12/8.
 */

//@Module(subModules = {"sub1"}, srcPath = DemoConstants.SRC_PATH)
public class DemoApplication extends Application {
    private static final String TAG = "DemoApplication";

    @Override
    public void onCreate() {
        super.onCreate();

        Router.generate();

        RConfig config = RConfig.get()
                .schemes("demo")
                .domains("rabbits.kyleduo.com");

        Rabbit.init(config)
                .addInterceptor(new Interceptor() {
                    @Override
                    public DispatchResult intercept(final Dispatcher dispatcher) {
                        final Action action = dispatcher.action();
                        if ((action.getTargetFlags() & 1) > 0) {
                            action.getExtras().putString("param", "拦截器中修改");
                            new AlertDialog.Builder((Context) action.getFrom())
                                    .setTitle("拦截")
                                    .setPositiveButton("继续", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dispatcher.dispatch(action);
                                        }
                                    })
                                    .setNegativeButton("取消", null).create().show();
                            return null;
                        }
                        return dispatcher.dispatch(action);
                    }
                })
                .registerNavigator(TargetInfo.TYPE_FRAGMENT_V4, new FragmentNavigator())
                .registerNavigator(TargetInfo.TYPE_NOT_FOUND, new Navigator() {
                    @Override
                    public DispatchResult perform(Action action, DispatchResult result) {
                        Toast.makeText((Context) action.getFrom(), "NOT_FOUND", Toast.LENGTH_SHORT).show();
                        return result;
                    }
                });

        final long time = SystemClock.elapsedRealtime();
        Log.d(TAG, "start : " + time);
    }

    public static class FragmentNavigator implements Navigator {

        @Override
        public DispatchResult perform(Action action, DispatchResult result) {
            Object from = action.getFrom();
            Object target = action.getTarget();

            if (!(target instanceof BaseFragment)) {
                return result.error("Target invalid");
            }

            BaseFragment fragment = (BaseFragment) target;

            if (from instanceof BaseActivity) {
                BaseActivity act = (BaseActivity) from;
                act.start(fragment);
            } else if (from instanceof BaseFragment) {
                ((BaseFragment) from).start(fragment);
            } else {
                return result.error("From invalid");
            }

            return result.success();
        }
    }
}
