package com.kyleduo.rabbits.demo;

import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.widget.Toast;

import com.kyleduo.rabbits.Action;
import com.kyleduo.rabbits.DispatchResult;
import com.kyleduo.rabbits.Interceptor;
import com.kyleduo.rabbits.Navigator;
import com.kyleduo.rabbits.P;
import com.kyleduo.rabbits.RConfig;
import com.kyleduo.rabbits.Rabbit;
import com.kyleduo.rabbits.Rule;
import com.kyleduo.rabbits.RuleSet;
import com.kyleduo.rabbits.Rules;
import com.kyleduo.rabbits.TargetInfo;
import com.kyleduo.rabbits.demo.base.BaseFragment;

/**
 *
 * Created by kyle on 2016/12/8.
 */

public class DemoApplication extends Application {
    @SuppressWarnings("unused")
    private static final String TAG = "DemoApplication";

    private static DemoApplication sApp;

    public static DemoApplication get() {
        return sApp;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sApp = this;

        Rabbit.init(RConfig.get().schemes("demo", "http", "https").domains("rabbits.kyleduo.com", "blog.kyleduo.com"))
                // do not open any native pages when there is a query named 'greenChannel'
                // and it's value equals '1'. This useful when use Rabbit as a bridge between
                // native and web page.
                .addInterceptor(new Interceptor() {
                    @Override
                    public DispatchResult intercept(Dispatcher dispatcher) {
                        dispatcher.action().discard();
                        return dispatcher.dispatch(dispatcher.action());
                    }
                }, Rules.set(RuleSet.Relation.OR, Rules.query("greenChannel").is("1"), Rules.query("ignore").is("1")))
                .addInterceptor(new Interceptor() {
                    @Override
                    public DispatchResult intercept(Dispatcher dispatcher) {
                        // ignore other following interceptors.
                        dispatcher.action().getExtras().putString("param", "rules");
                        dispatcher.action().setIgnoreInterceptors(true);
                        Toast.makeText(DemoApplication.this, "Interceptor by Rules", Toast.LENGTH_SHORT).show();
                        return dispatcher.dispatch(dispatcher.action());
                    }
                }, Rules.path().contains("/rules"))
                .addInterceptor(new Interceptor() {
                    @Override
                    public DispatchResult intercept(final Dispatcher dispatcher) {
                        final Action action = dispatcher.action();
                        if ((action.getTargetFlags() & 1) > 0) {
                            if (action.getFrom() instanceof Context) {
                                action.getExtras().putString("param", "interceptor");
                                new AlertDialog.Builder((Context) action.getFrom())
                                        .setTitle("Intercepted")
                                        .setMessage("The navigation has been intercepted by interceptor. \nA param has been set in the interceptor.")
                                        .setPositiveButton("Go on", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dispatcher.dispatch(action);
                                            }
                                        })
                                        .setNegativeButton("Cancel", null).create().show();
                                return null;
                            }
                        }
                        return dispatcher.dispatch(action);
                    }
                })
                // add Interceptor with totally custom rules
                .addInterceptor(new Interceptor() {
                    @Override
                    public DispatchResult intercept(Dispatcher dispatcher) {
                        return null;
                    }
                }, new Rule() {
                    @Override
                    public boolean verify(Uri uri) {
                        return false;
                    }
                })
                .registerNavigator(TargetInfo.TYPE_FRAGMENT_V4, new FragmentNavigator())
                .registerFallbackNavigator(new Navigator() {
                    @Override
                    public DispatchResult perform(Action action) {
                        Toast.makeText((Context) action.getFrom(), "fallback", Toast.LENGTH_SHORT).show();
                        return DispatchResult.success();
                    }
                });
    }

    public static class FragmentNavigator implements Navigator {

        @Override
        public DispatchResult perform(Action action) {
            Object from = action.getFrom();
            Object target = action.getTarget();

            boolean isBase = target instanceof BaseFragment;

            if (!isBase) {
                return DispatchResult.error("Target invalid");
            }

            return Rabbit.from(from)
                    .to(P.P_FRAGMENT_CONTAINER)
                    .action(action)
                    .putExtra(FragmentContainerActivity.KEY_FRAG_URL, action.getOriginUrl())
                    .start();
        }
    }
}
