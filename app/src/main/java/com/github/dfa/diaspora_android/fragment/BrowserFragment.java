package com.github.dfa.diaspora_android.fragment;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.github.dfa.diaspora_android.App;
import com.github.dfa.diaspora_android.R;
import com.github.dfa.diaspora_android.activity.MainActivity;
import com.github.dfa.diaspora_android.data.AppSettings;
import com.github.dfa.diaspora_android.ui.ContextMenuWebView;
import com.github.dfa.diaspora_android.util.DiasporaUrlHelper;
import com.github.dfa.diaspora_android.webview.CustomWebViewClient;
import com.github.dfa.diaspora_android.webview.ProgressBarWebChromeClient;
import com.github.dfa.diaspora_android.util.AppLog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import info.guardianproject.netcipher.NetCipher;
import info.guardianproject.netcipher.webkit.WebkitProxy;

/**
 * Fragment with a webView and a ProgressBar.
 * This Fragment retains its instance.
 * Created by vanitas on 26.09.16.
 */

public class BrowserFragment extends CustomFragment {
    public static final String TAG = "com.github.dfa.diaspora_android.BrowserFragment";

    protected View rootLayout;
    protected SwipeRefreshLayout swipeRefreshLayout;
    protected ContextMenuWebView webView;
    protected ProgressBar progressBar;
    protected AppSettings appSettings;
    protected CustomWebViewClient webViewClient;
    protected WebSettings webSettings;
    protected DiasporaUrlHelper urls;

    protected String pendingUrl;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        AppLog.d(this, "onCreateView()");
        if(rootLayout == null) {
            rootLayout = inflater.inflate(R.layout.browser__fragment, container, false);
        }
        return rootLayout;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        AppLog.d(this, "onViewCreated()");
        super.onViewCreated(view, savedInstanceState);

        if(this.appSettings == null) {
            this.appSettings = ((App) getActivity().getApplication()).getSettings();
            this.urls = new DiasporaUrlHelper(appSettings);
        }

        if(this.webView == null) {
            this.webView = (ContextMenuWebView) view.findViewById(R.id.webView);
            this.applyWebViewSettings();
        }

        if(this.progressBar == null) {
            this.progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        }

        if(this.swipeRefreshLayout == null) {
            this.swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
            this.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    if(webView.getUrl() != null && !webView.getUrl().equals(urls.getNewPostUrl())) {
                        webView.reload();
                    }
                    BrowserFragment.this.swipeRefreshLayout.setRefreshing(false);
                }
            });
        }

        if (appSettings.isProxyEnabled()) {
            if (!setProxy(appSettings.getProxyHost(), appSettings.getProxyPort())) {
                AppLog.e(this, "Could not enable Proxy");
                Toast.makeText(getContext(), R.string.toast_set_proxy_failed, Toast.LENGTH_SHORT).show();
            }
        } else if (appSettings.wasProxyEnabled()) {
            resetProxy();
        }

        if(pendingUrl != null) {
            loadUrl(pendingUrl);
            pendingUrl = null;
        }

        webView.setParentActivity(getActivity());
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

    /**
     * Set proxy according to arguments. host must not be "" or null, port must be positive.
     * Return true on success and update appSettings' proxy related values.
     *
     * @param host proxy host (eg. localhost or 127.0.0.1)
     * @param port proxy port (eg. 8118)
     * @return success
     * @throws IllegalArgumentException if arguments do not fit specifications above
     */
    private boolean setProxy(final String host, final int port) {
        AppLog.i(this, "StreamFragment.setProxy()");
        if (host != null && !host.equals("") && port >= 0) {
            AppLog.i(this, "Set proxy to "+host+":"+port);
            //Temporary change thread policy
            AppLog.v(this, "Set temporary ThreadPolicy");
            StrictMode.ThreadPolicy old = StrictMode.getThreadPolicy();
            StrictMode.ThreadPolicy tmp = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(tmp);

            AppLog.v(this, "Apply NetCipher proxy settings");
            NetCipher.setProxy(host, port); //Proxy for HttpsUrlConnections
            try {
                //Proxy for the webview
                AppLog.v(this, "Apply Webkit proxy settings");
                WebkitProxy.setProxy(MainActivity.class.getName(), getContext().getApplicationContext(), null, host, port);
            } catch (Exception e) {
                AppLog.e(this, "Could not apply WebKit proxy settings:\n"+e.toString());
            }
            AppLog.v(this, "Save changes in appSettings");
            appSettings.setProxyEnabled(true);
            appSettings.setProxyWasEnabled(true);

            AppLog.v(this, "Reset old ThreadPolicy");
            StrictMode.setThreadPolicy(old);
            AppLog.i(this, "Success! Reload WebView");
            webView.reload();
            return true;
        } else {
            AppLog.e(this, "Invalid proxy configuration. Host: "+host+" Port: "+port+"\nRefuse to set proxy");
            return false;
        }
    }

    @SuppressWarnings("unused")
    private boolean setProxy() {
        return setProxy(appSettings.getProxyHost(), appSettings.getProxyPort());
    }

    private void resetProxy() {
        AppLog.i(this, "StreamFragment.resetProxy()");
        AppLog.v(this, "write changes to appSettings");
        appSettings.setProxyEnabled(false);
        appSettings.setProxyWasEnabled(false);

        //Temporary change thread policy
        AppLog.v(this, "Set temporary ThreadPolicy");
        StrictMode.ThreadPolicy old = StrictMode.getThreadPolicy();
        StrictMode.ThreadPolicy tmp = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(tmp);

        AppLog.v(this, "clear NetCipher proxy");
        NetCipher.clearProxy();
        try {
            AppLog.v(this, "clear WebKit proxy");
            WebkitProxy.resetProxy(MainActivity.class.getName(), getContext());
        } catch (Exception e) {
            AppLog.e(this, "Could not clear WebKit proxy:\n"+e.toString());
        }
        AppLog.v(this, "Reset old ThreadPolicy");
        StrictMode.setThreadPolicy(old);

        //Restart app
        AppLog.i(this, "Success! Restart app due to proxy reset");
        Intent restartActivity = new Intent(getContext(), MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getContext(), 12374, restartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager mgr = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, pendingIntent);
        System.exit(0);
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
            if(!fileSaveDirectory.mkdirs()) {
                AppLog.w(this, "Could not mkdir "+fileSaveDirectory.getAbsolutePath());
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
        if(webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        return false;
    }

    public void loadUrl(String url) {
        if(getWebView() != null) {
            AppLog.v(this, "loadUrl(): load "+url);
            getWebView().loadUrlNew(url);
        } else {
            AppLog.v(this, "loadUrl(): WebView null: Set pending url to "+url);
            pendingUrl = url;
        }
    }

    public String getUrl() {
        if(getWebView() != null) {
            return getWebView().getUrl();
        } else {
            return pendingUrl;
        }
    }

    public void reloadUrl() {
        AppLog.v(this, "reloadUrl()");
        if(getWebView() != null) {
            getWebView().reload();
        }
    }

    public ContextMenuWebView getWebView() {
        return this.webView;
    }
}
