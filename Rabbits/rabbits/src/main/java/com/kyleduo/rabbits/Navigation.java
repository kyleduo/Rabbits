package com.kyleduo.rabbits;

import android.os.Bundle;
import android.support.annotation.NonNull;

import java.util.List;
import java.util.Map;

/**
 * Represent a Navigation into which the caller put extras and configures and perform the navigation.
 *
 * Created by kyle on 26/01/2018.
 */

@SuppressWarnings("unused")
public interface Navigation {
    Navigation to(String url);

    Navigation addIntentFlags(int flags);

    Navigation setIntentFlags(int flags);

    Navigation newTask();

    Navigation clearTop();

    Navigation singleTop();

    Navigation putExtra(@NonNull String key, Object value);

    Navigation putExtras(Bundle bundle);

    Navigation putExtras(Map<String, Object> extras);

    Navigation addInterceptor(Interceptor interceptor);

    Navigation addInterceptor(Interceptor interceptor, Rule rule);

    Navigation justObtain();

    Navigation forResult(int requestCode);

    Navigation redirect();

    Navigation ignoreInterceptors();

    Navigation ignoreFallback();

    Navigation setTransitionAnimations(int[] transitionAnimations);

    @NonNull
    DispatchResult start();

    @NonNull
    DispatchResult startForResult(int requestCode);

    @NonNull
    DispatchResult obtain();

    @NonNull
    Action action();

    List<Interceptor> interceptors();
}
