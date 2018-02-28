package com.kyleduo.rabbits.demo;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.kyleduo.rabbits.DispatchResult;
import com.kyleduo.rabbits.Rabbit;
import com.kyleduo.rabbits.Rules;
import com.kyleduo.rabbits.annotations.Page;
import com.kyleduo.rabbits.demo.base.BaseFragment;

/**
 * Created by kyle on 2016/12/12.
 */
@Page("/web")
public class WebFragment extends BaseFragment {
    public static final String KEY_URL = "url";

    private WebView mWebView;

    @SuppressLint("SetJavaScriptEnabled")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mWebView = new WebView(getContext());
        mWebView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mWebView.setWebViewClient(new DefaultWebViewClient());
        mWebView.getSettings().setUseWideViewPort(false);
        mWebView.getSettings().setJavaScriptEnabled(true);
        return mWebView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle extras = getArguments();
        String url = "file:///android_asset/web.html";
        if (extras != null) {
            url = extras.getString(KEY_URL, url);
        }
        mWebView.loadUrl(url);
    }

    private class DefaultWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            boolean ret = Rabbit.from(WebFragment.this)
                    .to(url)
                    .ignoreFallback()
                    .start()
                    .getCode() == DispatchResult.STATUS_SUCCESS;
            if (ret) {
                return true;
            }
            if (Rules.scheme().is("tel").verify(Uri.parse(url))) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(url));
                startActivity(intent);
                view.reload();
                return true;
            }
            return super.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
        }
    }

    @Override
    public boolean onBackPressedSupport() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        }
        return super.onBackPressedSupport();
    }
}
