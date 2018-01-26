package com.kyleduo.rabbits.demo;

import android.app.Application;
import android.os.SystemClock;
import android.util.Log;

/**
 * Created by kyle on 2016/12/8.
 */

//@Module(subModules = {"sub1"}, srcPath = DemoConstants.SRC_PATH)
public class DemoApplication extends Application {
    private static final String TAG = "DemoApplication";

    @Override
    public void onCreate() {
        super.onCreate();

//        RConfig config = RConfig.get()
//                .scheme("demo")
//                .defaultHost("rabbits.kyleduo.com")
//                .forceUpdatePersist(BuildConfig.DEBUG)
//                .navigatorFactory(new DemoNavigatorFactory());
//        Rabbit.init(config);

        // syc setup
//        Rabbit.setup(this);

        Router.generate();

        final long time = SystemClock.elapsedRealtime();
        Log.d(TAG, "start : " + time);
        // async setup
//        Rabbit.asyncSetup(this, new MappingsLoaderCallback() {
//            @Override
//            public void onMappingsLoaded(MappingsGroup mappings) {
//                long endTime = SystemClock.elapsedRealtime();
//                Log.d(TAG, "stop  : " + endTime + "  cost: " + (endTime - time) + "ms");
//
//                Log.d(TAG, "Current mappings version: " + Rabbit.currentVersion());
//            }
//
//            @Override
//            public void onMappingsLoadFail() {
//                Log.d(TAG, "fail");
//            }
//
//            @Override
//            public void onMappingsPersisted(boolean success) {
//                long endTime = SystemClock.elapsedRealtime();
//                Log.d(TAG, "persist stop  : " + endTime + "  cost: " + (endTime - time) + "ms");
//            }
//        });

//        Rabbit.addGlobalInterceptor(new INavigationInterceptor() {
//            @Override
//            public boolean intercept(Object from, Target target) {
//                if (UriUtils.matchPath(target.getUri(), "/intercept/dump")) {
//                    Rabbit.from(from)
//                            .to(P.DUMP)
//                            .merge(target)
//                            .start();
//                    return true;
//                }
//                return false;
//            }
//        });
    }
}
