package com.kyleduo.rabbits.demo;

import android.app.Application;
import android.os.SystemClock;
import android.util.Log;

import com.kyleduo.rabbits.MappingsGroup;
import com.kyleduo.rabbits.MappingsLoaderCallback;
import com.kyleduo.rabbits.P;
import com.kyleduo.rabbits.RConfig;
import com.kyleduo.rabbits.Rabbit;
import com.kyleduo.rabbits.Target;
import com.kyleduo.rabbits.annotations.Module;
import com.kyleduo.rabbits.demo.navigation.DemoNavigatorFactory;
import com.kyleduo.rabbits.demo.utils.UriUtils;
import com.kyleduo.rabbits.navigator.INavigationInterceptor;

/**
 * Created by kyle on 2016/12/8.
 */

@Module(subModules = {"sub1"})
public class DemoApplication extends Application {
    private static final String TAG = "DemoApplication";

    @Override
    public void onCreate() {
        super.onCreate();

        RConfig config = RConfig.get()
                .scheme("demo")
                .defaultHost("rabbits.kyleduo.com")
                .forceUpdatePersist(false)
                .navigatorFactory(new DemoNavigatorFactory());
        Rabbit.init(config);

        // syc setup
//        Rabbit.setup(this);

        final long time = SystemClock.elapsedRealtime();
        Log.d(TAG, "start : " + time);
        // async setup
        Rabbit.asyncSetup(this, new MappingsLoaderCallback() {
            @Override
            public void onMappingsLoaded(MappingsGroup mappings) {
                long endTime = SystemClock.elapsedRealtime();
                Log.d(TAG, "stop  : " + endTime + "  cost: " + (endTime - time) + "ms");
            }

            @Override
            public void onMappingsLoadFail() {
                Log.d(TAG, "fail");
            }

            @Override
            public void onMappingsPersisted(boolean success) {
                long endTime = SystemClock.elapsedRealtime();
                Log.d(TAG, "p stop  : " + endTime + "  cost: " + (endTime - time) + "ms");
            }
        });

        Rabbit.addGlobalInterceptor(new INavigationInterceptor() {
            @Override
            public boolean intercept(Object from, Target target) {
                if (UriUtils.matchPath(target.getUri(), "/intercept/dump")) {
                    Rabbit.from(from)
                            .to(P.DUMP)
                            .mergeExtras(target.getExtras())
                            .start();
                    return true;
                }
                return false;
            }
        });
    }
}
