package com.kyleduo.rabbits;

import android.os.Bundle;
import android.support.annotation.NonNull;

import java.util.Map;

/**
 * Created by kyle on 26/01/2018.
 */

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

    Navigation justObtain(boolean justObtain);

    DispatchResult start();

    DispatchResult obtain();

    Action action();
}
