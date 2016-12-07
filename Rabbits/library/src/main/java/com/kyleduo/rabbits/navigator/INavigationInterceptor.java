package com.kyleduo.rabbits.navigator;

import android.net.Uri;
import android.os.Bundle;

/**
 * Interface of interceptor.
 *
 * Created by kyle on 2016/12/7.
 */

public interface INavigationInterceptor {
	boolean intercept(Uri uri, Object from, Object to, String page, int intentFlags, Bundle extras);
}
