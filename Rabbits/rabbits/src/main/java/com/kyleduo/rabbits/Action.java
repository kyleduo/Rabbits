package com.kyleduo.rabbits;

import android.net.Uri;
import android.os.Bundle;

import com.kyleduo.rabbits.annotations.TargetInfo;

/**
 * Created by kyle on 26/01/2018.
 */

public class Action {
    private Object mFrom;
    private Object mTarget;
    private String mOriginUrl;
    private Uri mUri;
    private TargetInfo mTargetInfo;
    private String mReferer;
    private boolean mRedirect;
    private Bundle mExtras;
    private int mIntentFlags;
    private int[] mTransitionAnimations;
    private int mRequestCode;
    private String parent;
    private boolean mIgnoreIntercepts;
    private boolean mIgnoreFallbacks;
    private boolean mJustObtain;
    private Action mNext;

    public Object getFrom() {
        return mFrom;
    }

    void setFrom(Object from) {
        mFrom = from;
    }

    public Object getTarget() {
        return mTarget;
    }

    public void setTarget(Object target) {
        mTarget = target;
    }

    public String getOriginUrl() {
        return mOriginUrl;
    }

    public void setOriginUrl(String originUrl) {
        mOriginUrl = originUrl;
    }

    public Uri getUri() {
        return mUri;
    }

    public void setUri(Uri uri) {
        mUri = uri;
    }

    public String getReferer() {
        return mReferer;
    }

    public void setReferer(String referer) {
        mReferer = referer;
    }

    public boolean isRedirect() {
        return mRedirect;
    }

    public void setRedirect(boolean redirect) {
        mRedirect = redirect;
    }

    public Bundle getExtras() {
        return mExtras;
    }

    public void setExtras(Bundle extras) {
        mExtras = extras;
    }

    public int getIntentFlags() {
        return mIntentFlags;
    }

    public void setIntentFlags(int intentFlags) {
        mIntentFlags = intentFlags;
    }

    public int[] getTransitionAnimations() {
        return mTransitionAnimations;
    }

    public void setTransitionAnimations(int[] transitionAnimations) {
        mTransitionAnimations = transitionAnimations;
    }

    public int getRequestCode() {
        return mRequestCode;
    }

    public void setRequestCode(int requestCode) {
        mRequestCode = requestCode;
    }

    public boolean isIgnoreIntercepts() {
        return mIgnoreIntercepts;
    }

    public void setIgnoreIntercepts(boolean ignoreIntercepts) {
        mIgnoreIntercepts = ignoreIntercepts;
    }

    public boolean isIgnoreFallbacks() {
        return mIgnoreFallbacks;
    }

    public void setIgnoreFallbacks(boolean ignoreFallbacks) {
        mIgnoreFallbacks = ignoreFallbacks;
    }

    public Action getNext() {
        return mNext;
    }

    public void setNext(Action next) {
        mNext = next;
    }

    public boolean isJustObtain() {
        return mJustObtain;
    }

    public void setJustObtain(boolean justObtain) {
        mJustObtain = justObtain;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }


    public TargetInfo getTargetInfo() {
        return mTargetInfo;
    }

    public void setTargetInfo(TargetInfo targetInfo) {
        mTargetInfo = targetInfo;
    }
}
