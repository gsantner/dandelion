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

import android.annotation.TargetApi;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.LocalBroadcastManager;
import android.webkit.CookieManager;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.github.dfa.diaspora_android.App;
import com.github.dfa.diaspora_android.activity.MainActivity;
import com.github.dfa.diaspora_android.data.DiasporaPodList;
import com.github.dfa.diaspora_android.util.AppSettings;

import net.gsantner.opoc.util.AdBlock;

public class CustomWebViewClient extends WebViewClient {
    private final App app;
    private String lastLoadUrl = "";
    private boolean isAdBlockEnabled = false;
    AppSettings appSettings = AppSettings.get();

    public CustomWebViewClient(App app, WebView webView) {
        this.app = app;
        isAdBlockEnabled = AppSettings.get().isAdBlockEnabled();
    }

    //Open non-diaspora links in customtab/external browser
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        DiasporaPodList.DiasporaPod pod = AppSettings.get().getPod();
        String host = null;
        if (pod != null && pod.getPodUrl() != null && pod.getPodUrl().getHost() != null) {
            host = pod.getPodUrl().getHost();
        }

        if (url.startsWith("https://dia.so/")
                || (host != null && (url.startsWith("https://" + host)
                || url.startsWith("http://" + host)))) {
            return false;
        }//make youtube links open external-->never customtab
        else if (appSettings.isOpenYoutubeExternalEnabled() && (url.startsWith("https://youtube.com/") || url.startsWith("https://www.youtube.com/") || url.startsWith("https://m.youtube.com/") || url.startsWith("https://youtu.be/"))) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            view.getContext().startActivity(intent);
            return true;
        } else {
            Intent i = new Intent(MainActivity.ACTION_OPEN_EXTERNAL_URL);
            i.putExtra(MainActivity.EXTRA_URL, url);
            LocalBroadcastManager.getInstance(app.getApplicationContext()).sendBroadcast(i);
            return true;
        }
    }

    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);

        final CookieManager cookieManager = app.getCookieManager();
        String cookies = cookieManager.getCookie(url);
        DiasporaPodList.DiasporaPod pod = AppSettings.get().getPod();

        if (cookies != null) {
            cookieManager.setCookie(url, cookies);
            if (pod != null && pod.getPodUrl() != null) {
                cookieManager.setCookie(pod.getPodUrl().getBaseUrl(), cookies);
                cookieManager.setCookie(".dia.so", "pod=" + pod.getPodUrl().getHost());
            }
            for (String c : cookies.split(";")) {
                //AppLog.d(this, "Cookie: " + c.split("=")[0] + " Value:" + c.split("=")[1]);
            }
            //new ProfileFetchTask(app).execute();
        }
    }


    @SuppressWarnings("deprecation")
    @TargetApi(Build.VERSION_CODES.KITKAT_WATCH)
    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
        if (isAdBlockEnabled && AdBlock.getInstance().isAdHost(url)) {
            return AdBlock.createEmptyResponse();
        }
        return super.shouldInterceptRequest(view, url);
    }

}
