package com.kyleduo.rabbits.demo.utils;

import android.net.Uri;

/**
 * Created by kyle on 2017/1/6.
 */

public class UriUtils {
	public static boolean matchPath(Uri uri, String path) {
		return uri != null && uri.getPath() != null && uri.getPath().equals(path);
	}
}
