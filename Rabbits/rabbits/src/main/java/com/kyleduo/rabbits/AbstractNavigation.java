package com.kyleduo.rabbits;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.util.Size;
import android.util.SizeF;
import android.util.SparseArray;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by kyle on 26/01/2018.
 */

public abstract class AbstractNavigation implements Navigation {
    protected Action action;
    private List<Interceptor> mInterceptors;

    AbstractNavigation(Action action) {
        this.action = action;
    }

    @NonNull
    @Override
    public Action action() {
        return action;
    }

    @Override
    public List<Interceptor> interceptors() {
        return mInterceptors;
    }

    @Override
    public Navigation addIntentFlags(int flags) {
        action.setIntentFlags(action.getIntentFlags() | flags);
        return this;
    }

    @Override
    public Navigation setIntentFlags(int flags) {
        action.setIntentFlags(flags);
        return this;
    }

    @Override
    public Navigation newTask() {
        addIntentFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return this;
    }

    @Override
    public Navigation clearTop() {
        addIntentFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return this;
    }

    @Override
    public Navigation singleTop() {
        addIntentFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        return this;
    }

    @Override
    public Navigation redirect() {
        action.setRedirect(true);
        return this;
    }

    @Override
    public Navigation putExtra(@NonNull String key, Object value) {
        Bundle extras = action.getExtras();
        if (extras == null) {
            extras = new Bundle();
        }
        if (value == null) {
            extras.remove(key);
        }
        action.setExtras(extras);
        if (value instanceof Integer) {
            extras.putInt(key, (Integer) value);
        } else if (value instanceof String) {
            extras.putString(key, (String) value);
        } else if (value instanceof Boolean) {
            extras.putBoolean(key, (Boolean) value);
        } else if (value instanceof Long) {
            extras.putLong(key, (Long) value);
        } else if (value instanceof Double) {
            extras.putDouble(key, (Double) value);
        } else if (value instanceof double[]) {
            extras.putDoubleArray(key, (double[]) value);
        } else if (value instanceof int[]) {
            extras.putIntArray(key, (int[]) value);
        } else if (value instanceof long[]) {
            extras.putLongArray(key, (long[]) value);
        } else if (value instanceof boolean[]) {
            extras.putBooleanArray(key, (boolean[]) value);
        } else if (value instanceof String[]) {
            extras.putStringArray(key, (String[]) value);
        } else if (value instanceof Byte) {
            extras.putByte(key, (Byte) value);
        } else if (value instanceof Character) {
            extras.putChar(key, (char) value);
        } else if (value instanceof Short) {
            extras.putShort(key, (short) value);
        } else if (value instanceof Float) {
            extras.putFloat(key, (Float) value);
        } else if (value instanceof CharSequence) {
            extras.putCharSequence(key, (CharSequence) value);
        } else if (value instanceof Size) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                extras.putSize(key, (Size) value);
            }
        } else if (value instanceof SizeF) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                extras.putSizeF(key, (SizeF) value);
            }
        } else if (value instanceof Parcelable[]) {
            extras.putParcelableArray(key, (Parcelable[]) value);
        } else if (value instanceof ArrayList<?>) {
            // WARNING
            Type[] mySuperClass = value.getClass().getGenericInterfaces();
            Type type = mySuperClass[0];
            if (Parcelable.class.isAssignableFrom((Class<?>) type)) {
                //noinspection unchecked
                extras.putParcelableArrayList(key, (ArrayList<? extends Parcelable>) value);
            }
        } else if (value instanceof SparseArray<?>) {
            Type[] mySuperClass = value.getClass().getGenericInterfaces();
            Type type = mySuperClass[0];
            if (Parcelable.class.isAssignableFrom((Class<?>) type)) {
                //noinspection unchecked
                extras.putSparseParcelableArray(key, (SparseArray<? extends Parcelable>) value);
            }
        } else if (value instanceof byte[]) {
            extras.putByteArray(key, (byte[]) value);
        } else if (value instanceof short[]) {
            extras.putShortArray(key, (short[]) value);
        } else if (value instanceof char[]) {
            extras.putCharArray(key, (char[]) value);
        } else if (value instanceof float[]) {
            extras.putFloatArray(key, (float[]) value);
        } else if (value instanceof CharSequence[]) {
            extras.putCharSequenceArray(key, (CharSequence[]) value);
        } else if (value instanceof Serializable) {
            extras.putSerializable(key, (Serializable) value);
        }

        return this;
    }

    @Override
    public Navigation putExtras(Bundle bundle) {
        if (bundle == null) {
            action.setExtras(null);
        } else {
            action.getExtras().putAll(bundle);
        }
        return this;
    }

    @Override
    public Navigation putExtras(Map<String, Object> extras) {
        if (extras == null) {
            action.setExtras(null);
        } else {
            for (Map.Entry<String, Object> e : extras.entrySet()) {
                putExtra(e.getKey(), e.getValue());
            }
        }
        return this;
    }

    @Override
    public Navigation ignoreInterceptors() {
        action.setIgnoreInterceptors(true);
        return this;
    }

    @Override
    public Navigation ignoreFallback() {
        action.setIgnoreFallback(true);
        return this;
    }

    @Override
    public Navigation addInterceptor(Interceptor interceptor) {
        if (mInterceptors == null) {
            mInterceptors = new ArrayList<>();
        }
        mInterceptors.add(interceptor);
        return this;
    }

    @Override
    public Navigation addInterceptor(Interceptor interceptor, Rule rule) {
        if (mInterceptors == null) {
            mInterceptors = new ArrayList<>();
        }
        mInterceptors.add(new PatternInterceptor(interceptor, rule));
        return this;
    }

    @Override
    public Navigation justObtain() {
        action.setJustObtain(true);
        return this;
    }

    @Override
    public Navigation forResult(int requestCode) {
        action.setRequestCode(requestCode);
        return this;
    }

    @Override
    public Navigation setTransitionAnimations(int[] transitionAnimations) {
        action.setTransitionAnimations(transitionAnimations);
        return this;
    }
}
