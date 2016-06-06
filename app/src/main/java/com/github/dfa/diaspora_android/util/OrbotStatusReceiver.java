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

package com.github.dfa.diaspora_android.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.github.dfa.diaspora_android.App;
import com.github.dfa.diaspora_android.activity.MainActivity;
import com.github.dfa.diaspora_android.data.AppSettings;

import info.guardianproject.netcipher.NetCipher;
import info.guardianproject.netcipher.proxy.OrbotHelper;
import info.guardianproject.netcipher.web.WebkitProxy;

/**
 * BroadcastReceiver that handles Orbot status intents and sets the proxy.
 * Created by vanitas on 06.06.16.
 */
public class OrbotStatusReceiver extends BroadcastReceiver {

    public static final String EXTRA_HTTP_HOST = "org.torproject.android.intent.extra.HTTP_PROXY_HOST";
    public static final String EXTRA_HTTP_PORT = "org.torproject.android.intent.extra.HTTP_PROXY_PORT";
    public static final String REQUEST_SHOW_ORBOT = "com.github.dfa.diaspora.request_show_orbot";
    public static final String EXTRA_BACKGROUND_STARTS_DISABLED = "com.github.dfa.diaspora.request_show_orbot.BG_START_DISABLED";
    public static final String DEFAULT_HOST = "127.0.0.1";
    public static final int DEFAULT_PORT = 8118;

    private static String host = "";
    private static int port = 0;
    private static boolean promptOnBackgroundStart = true;
    private AppSettings appSettings;
    private static boolean proxySet = false;

    public OrbotStatusReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if(OrbotHelper.ACTION_STATUS.equals(intent.getAction())) {
            if(appSettings == null) appSettings = new AppSettings(context.getApplicationContext());
            String orbotStatus = intent.getExtras().getString(OrbotHelper.EXTRA_STATUS);
            if(appSettings.isProxyOrbot() && orbotStatus != null) {
                //Status on
                if (orbotStatus.equals(OrbotHelper.STATUS_ON)) {
                    setProxy(context, intent);
                    return;
                }
                //Status off
                if(orbotStatus.equals(OrbotHelper.STATUS_OFF)) {
                    Log.d(App.TAG, "Warning: Orbot reports status off.");
                    Intent orbotOffIntent = new Intent(REQUEST_SHOW_ORBOT);
                    orbotOffIntent.putExtra(EXTRA_BACKGROUND_STARTS_DISABLED, false);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(orbotOffIntent);
                    return;
                }
                //Background starts disabled
                if(orbotStatus.equals(OrbotHelper.STATUS_STARTS_DISABLED ) && promptOnBackgroundStart) {
                    promptOnBackgroundStart = false;
                    Log.d(App.TAG, "Warning: Orbot has background starts disabled.");
                    Intent backgroundStartsDisabledIntent = new Intent(REQUEST_SHOW_ORBOT);
                    backgroundStartsDisabledIntent.putExtra(EXTRA_BACKGROUND_STARTS_DISABLED, true);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(backgroundStartsDisabledIntent);
                }
            }
        } else {
            Log.e(App.TAG, "Warning: Intents action "+intent.getAction()+ " does not equal "+OrbotHelper.ACTION_STATUS);
        }
    }

    public static void setProxy(Context context, Intent intent) {
        if(intent != null) {
            String status = intent.getStringExtra(OrbotHelper.EXTRA_STATUS);
            if(status.equals(OrbotHelper.STATUS_ON)) {
                String nHost = intent.getExtras().getString(EXTRA_HTTP_HOST, null);
                int nPort = intent.getIntExtra(EXTRA_HTTP_PORT, -1);
                //Got no values from intent
                if((nHost == null || nPort == -1)) {
                    if(host.equals("") || port == 0) {
                        setProxy(context, DEFAULT_HOST, DEFAULT_PORT);
                    }
                } else {
                    setProxy(context, nHost, nPort);
                }
            }
        } else {
            setProxy(context, DEFAULT_HOST, DEFAULT_PORT);
        }
    }

    public static void setProxy(Context context, String host, int port) {
        try {
            OrbotStatusReceiver.host = host;
            OrbotStatusReceiver.port = port;
            NetCipher.setProxy(host, port);
            WebkitProxy.setProxy(MainActivity.class.getName(), context.getApplicationContext(), null, host, port);
            Log.d(App.TAG, "Proxy successfully set.");
            proxySet = true;
        } catch(Exception e) {
            Log.e(App.TAG, "setProxy failed: ");
            e.printStackTrace();
        }
    }

    public static void resetProxy(Context context) {
        promptOnBackgroundStart = true;
        try {
            OrbotStatusReceiver.host = "";
            OrbotStatusReceiver.port = 0;
            NetCipher.clearProxy();
            WebkitProxy.resetProxy(MainActivity.class.getName(), context.getApplicationContext());
        } catch (Exception e) {
            //Fails in any case on android 6. Ignore it and restart application.
        }
        proxySet = false;
        //Restart application
        Intent restartActivity = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 12374, restartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager mgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, pendingIntent);
        System.exit(0);
    }

    public static boolean isProxySet() {
        return proxySet;
    }

}
