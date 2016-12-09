package com.kyleduo.rabbits;

import android.net.Uri;
import android.os.Bundle;

/**
 * Created by kyle on 2016/12/9.
 */

public class Target {
	private Uri mUri;
	private String mPage;
	private int mFlags;
	private Bundle mExtras;
	private Object mTo;

	public Target(Uri uri) {
		mUri = uri;
	}

	public Uri getUri() {
		return mUri;
	}

	void setUri(Uri uri) {
		mUri = uri;
	}

	public String getPage() {
		return mPage;
	}

	void setPage(String page) {
		mPage = page;
	}

	public int getFlags() {
		return mFlags;
	}

	void setFlags(int flags) {
		mFlags = flags;
	}

	public Bundle getExtras() {
		return mExtras;
	}

	void setExtras(Bundle extras) {
		mExtras = extras;
	}

	public Object getTo() {
		return mTo;
	}

	void setTo(Object to) {
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
}
