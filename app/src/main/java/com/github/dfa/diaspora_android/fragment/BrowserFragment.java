/*
    This file is part of the Diaspora for Android.

    Diaspora for Android is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Diaspora for Android is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with the Diaspora for Android.

    If not, see <http://www.gnu.org/licenses/>.
 */
package com.github.dfa.diaspora_android.fragment;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ProgressBar;

import com.github.dfa.diaspora_android.App;
import com.github.dfa.diaspora_android.R;
import com.github.dfa.diaspora_android.activity.MainActivity;
import com.github.dfa.diaspora_android.data.AppSettings;
import com.github.dfa.diaspora_android.ui.ContextMenuWebView;
import com.github.dfa.diaspora_android.util.AppLog;
import com.github.dfa.diaspora_android.util.ProxyHandler;
import com.github.dfa.diaspora_android.util.theming.ThemeHelper;
import com.github.dfa.diaspora_android.webview.CustomWebViewClient;
import com.github.dfa.diaspora_android.webview.ProgressBarWebChromeClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Fragment with a webView and a ProgressBar.
 * This Fragment retains its instance.
 * Created by vanitas on 26.09.16.
 */

public class BrowserFragment extends ThemedFragment {
    public static final String TAG = "com.github.dfa.diaspora_android.BrowserFragment";

    protected View rootLayout;
    protected ContextMenuWebView webView;
    protected ProgressBar progressBar;
    protected AppSettings appSettings;
    protected CustomWebViewClient webViewClient;
    protected WebSettings webSettings;

    protected String pendingUrl;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        AppLog.d(this, "onCreateView()");
        if (rootLayout == null) {
            rootLayout = inflater.inflate(R.layout.browser__fragment, container, false);
        }
        return rootLayout;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        AppLog.d(this, "onViewCreated()");
        super.onViewCreated(view, savedInstanceState);

        if (this.appSettings == null) {
            this.appSettings = ((App) getActivity().getApplication()).getSettings();
        }

        if (this.webView == null) {
            this.webView = (ContextMenuWebView) view.findViewById(R.id.webView);
            this.applyWebViewSettings();
            ProxyHandler.getInstance().addWebView(webView);
        }

        if (this.progressBar == null) {
            this.progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        }

        if (pendingUrl != null) {
            loadUrl(pendingUrl);
            pendingUrl = null;
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                webView.setParentActivity(getActivity());
            }
        });
        this.setRetainInstance(true);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (getRetainInstance() && rootLayout.getParent() instanceof ViewGroup) {
            ((ViewGroup) rootLayout.getParent()).removeView(rootLayout);
        }
    }

    private void applyWebViewSettings() {
        this.webSettings = webView.getSettings();
        webSettings.setAllowFileAccess(false);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
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
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (webView != null) {
                    webSettings.setMinimumFontSize(appSettings.getMinimumFontSize());
                    webSettings.setLoadsImagesAutomatically(appSettings.isLoadImages());
                }
            }
        });
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    protected boolean makeScreenshotOfWebView(boolean hasToShareScreenshot) {
        AppLog.i(this, "StreamFragment.makeScreenshotOfWebView()");
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            int hasWRITE_EXTERNAL_STORAGE = getActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (hasWRITE_EXTERNAL_STORAGE != PackageManager.PERMISSION_GRANTED) {
                if (!shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    new AlertDialog.Builder(getContext())
                            .setMessage(R.string.permissions_screenshot)
                            .setNegativeButton(android.R.string.no, null)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (android.os.Build.VERSION.SDK_INT >= 23)
                                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                                MainActivity.REQUEST_CODE_ASK_PERMISSIONS);
                                }
                            })
                            .show();
                    return false;
                }
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MainActivity.REQUEST_CODE_ASK_PERMISSIONS);
                return false;
            }
        }

        Date dateNow = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yy_MM_dd--HH_mm_ss", Locale.getDefault());
        File fileSaveDirectory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/Diaspora");

        String fileSaveName = hasToShareScreenshot ? ".DfA_share.jpg" : String.format("DfA_%s.jpg", dateFormat.format(dateNow));
        if (!fileSaveDirectory.exists()) {
            if (!fileSaveDirectory.mkdirs()) {
                AppLog.w(this, "Could not mkdir " + fileSaveDirectory.getAbsolutePath());
            }
        }

        if (!hasToShareScreenshot) {
            Snackbar.make(webView, getString(R.string.share__toast_screenshot) + " " + fileSaveName, Snackbar.LENGTH_LONG).show();
        }

        Bitmap bitmap;
        webView.setDrawingCacheEnabled(true);
        bitmap = Bitmap.createBitmap(webView.getDrawingCache());
        webView.setDrawingCacheEnabled(false);

        OutputStream bitmapWriter = null;
        try {
            bitmapWriter = new FileOutputStream(new File(fileSaveDirectory, fileSaveName));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, bitmapWriter);
            bitmapWriter.flush();
            bitmap.recycle();
        } catch (Exception e) {
            return false;
        } finally {
            if (bitmapWriter != null) {
                try {
                    bitmapWriter.close();
                } catch (IOException _ignSaveored) {/* Nothing */}
            }
        }

        // Only show share intent when Action Share Screenshot was selected
        if (hasToShareScreenshot) {
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("image/jpeg");
            sharingIntent.putExtra(Intent.EXTRA_SUBJECT, webView.getTitle());
            sharingIntent.putExtra(Intent.EXTRA_TEXT, webView.getUrl());
            Uri bmpUri = Uri.fromFile(new File(fileSaveDirectory, fileSaveName));
            sharingIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
            startActivity(Intent.createChooser(sharingIntent, getString(R.string.action_share_dotdotdot)));
        } else {
            // Broadcast that this file is indexable
            File file = new File(fileSaveDirectory, fileSaveName);
            Uri uri = Uri.fromFile(file);
            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri);
            getActivity().sendBroadcast(intent);
        }
        return true;
    }

    @Override
    public String getFragmentTag() {
        return TAG;
    }

    @Override
    public void onCreateBottomOptionsMenu(Menu menu, MenuInflater inflater) {
        /* Nothing to do here */
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
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (getWebView() != null) {
                    getWebView().reload();
                }
            }
        });

    }

    public ContextMenuWebView getWebView() {
        return this.webView;
    }

    @Override
    protected void applyColorToViews() {
        ThemeHelper.updateProgressBarColor(progressBar);
    }
}
