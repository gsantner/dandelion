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

    public static final String defaultHost = "127.0.0.1";
    public static final int defaultPort = 8118;
    private Intent lastStatus;
    private Context lastContext;
    private static MainActivity mainActivity;
    private AppSettings appSettings;
    private static boolean proxySet = false;

    public OrbotStatusReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if(OrbotHelper.ACTION_STATUS.equals(intent.getAction())) {
            lastStatus = intent;
            lastContext = context;
            if(appSettings == null) appSettings = new AppSettings(context.getApplicationContext());
            String orbotStatus = intent.getExtras().getString(OrbotHelper.EXTRA_STATUS);
            if(appSettings.isProxyOrbot()) {
                if (orbotStatus.equals(OrbotHelper.STATUS_ON) && !proxySet) {
                    setProxy(lastContext, lastStatus);
                } else if(orbotStatus.equals(OrbotHelper.STATUS_OFF)) {
                    Log.d(App.TAG, "Warning: Orbot reports status off.");
                    OrbotHelper.requestStartTor(context.getApplicationContext());
                } else if(orbotStatus.equals(OrbotHelper.STATUS_STARTS_DISABLED)) {
                    Log.d(App.TAG, "Warning: Orbot has background starts disabled.");
                    if(mainActivity != null) mainActivity.requestOrbotStart(true);
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
                setProxy(context, defaultHost, defaultPort);
            }
        } else {
            Log.e(App.TAG, "OrbotStatusReceiver: lastStatus intent is null. Cannot set Proxy.");
        }
    }

    public static void setProxy(Context context, String host, int port) {
        try {
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
        try {
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

    public static void setMainActivity(MainActivity main) {
        mainActivity = main;
    }
}
