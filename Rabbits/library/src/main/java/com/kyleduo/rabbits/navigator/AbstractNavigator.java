package com.kyleduo.rabbits.navigator;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.RequiresApi;
import android.util.Size;
import android.util.SizeF;
import android.util.SparseArray;

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
	 * Original uri.
	 */
	protected Uri mUri;
	/**
	 * Where does this navigator from.
	 */
	protected Object mFrom;
	/**
	 * Where should this navigator go.
	 */
	protected Object mTo;
	/**
	 * Used for Fragment back stacks.
	 */
	protected String mTag;
	/**
	 * Extras params for this navigator.
	 */
	protected Bundle mExtras;
	/**
	 * Flags for Intent.
	 */
	protected int mIntentFlags;

	protected List<INavigationInterceptor> mInterceptors;

	public AbstractNavigator(Uri uri, Object from, Object to, String tag, int flags, Bundle extras, List<INavigationInterceptor> interceptors) {
		mFrom = from;
		mTo = to;
		mTag = tag;
		mExtras = extras;
		mIntentFlags = flags;
		mInterceptors = interceptors;
		mUri = uri;
	}

	/**
	 * Check out whether there are flag witch specific this navigation should start a new task.
	 * For Fragments, maybe it should start a new activity.
	 *
	 * @return should start in new task
	 */
	protected boolean shouldNewTask() {
		return (mIntentFlags & Intent.FLAG_ACTIVITY_NEW_TASK) != 0;
	}

	/**
	 * Check out whether there are flag witch specific this navigation should clear top of its task.
	 * For Fragments, maybe it should remove top fragments.
	 *
	 * @return should start with clear top
	 */
	protected boolean shouldClearTop() {
		return (mIntentFlags & Intent.FLAG_ACTIVITY_CLEAR_TOP) != 0;
	}

	public AbstractNavigator setFrom(Object from) {
		mFrom = from;
		return this;
	}

	public AbstractNavigator setTo(Object to) {
		mTo = to;
		return this;
	}

	public AbstractNavigator setTag(String tag) {
		mTag = tag;
		return this;
	}

	public AbstractNavigator setExtras(Bundle extras) {
		mExtras = extras;
		return this;
	}

	public abstract boolean start();

	public abstract boolean startForResult(int requestCode);

	public AbstractNavigator addIntentFlags(int flags) {
		mIntentFlags |= flags;
		return this;
	}

	public AbstractNavigator setIntentFlags(int flags) {
		mIntentFlags = flags;
		return this;
	}

	public AbstractNavigator putString(String key, String value) {
		ensureExtras();
		mExtras.putString(key, value);
		return this;
	}

	public AbstractNavigator putBoolean(String key, boolean value) {
		ensureExtras();
		mExtras.putBoolean(key, value);
		return this;
	}

	public AbstractNavigator putInt(String key, int value) {
		ensureExtras();
		mExtras.putInt(key, value);
		return this;
	}

	public AbstractNavigator putLong(String key, long value) {
		ensureExtras();
		mExtras.putLong(key, value);
		return this;
	}

	public AbstractNavigator putDouble(String key, double value) {
		ensureExtras();
		mExtras.putDouble(key, value);
		return this;
	}

	public AbstractNavigator putBooleanArray(String key, boolean[] value) {
		ensureExtras();
		mExtras.putBooleanArray(key, value);
		return this;
	}

	public AbstractNavigator putIntArray(String key, int[] value) {
		ensureExtras();
		mExtras.putIntArray(key, value);
		return this;
	}

	public AbstractNavigator putLongArray(String key, long[] value) {
		ensureExtras();
		mExtras.putLongArray(key, value);
		return this;
	}

	public AbstractNavigator putDoubleArray(String key, double[] value) {
		ensureExtras();
		mExtras.putDoubleArray(key, value);
		return this;
	}

	public AbstractNavigator putStringArray(String key, String[] value) {
		ensureExtras();
		mExtras.putStringArray(key, value);
		return this;
	}

	public AbstractNavigator putByte(String key, byte value) {
		ensureExtras();
		mExtras.putByte(key, value);
		return this;
	}

	public AbstractNavigator putChar(String key, char value) {
		ensureExtras();
		mExtras.putChar(key, value);
		return this;
	}

	public AbstractNavigator putShort(String key, short value) {
		ensureExtras();
		mExtras.putShort(key, value);
		return this;
	}

	public AbstractNavigator putFloat(String key, float value) {
		ensureExtras();
		mExtras.putFloat(key, value);
		return this;
	}

	public AbstractNavigator putCharSequence(String key, CharSequence value) {
		ensureExtras();
		mExtras.putCharSequence(key, value);
		return this;
	}

	public AbstractNavigator putParcelable(String key, Parcelable value) {
		ensureExtras();
		mExtras.putParcelable(key, value);
		return this;
	}

	@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
	public AbstractNavigator putSize(String key, Size value) {
		ensureExtras();
		mExtras.putSize(key, value);
		return this;
	}

	@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
	public AbstractNavigator putSizeF(String key, SizeF value) {
		ensureExtras();
		mExtras.putSizeF(key, value);
		return this;
	}

	public AbstractNavigator putParcelableArray(String key, Parcelable[] value) {
		ensureExtras();
		mExtras.putParcelableArray(key, value);
		return this;
	}

	public AbstractNavigator putParcelableArrayList(String key, ArrayList<? extends Parcelable> value) {
		ensureExtras();
		mExtras.putParcelableArrayList(key, value);
		return this;
	}

	public AbstractNavigator putSparseParcelableArray(String key, SparseArray<? extends Parcelable> value) {
		ensureExtras();
		mExtras.putSparseParcelableArray(key, value);
		return this;
	}

	public AbstractNavigator putIntegerArrayList(String key, ArrayList<Integer> value) {
		ensureExtras();
		mExtras.putIntegerArrayList(key, value);
		return this;
	}

	public AbstractNavigator putStringArrayList(String key, ArrayList<String> value) {
		ensureExtras();
		mExtras.putStringArrayList(key, value);
		return this;
	}

	public AbstractNavigator putCharSequenceArrayList(String key, ArrayList<CharSequence> value) {
		ensureExtras();
		mExtras.putCharSequenceArrayList(key, value);
		return this;
	}

	public AbstractNavigator putSerializable(String key, Serializable value) {
		ensureExtras();
		mExtras.putSerializable(key, value);
		return this;
	}

	public AbstractNavigator putByteArray(String key, byte[] value) {
		ensureExtras();
		mExtras.putByteArray(key, value);
		return this;
	}

	public AbstractNavigator putShortArray(String key, short[] value) {
		ensureExtras();
		mExtras.putShortArray(key, value);
		return this;
	}

	public AbstractNavigator putCharArray(String key, char[] value) {
		ensureExtras();
		mExtras.putCharArray(key, value);
		return this;
	}

	public AbstractNavigator putFloatArray(String key, float[] value) {
		ensureExtras();
		mExtras.putFloatArray(key, value);
		return this;
	}

	public AbstractNavigator putCharSequenceArray(String key, CharSequence[] value) {
		ensureExtras();
		mExtras.putCharSequenceArray(key, value);
		return this;
	}

	public AbstractNavigator putBundle(String key, Bundle value) {
		ensureExtras();
		mExtras.putBundle(key, value);
		return this;
	}

	@SuppressWarnings("unchecked")
	public AbstractNavigator mergeExtras(Bundle bundle) {
		if (bundle == null) {
			return this;
		}
		if (mExtras == null) {
			mExtras = bundle;
		} else {
			mExtras.putAll(bundle);
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
		if (mExtras == null) {
			mExtras = new Bundle();
		}
	}
}
