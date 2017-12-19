package com.kyleduo.rabbits.navigator;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.IInterface;
import android.os.Parcelable;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.util.Size;
import android.util.SizeF;
import android.util.SparseArray;

import com.kyleduo.rabbits.Rabbit;
import com.kyleduo.rabbits.Target;
import com.kyleduo.rabbits.dispatcher.DefaultDispatcher;
import com.kyleduo.rabbits.dispatcher.IDispatcher;
import com.kyleduo.rabbits.dispatcher.InterceptorDispatcher;
import com.kyleduo.rabbits.interceptor.IInterceptor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Interface used to :
 * 1. start a Activity (for result)
 * 2. show a Fragment
 * 3. return a Fragment instance
 * 4. check whether intercept
 * Created by kyle on 2016/12/7.
 */

@SuppressWarnings({"WeakerAccess", "unused"})
public abstract class AbstractNavigator implements IProvider {
    private static final String TAG = "AbstractNavigator";

    /**
     * Where does this navigator from.
     */
    protected Object mFrom;

    protected Target mTarget;

    protected List<INavigationInterceptor> mInterceptors;

    public AbstractNavigator(Object from, Target target, List<INavigationInterceptor> interceptors) {
        mFrom = from;
        mTarget = target;
        mInterceptors = interceptors;
    }

    /**
     * Check out whether there are flag witch specific this navigation should start a new task.
     * For Fragments, maybe it should start a new activity.
     *
     * @return should start in new task
     */
    protected boolean shouldNewTask() {
        return (mTarget.getFlags() & Intent.FLAG_ACTIVITY_NEW_TASK) != 0;
    }

    /**
     * Check out whether there are flag witch specific this navigation should clear top of its task.
     * For Fragments, maybe it should remove top fragments.
     *
     * @return should start with clear top
     */
    protected boolean shouldClearTop() {
        return (mTarget.getFlags() & Intent.FLAG_ACTIVITY_CLEAR_TOP) != 0;
    }

    public AbstractNavigator setFrom(Object from) {
        mFrom = from;
        return this;
    }

    public AbstractNavigator setTo(Object to) {
        mTarget.setTo(to);
        return this;
    }

    public AbstractNavigator setPage(String tag) {
        mTarget.setPage(tag);
        return this;
    }

    public AbstractNavigator setExtras(Bundle extras) {
        mTarget.setExtras(extras);
        return this;
    }

    public AbstractNavigator finishPrevious(boolean finishPrevious) {
        mTarget.setFinishPrevious(finishPrevious);
        return this;
    }

    /**
     * Merge all information of {@code target} into this navigator's even the {@link Rabbit#KEY_ORIGIN_URI}.
     * So you can redirect to the origin url after open this target page. This is used when open an
     * Activity in which open a Fragment later.
     *
     * @param target target to be redirect
     * @return Navigator
     */
    public AbstractNavigator redirect(Target target) {
        return this.merge(target, false);
    }

    /**
     * Merge another into this navigator. This method will merge extras, intent flags and other options
     * from another target likes {@link AbstractNavigator#redirect(Target)} besides this method will
     * remove {@link Rabbit#KEY_ORIGIN_URI} and {@link Rabbit#KEY_SOURCE_URI} which means no trace
     * will be remain of the other target.
     *
     * @param target target to be merged
     * @return Navigator
     */
    public AbstractNavigator merge(Target target) {
        return this.merge(target, true);
    }

    private AbstractNavigator merge(Target target, boolean clean) {
        if (clean && target.getExtras() != null) {
            target.getExtras().remove(Rabbit.KEY_ORIGIN_URI);
            target.getExtras().remove(Rabbit.KEY_SOURCE_URI);
        }
        this.mergeExtras(target.getExtras())
                .setIntentFlags(target.getFlags())
                .finishPrevious(target.shouldFinishPrevious());
        if (target.getTransitionAnimations() != null) {
            setTransitionAnimations(target.getTransitionAnimations());
        }
        return this;
    }

    /**
     * Navigate to another page.
     *
     * @return true for success
     */
    @SuppressWarnings("SimplifiableIfStatement")
    public boolean start() {
        if (checkInterceptor()) {
            return true;
        }
        final Object to = mTarget.getTo();
        if (to == null) {
            return false;
        }
        return handleStart(-1);
    }

    /**
     * Navigate to another page for result.
     *
     * @param requestCode requestCode
     * @return true for success
     */
    @SuppressWarnings("SimplifiableIfStatement")
    public boolean startForResult(int requestCode) {
        if (checkInterceptor()) {
            return true;
        }
        final Object to = mTarget.getTo();
        if (to == null) {
            return false;
        }
        return handleStart(requestCode);
    }

    protected boolean handleStart(int requestCode) {
        IDispatcher dispatcher = new DefaultDispatcher();

        List<IInterceptor> is = new ArrayList<>();

        final int count = is.size();
        InterceptorDispatcher id = null;
        for (int i = count - 1; i >= 0; i--) {
            id = new InterceptorDispatcher(is.get(i), id == null ? dispatcher : id);
        }

        id.dispatch(mTarget);

        return true;
    }

    private IDispatcher createDispatcher(List<IInterceptor> is, IDispatcher ds) {
        return null;
    }

    public AbstractNavigator addIntentFlags(int flags) {
        mTarget.setFlags(mTarget.getFlags() | flags);
        return this;
    }

    public AbstractNavigator setIntentFlags(int flags) {
        mTarget.setFlags(flags);
        return this;
    }

    public AbstractNavigator setTransitionAnimations(int[] animations) {
        if (animations == null || animations.length != 2) {
            Log.e(TAG, "Animations' length should be 2.");
            return this;
        }
        mTarget.setTransitionAnimations(animations);
        return this;
    }

    public AbstractNavigator putExtra(String key, String value) {
        ensureExtras();
        mTarget.getExtras().putString(key, value);
        return this;
    }

    public AbstractNavigator putExtra(String key, boolean value) {
        ensureExtras();
        mTarget.getExtras().putBoolean(key, value);
        return this;
    }

    public AbstractNavigator putExtra(String key, int value) {
        ensureExtras();
        mTarget.getExtras().putInt(key, value);
        return this;
    }

    public AbstractNavigator putExtra(String key, long value) {
        ensureExtras();
        mTarget.getExtras().putLong(key, value);
        return this;
    }

    public AbstractNavigator putExtra(String key, double value) {
        ensureExtras();
        mTarget.getExtras().putDouble(key, value);
        return this;
    }

    public AbstractNavigator putExtra(String key, boolean[] value) {
        ensureExtras();
        mTarget.getExtras().putBooleanArray(key, value);
        return this;
    }

    public AbstractNavigator putExtra(String key, int[] value) {
        ensureExtras();
        mTarget.getExtras().putIntArray(key, value);
        return this;
    }

    public AbstractNavigator putExtra(String key, long[] value) {
        ensureExtras();
        mTarget.getExtras().putLongArray(key, value);
        return this;
    }

    public AbstractNavigator putExtra(String key, double[] value) {
        ensureExtras();
        mTarget.getExtras().putDoubleArray(key, value);
        return this;
    }

    public AbstractNavigator putExtra(String key, String[] value) {
        ensureExtras();
        mTarget.getExtras().putStringArray(key, value);
        return this;
    }

    public AbstractNavigator putExtra(String key, byte value) {
        ensureExtras();
        mTarget.getExtras().putByte(key, value);
        return this;
    }

    public AbstractNavigator putExtra(String key, char value) {
        ensureExtras();
        mTarget.getExtras().putChar(key, value);
        return this;
    }

    public AbstractNavigator putExtra(String key, short value) {
        ensureExtras();
        mTarget.getExtras().putShort(key, value);
        return this;
    }

    public AbstractNavigator putExtra(String key, float value) {
        ensureExtras();
        mTarget.getExtras().putFloat(key, value);
        return this;
    }

    public AbstractNavigator putExtra(String key, CharSequence value) {
        ensureExtras();
        mTarget.getExtras().putCharSequence(key, value);
        return this;
    }

    public AbstractNavigator putExtra(String key, Parcelable value) {
        ensureExtras();
        mTarget.getExtras().putParcelable(key, value);
        return this;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public AbstractNavigator putExtra(String key, Size value) {
        ensureExtras();
        mTarget.getExtras().putSize(key, value);
        return this;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public AbstractNavigator putExtra(String key, SizeF value) {
        ensureExtras();
        mTarget.getExtras().putSizeF(key, value);
        return this;
    }

    public AbstractNavigator putExtra(String key, Parcelable[] value) {
        ensureExtras();
        mTarget.getExtras().putParcelableArray(key, value);
        return this;
    }

    public AbstractNavigator putExtra(String key, ArrayList<? extends Parcelable> value) {
        ensureExtras();
        mTarget.getExtras().putParcelableArrayList(key, value);
        return this;
    }

    public AbstractNavigator putExtra(String key, SparseArray<? extends Parcelable> value) {
        ensureExtras();
        mTarget.getExtras().putSparseParcelableArray(key, value);
        return this;
    }

    public AbstractNavigator putExtra(String key, Serializable value) {
        ensureExtras();
        mTarget.getExtras().putSerializable(key, value);
        return this;
    }

    public AbstractNavigator putExtra(String key, byte[] value) {
        ensureExtras();
        mTarget.getExtras().putByteArray(key, value);
        return this;
    }

    public AbstractNavigator putExtra(String key, short[] value) {
        ensureExtras();
        mTarget.getExtras().putShortArray(key, value);
        return this;
    }

    public AbstractNavigator putExtra(String key, char[] value) {
        ensureExtras();
        mTarget.getExtras().putCharArray(key, value);
        return this;
    }

    public AbstractNavigator putExtra(String key, float[] value) {
        ensureExtras();
        mTarget.getExtras().putFloatArray(key, value);
        return this;
    }

    public AbstractNavigator putExtra(String key, CharSequence[] value) {
        ensureExtras();
        mTarget.getExtras().putCharSequenceArray(key, value);
        return this;
    }

    public AbstractNavigator putExtra(String key, Bundle value) {
        ensureExtras();
        mTarget.getExtras().putBundle(key, value);
        return this;
    }

    @SuppressWarnings("unchecked")
    public AbstractNavigator mergeExtras(Bundle bundle) {
        if (bundle == null) {
            return this;
        }
        if (mTarget.getExtras() == null) {
            mTarget.setExtras(bundle);
        } else {
            if (mTarget.getExtras().get(Rabbit.KEY_SOURCE_URI) != null) {
                bundle.remove(Rabbit.KEY_SOURCE_URI);
            }
            mTarget.getExtras().putAll(bundle);
        }
        return this;
    }

    public AbstractNavigator newTask() {
        addIntentFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return this;
    }

    public AbstractNavigator clearTop() {
        addIntentFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return this;
    }

    public AbstractNavigator broughtToFront() {
        addIntentFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        return this;
    }

    private void ensureExtras() {
        if (mTarget.getExtras() == null) {
            mTarget.setExtras(new Bundle());
        }
    }

    protected boolean checkInterceptor() {
        if (mInterceptors != null) {
            for (INavigationInterceptor i : mInterceptors) {
                if (i.intercept(mFrom, mTarget)) {
                    return true;
                }
            }
        }
        return false;
    }
}
