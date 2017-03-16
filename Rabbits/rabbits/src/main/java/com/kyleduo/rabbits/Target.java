package com.kyleduo.rabbits;

import android.net.Uri;
import android.os.Bundle;

/**
 * Represents a navigation target.
 * <p>
 * Created by kyle on 2016/12/9.
 */

public class Target {
	private Uri mUri;
	private String mPage;
	private int mFlags;
	private boolean mFinishPrevious;
	private Bundle mExtras;
	private Object mTo;
	private int[] mTransitionAnimations;

	Target(Uri uri) {
		mUri = uri;
	}

	public Uri getUri() {
		return mUri;
	}

	public String getPage() {
		return mPage;
	}

	public void setPage(String page) {
		mPage = page;
	}

	public int getFlags() {
		return mFlags;
	}

	public void setFlags(int flags) {
		mFlags = flags;
	}

	public Bundle getExtras() {
		return mExtras;
	}

	public void setExtras(Bundle extras) {
		mExtras = extras;
	}

	public Object getTo() {
		return mTo;
	}

	public void setTo(Object to) {
		mTo = to;
	}

	Target route(IRouter router) {
		if (mPage == null) {
			return this;
		}
		mTo = router.route(mPage);
		return this;
	}

	Target obtain(IRouter router) {
		if (mPage == null) {
			return this;
		}
		mTo = router.obtain(mPage);
		return this;
	}

	boolean hasMatched() {
		return mTo != null && mPage != null;
	}

	@Override
	public String toString() {
		return "Target{" +
				"mUri=" + mUri +
				", mPage='" + mPage + '\'' +
				", mFlags=" + mFlags +
				", mExtras=" + mExtras +
				", mTo=" + mTo +
				'}';
	}

	public boolean shouldFinishPrevious() {
		return mFinishPrevious;
	}

	public void setFinishPrevious(boolean finishPrevious) {
		mFinishPrevious = finishPrevious;
	}

	public int[] getTransitionAnimations() {
		return mTransitionAnimations;
	}

	public void setTransitionAnimations(int[] transitionAnimations) {
		mTransitionAnimations = transitionAnimations;
	}
}
