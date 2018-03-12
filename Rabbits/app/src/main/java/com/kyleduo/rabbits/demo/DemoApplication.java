package com.kyleduo.rabbits.demo;

import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.Toast;

import com.kyleduo.rabbits.Action;
import com.kyleduo.rabbits.Interceptor;
import com.kyleduo.rabbits.Navigator;
import com.kyleduo.rabbits.P;
import com.kyleduo.rabbits.Rabbit;
import com.kyleduo.rabbits.RabbitConfig;
import com.kyleduo.rabbits.RabbitResult;
import com.kyleduo.rabbits.TargetInfo;
import com.kyleduo.rabbits.demo.base.BaseActivity;
import com.kyleduo.rabbits.demo.base.BaseFragment;
import com.kyleduo.rabbits.demo.utils.Constants;
import com.kyleduo.rabbits.rules.Rule;
import com.kyleduo.rabbits.rules.RuleSet;
import com.kyleduo.rabbits.rules.Rules;

import me.yokeyword.fragmentation.SupportFragment;

/**
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

        Rabbit.init(
                RabbitConfig.get()
                        .schemes("demo", "http", "https")
                        .domains("rabbits.kyleduo.com", "blog.kyleduo.com", "rabbits.kyleduo.com")
                        .debug(true))
                // do not open any native pages when there is a query named 'greenChannel'
                // and it's value equals '1'. This useful when use Rabbit as a bridge between
                // native and web page.
                .addInterceptor(new Interceptor() {
                    @Override
                    public RabbitResult intercept(Dispatcher dispatcher) {
                        dispatcher.action().discard();
                        return dispatcher.dispatch(dispatcher.action());
                    }
                }, Rules.set(RuleSet.Relation.OR, Rules.query("greenChannel").is("1"), Rules.query("ignore").is("1")))
                .addInterceptor(new Interceptor() {
                    @Override
                    public RabbitResult intercept(Dispatcher dispatcher) {
                        dispatcher.action().setRedirect(true);
                        return dispatcher.dispatch(dispatcher.action());
                    }
                }, Rules.query("redirect").is("1"))
                .addInterceptor(new Interceptor() {
                    @Override
                    public RabbitResult intercept(Dispatcher dispatcher) {
                        // ignore other following interceptors.
                        dispatcher.action().getExtras().putString("param", "rules");
                        dispatcher.action().setIgnoreInterceptors(true);
                        Toast.makeText(DemoApplication.this, "Interceptor by Rules", Toast.LENGTH_SHORT).show();
                        return dispatcher.dispatch(dispatcher.action());
                    }
                }, Rules.set(RuleSet.Relation.AND, Rules.scheme().startsWith("demo"), Rules.domain().contains("kyleduo"), Rules.path().contains("/rules")))
                .addInterceptor(new Interceptor() {
                    @Override
                    public RabbitResult intercept(final Dispatcher dispatcher) {
                        final Action action = dispatcher.action();
                        if (action.getFrom() instanceof Context) {
                            action.getExtras().putString("param", "set in interceptor");
                            new AlertDialog.Builder((Context) action.getFrom())
                                    .setTitle("Intercepted")
                                    .setMessage("The navigation has been intercepted by interceptor. \n\nA param has been set in the interceptor.")
                                    .setPositiveButton("Go on", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dispatcher.dispatch(action);
                                        }
                                    })
                                    .setNegativeButton("Cancel", null).create().show();
                            return null;
                        }
                        return dispatcher.dispatch(action);
                    }
                }, Rules.flags().has(1))
                // add Interceptor with totally custom rules
                .addInterceptor(new Interceptor() {
                    @Override
                    public RabbitResult intercept(Dispatcher dispatcher) {
                        return null;
                    }
                }, new Rule() {
                    @Override
                    public boolean verify(Action action) {
                        return false;
                    }
                })
                .registerNavigator(TargetInfo.TYPE_FRAGMENT_V4, new FragmentNavigator())
                .registerFallbackNavigator(new FallbackNavigator());
    }

    public static class FragmentNavigator implements Navigator {

        @Override
        public RabbitResult perform(Action action) {
            Object from = action.getFrom();
            Object target = action.getTarget();

            boolean isBase = target instanceof BaseFragment;

            if (!isBase) {
                return RabbitResult.error("Target invalid");
            }

            if ((action.getTargetFlags() & Constants.FLAG_FRAG_EMBED) > 0) {
                if (from instanceof BaseFragment) {
                    if (action.isRedirect()) {
                        ((BaseFragment) from).replaceFragment((SupportFragment) target, false);
                    } else {
                        ((BaseFragment) from).start((SupportFragment) target);
                    }
                    return RabbitResult.success();
                } else if (from instanceof BaseActivity) {
                    if (((BaseActivity) from).getTopFragment() != null) {
                        if (action.isRedirect()) {
                            ((BaseActivity) from).getTopFragment().replaceFragment((SupportFragment) target, false);
                        } else {
                            ((BaseActivity) from).start((SupportFragment) target);
                        }
                    }
                    return RabbitResult.success();
                }
            }

            return Rabbit.from(from)
                    .to(P.P_FRAGMENT_CONTAINER)
                    .action(action)
                    .putExtra(FragmentContainerActivity.KEY_FRAG_URL, action.getOriginUrl())
                    .start();
        }
    }

    public static class FallbackNavigator implements Navigator {

        @Override
        public RabbitResult perform(Action action) {
            Uri uri = action.createUri();
            if (TextUtils.isEmpty(uri.getScheme()) || !uri.getScheme().startsWith("http")) {
                uri = uri.buildUpon().scheme("https").build();
            }
            if (TextUtils.isEmpty(uri.getAuthority())) {
                uri = uri.buildUpon().authority("kyleduo.com").build();
            }
            return Rabbit.from(action.getFrom())
                    .to(P.P_WEB)
                    .putExtra(WebFragment.KEY_URL, uri.toString())
                    .start();
        }
    }
}
