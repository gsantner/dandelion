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
package com.github.dfa.diaspora_android.task;

import android.content.Context;
import android.os.AsyncTask;
import android.webkit.CookieManager;

import com.github.dfa.diaspora_android.App;
import com.github.dfa.diaspora_android.util.AppLog;
import com.github.dfa.diaspora_android.util.DiasporaUrlHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import info.guardianproject.netcipher.NetCipher;

/**
 * AsyncTask to fetch a users profile
 */
public class StatisticsFetchTask extends AsyncTask<Void, Void, Void> {
    // Code for getting the profile async without any UI/WebView
    // TODO: This is an early version,needs to be converted to Service

    private final App app;
    private final Context context;
    private final DiasporaUrlHelper urls;

    public StatisticsFetchTask(final App app) {
        this.context = app.getApplicationContext();
        this.app = app;
        this.urls = new DiasporaUrlHelper(app.getSettings());
    }


    @Override
    protected Void doInBackground(Void... params) {
        final CookieManager cookieManager = app.getCookieManager();
        String cookies = cookieManager.getCookie(urls.getPodUrl());

        HttpsURLConnection connection;
        InputStream inStream;
        try {
            URL url = new URL(urls.getStatisticsUrl());
            connection = NetCipher.getHttpsURLConnection(url);
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(15000);
            connection.setRequestMethod("GET");
            if (cookies != null) {
                connection.setRequestProperty("Cookie", cookies);
            }
            connection.connect();

            inStream = connection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(inStream));
            String line;
            while ((line = br.readLine()) != null) {
                AppLog.d(this, "STATS: " + line);
            }

            try {
                br.close();
                inStream.close();
            } catch (IOException e) {/*Nothing*/}

            connection.disconnect();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
