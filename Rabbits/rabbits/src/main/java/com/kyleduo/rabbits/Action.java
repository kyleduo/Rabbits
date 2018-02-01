package com.kyleduo.rabbits;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;

/**
 * Created by kyle on 26/01/2018.
 */

public class Action {
    // Set before dispatch
    private Object mFrom;
    /**
     * Origin url witch the caller use.
     */
    private String mOriginUrl;
    private Bundle mExtras;
    private int mIntentFlags;
    private int[] mTransitionAnimations;
    private boolean mIgnoreIntercepts;
    private boolean mIgnoreFallbacks;
    private boolean mJustObtain;
    private int mRequestCode;

    // Generate during dispatch
    /**
     * Uri used by Rabbit to find the target page.
     */
    private Uri mUri;
    // information from target info
    private String mTargetPattern;
    private int mTargetFlags;
    private int mTargetType;
    private Class<?> mTargetClass;
    /**
     * Real navigation target, maybe an Intent or a Fragment instance.
     */
    private Object mTarget;

    // Not used for now
    private boolean mRedirect;

    public Object getFrom() {
        return mFrom;
    }

    void setFrom(Object from) {
        mFrom = from;
    }

    @Nullable
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

    public boolean isRedirect() {
        return mRedirect;
    }

    public void setRedirect(@SuppressWarnings("SameParameterValue") boolean redirect) {
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

    public boolean isJustObtain() {
        return mJustObtain;
    }

    public void setJustObtain(boolean justObtain) {
        mJustObtain = justObtain;
    }

    public int getTargetFlags() {
        return mTargetFlags;
    }

    public void setTargetFlags(int targetFlags) {
        mTargetFlags = targetFlags;
    }

    public int getTargetType() {
        return mTargetType;
    }

    public void setTargetType(int targetType) {
        mTargetType = targetType;
    }

    public Class<?> getTargetClass() {
        return mTargetClass;
    }

    public void setTargetClass(Class<?> targetClass) {
        mTargetClass = targetClass;
    }

    public String getTargetPattern() {
        return mTargetPattern;
    }

    public void setTargetPattern(String targetPattern) {
        mTargetPattern = targetPattern;
    }
}
