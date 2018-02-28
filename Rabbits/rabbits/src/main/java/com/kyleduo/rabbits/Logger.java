package com.kyleduo.rabbits;

import android.util.Log;

/**
 * Created by kyle on 28/02/2018.
 */

class Logger {
    private static final String TAG = "Rabbits";

    public static void d(String message) {
        if (!Rabbit.sDebug) {
            return;
        }
        Log.d(TAG, message);
    }

    public static void v(String message) {
        if (!Rabbit.sDebug) {
            return;
        }
        Log.v(TAG, message);
    }

    public static void i(String message) {
        if (!Rabbit.sDebug) {
            return;
        }
        Log.i(TAG, message);
    }

    public static void w(String message) {
        if (!Rabbit.sDebug) {
            return;
        }
        Log.w(TAG, message);
    }

    public static void e(String message) {
        if (!Rabbit.sDebug) {
            return;
        }
        Log.e(TAG, message);
    }
}
