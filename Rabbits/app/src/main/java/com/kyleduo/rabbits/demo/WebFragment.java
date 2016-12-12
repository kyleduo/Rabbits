package com.kyleduo.rabbits.demo;

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

import com.kyleduo.rabbits.Rabbit;
import com.kyleduo.rabbits.annotations.Page;
import com.kyleduo.rabbits.annotations.PageType;
import com.kyleduo.rabbits.demo.base.BaseFragment;
import com.kyleduo.rabbits.navigator.INavigationInterceptor;

/**
 * Created by kyle on 2016/12/12.
 */
@Page(name = "WEB", type = PageType.FRAGMENT)
public class WebFragment extends BaseFragment {

	private WebView mWebView;

	public static WebFragment newInstance() {

		Bundle args = new Bundle();

		WebFragment fragment = new WebFragment();
		fragment.setArguments(args);
		return fragment;
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		mWebView = new WebView(getContext());
		mWebView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
		mWebView.setWebViewClient(new DefaultWebViewClient());
		return mWebView;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		String url = "file:///android_asset/web.html";
		mWebView.loadUrl(url);
	}

	public void load(String url) {
		mWebView.loadUrl(url);
	}

	private class DefaultWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			INavigationInterceptor webInterceptor = new INavigationInterceptor() {
				@Override
				public boolean intercept(Uri uri, Object from, Object to, String page, int intentFlags, Bundle extras) {
					if (uri.getPath().equals("/tobeintercept")) {
						Rabbit.from(WebFragment.this)
								.to("demo://rabbits.kyleduo.com/test")
								.mergeExtras(extras)
								.clearTop()
								.start();
						return true;
					}
					return false;
				}
			};

			boolean ret = Rabbit.from(WebFragment.this)
					.addInterceptor(webInterceptor)
					.tryTo(url)
					.start();
			return ret || super.shouldOverrideUrlLoading(view, url);
		}

		@Override
		public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
			super.onReceivedError(view, request, error);
		}
	}
}
