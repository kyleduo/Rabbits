package com.kyleduo.rabbits.navigator;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.RequiresApi;
import android.util.Size;
import android.util.SizeF;
import android.util.SparseArray;

import com.kyleduo.rabbits.Target;

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

	public AbstractNavigator ignoreParent(boolean ignoreParent) {
		mTarget.setIgnoreParent(ignoreParent);
		return this;
	}

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

	protected abstract boolean handleStart(int requestCode);

	public AbstractNavigator addIntentFlags(int flags) {
		mTarget.setFlags(mTarget.getFlags() | flags);
		return this;
	}

	public AbstractNavigator setIntentFlags(int flags) {
		mTarget.setFlags(flags);
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
