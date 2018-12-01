/*
    This file is part of the dandelion*.

    dandelion* is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    dandelion* is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with the dandelion*.

    If not, see <http://www.gnu.org/licenses/>.
 */
package com.github.dfa.diaspora_android.web;

import android.content.Context;
import android.content.MutableContextWrapper;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ProgressBar;

import com.github.dfa.diaspora_android.App;
import com.github.dfa.diaspora_android.R;
import com.github.dfa.diaspora_android.ui.theme.ThemeHelper;
import com.github.dfa.diaspora_android.ui.theme.ThemedFragment;
import com.github.dfa.diaspora_android.util.AppLog;
import com.github.dfa.diaspora_android.util.AppSettings;
import android.support.v4.widget.SwipeRefreshLayout;//pull to refresh

/**
 * Fragment with a webView and a ProgressBar.
 * This Fragment retains its instance.
 * Created by vanitas on 26.09.16.
 */

public class BrowserFragment extends ThemedFragment {
    public static final String TAG = "com.github.dfa.diaspora_android.BrowserFragment";

    protected ContextMenuWebView webView;
    protected ProgressBar progressBar;
    protected AppSettings appSettings;
    protected CustomWebViewClient webViewClient;
    protected WebSettings webSettings;

    protected String pendingUrl;
    protected SwipeRefreshLayout swipe;//pull to refresh

    @Override
    protected int getLayoutResId() {
        return R.layout.browser__fragment;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        AppLog.d(this, "onViewCreated()");
        super.onViewCreated(view, savedInstanceState);

        if (this.appSettings == null) {
            this.appSettings = ((App) getActivity().getApplication()).getSettings();
        }

        if (this.webView == null) {
            this.webView = view.findViewById(R.id.webView);
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    BrowserFragment.this.applyWebViewSettings();
                }
            });

            ProxyHandler.getInstance().addWebView(webView);
        }

        if (this.progressBar == null) {
            this.progressBar = view.findViewById(R.id.progressBar);
        }

        if (pendingUrl != null) {
            loadUrl(pendingUrl);
            pendingUrl = null;
        }

        webView.setParentActivity(getActivity());

        this.setRetainInstance(true);

        //pull to refresh
    swipe = view.findViewById(R.id.swipe);
    swipe.setOnRefreshListener(() -> reloadUrl());
    swipe.setDistanceToTriggerSync(2000);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (getRetainInstance() && getView() != null && getView().getParent() instanceof ViewGroup) {
            ((ViewGroup) getView().getParent()).removeView(getView());
        }
    }

    private void applyWebViewSettings() {
        this.webSettings = webView.getSettings();
        webSettings.setAllowFileAccess(false);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUserAgentString("Mozilla/5.0 (Linux; U; Android 4.4.4; Nexus 5 Build/KTU84P) AppleWebkit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30");
        webSettings.setDomStorageEnabled(true);
        webSettings.setMinimumFontSize(appSettings.getMinimumFontSize());
        webSettings.setLoadsImagesAutomatically(appSettings.isLoadImages());
        webSettings.setAppCacheEnabled(true);

        if (android.os.Build.VERSION.SDK_INT >= 21) {
            WebView.enableSlowWholeDocumentDraw();
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        this.registerForContextMenu(webView);
        //webView.setParentActivity(this);
        webView.setOverScrollMode(WebView.OVER_SCROLL_ALWAYS);

        this.webViewClient = new CustomWebViewClient((App) getActivity().getApplication(), webView);
        webView.setWebViewClient(webViewClient);
        webView.setWebChromeClient(new ProgressBarWebChromeClient(webView, progressBar));
    }

    @Override
    public void onResume() {
        super.onResume();
        if (webView != null) {
            webSettings.setMinimumFontSize(appSettings.getMinimumFontSize());
            webSettings.setLoadsImagesAutomatically(appSettings.isLoadImages());
        }
    }

    @Override
    public String getFragmentTag() {
        return TAG;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (webView != null && webView.getContext() instanceof MutableContextWrapper) {
            ((MutableContextWrapper) webView.getContext()).setBaseContext(context);
        }
    }

    public boolean onBackPressed() {
        if (webView.canGoBack()) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    webView.goBack();
                }
            });
            return true;
        }
        return false;
    }

    public void loadUrl(final String url) {
        if (getWebView() != null) {
            AppLog.v(this, "loadUrl(): load " + url);
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    getWebView().loadUrlNew(url);
                }
            });

        } else {
            AppLog.v(this, "loadUrl(): WebView null: Set pending url to " + url);
            pendingUrl = url;
        }
    }

    public String getUrl() {
        if (getWebView() != null) {
            return getWebView().getUrl();
        } else {
            return pendingUrl;
        }
    }

    public void reloadUrl() {
        AppLog.v(this, "reloadUrl()");
        if (getWebView() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    getWebView().reload();
                    swipe.setRefreshing(false);//pull to refresh
                }
            });

        }
    }

    public ContextMenuWebView getWebView() {
        return this.webView;
    }

    @Override
    protected void applyColorToViews() {
        ThemeHelper.updateProgressBarColor(progressBar);
    }
}
