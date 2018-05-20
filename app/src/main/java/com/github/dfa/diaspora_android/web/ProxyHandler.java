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
import android.os.StrictMode;
import android.webkit.WebView;

import com.github.dfa.diaspora_android.activity.MainActivity;
import com.github.dfa.diaspora_android.util.AppLog;
import com.github.dfa.diaspora_android.util.AppSettings;

import java.util.ArrayList;

import info.guardianproject.netcipher.NetCipher;
import info.guardianproject.netcipher.webkit.WebkitProxy;

/**
 * Proxy Handler that applies proxy settings of the app to webviews etc.
 * Created by vanitas on 10.10.16.
 */

public class ProxyHandler {
    private static ProxyHandler instance;
    private ArrayList<WebView> webViews;


    private ProxyHandler() {
        /* Singleton, yo? */
        this.webViews = new ArrayList<>();
    }

    public static ProxyHandler getInstance() {
        if (instance == null) {
            instance = new ProxyHandler();
        }
        return instance;
    }

    public void updateProxySettings(Context context) {
        AppLog.d(this, "UpdateProxySettings()");
        AppSettings appSettings = AppSettings.get();
        StrictMode.ThreadPolicy old = StrictMode.getThreadPolicy();
        StrictMode.ThreadPolicy tmp = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(tmp);
        if (appSettings.isProxyHttpEnabled()) {
            //Update NetCipher
            NetCipher.setProxy(appSettings.getProxyHttpHost(), appSettings.getProxyHttpPort());
            //Update webviews
            for (WebView wv : webViews) {
                if (wv != null) {
                    try {
                        WebkitProxy.setProxy(MainActivity.class.getName(), context.getApplicationContext(), wv, appSettings.getProxyHttpHost(), appSettings.getProxyHttpPort());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        StrictMode.setThreadPolicy(old);
    }

    public void addWebView(WebView wv) {
        AppLog.d(this, "AddWebView");
        if (wv != null && !webViews.contains(wv)) {
            webViews.add(wv);
            updateWebViewProxySettings(wv, wv.getContext());
        }
    }

    private void updateWebViewProxySettings(WebView wv, Context context) {
        AppLog.d(this, "UpdateWebViewProxySettings()");
        AppSettings appSettings = AppSettings.get();
        StrictMode.ThreadPolicy old = StrictMode.getThreadPolicy();
        StrictMode.ThreadPolicy tmp = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(tmp);
        if (appSettings.isProxyHttpEnabled()) {
            if (wv != null) {
                try {
                    WebkitProxy.setProxy(MainActivity.class.getName(), context.getApplicationContext(), wv, appSettings.getProxyHttpHost(), appSettings.getProxyHttpPort());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        StrictMode.setThreadPolicy(old);
    }

    public static class ProxySettings {
        private final boolean enabled;
        private final String host;
        private final int port;

        public ProxySettings(boolean enabled, String host, int port) {
            this.enabled = enabled;
            this.host = host;
            this.port = port;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public String getHost() {
            return host;
        }

        public int getPort() {
            return port;
        }

        @Override
        public boolean equals(Object other) {
            return (other instanceof ProxySettings) &&
                    enabled == ((ProxySettings) other).isEnabled() &&
                    host.equals(((ProxySettings) other).getHost()) &&
                    port == ((ProxySettings) other).getPort();
        }
    }
}
