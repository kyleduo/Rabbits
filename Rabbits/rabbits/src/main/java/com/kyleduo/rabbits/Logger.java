package com.kyleduo.rabbits;

import android.util.Log;

/**
 * Internal logger.
 *
 * Created by kyle on 28/02/2018.
 */

class Logger {
    private static final String TAG = "Rabbits";

    static void d(String message) {
        if (!Rabbit.sDebug) {
            return;
        }
        Log.d(TAG, message);
    }

    static void v(String message) {
        if (!Rabbit.sDebug) {
            return;
        }
        Log.v(TAG, message);
    }

    static void i(String message) {
        if (!Rabbit.sDebug) {
            return;
        }
        Log.i(TAG, message);
    }

    static void w(String message) {
        if (!Rabbit.sDebug) {
            return;
        }
        Log.w(TAG, message);
    }

    static void e(String message) {
        if (!Rabbit.sDebug) {
            return;
        }
        Log.e(TAG, message);
    }
}
