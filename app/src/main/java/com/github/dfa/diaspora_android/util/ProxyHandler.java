package com.github.dfa.diaspora_android.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.github.dfa.diaspora_android.App;
import com.github.dfa.diaspora_android.activity.MainActivity;
import com.github.dfa.diaspora_android.data.AppSettings;

import info.guardianproject.netcipher.NetCipher;
import info.guardianproject.netcipher.proxy.OrbotHelper;
import info.guardianproject.netcipher.web.WebkitProxy;

/**
 * Handle proxy configurations
 * In this particular case integration of Orbot as a proxy for the tor network has been done,
 * but other proxies can easily and similarly be added as well.
 * Created by vanitas on 05.06.16.
 */
public class ProxyHandler {
    //TODO: Remove when NetCipher > 1.2.1 releases
    public final static String EXTRA_PROXY_PORT_HTTP = "org.torproject.android.intent.extra.HTTP_PROXY_PORT";
    //Proxy types
    public static final int NO_PROXY = 0, ORBOT_PROXY = 1;
    private int activeProxy = NO_PROXY;

    private static ProxyHandler instance;
    private MainActivity mainActivity;
    private AppSettings appSettings;
    private OrbotReceiver orbotReceiver;


    private ProxyHandler(MainActivity main, AppSettings settings) {
        orbotReceiver = new OrbotReceiver();
        mainActivity = main;
        appSettings = settings;
    }

    public static ProxyHandler getInstance(MainActivity main, AppSettings settings) {
        if(instance == null) instance = new ProxyHandler(main, settings);
        return instance;
    }

    public void registerOrbotReceiver(Context context) {
        if(!orbotReceiver.isRegistered()) {
            context.registerReceiver(orbotReceiver, new IntentFilter(OrbotHelper.ACTION_STATUS));
            orbotReceiver.setRegistered(true);
        }
        else throw new IllegalStateException("OrbotReceiver is already registered.");
    }

    public void unregisterOrbotReceiver(Context context) {
        if(orbotReceiver.isRegistered()) {
            context.unregisterReceiver(orbotReceiver);
            orbotReceiver.setRegistered(false);
        }
        else throw new IllegalStateException("OrbotReceiver was not registered and can therefore not be unregistered.");
    }

    public void setProxy(Context context, String host, int port, int proxyType) {
        if(proxyType == NO_PROXY) {
            try {
                NetCipher.clearProxy();
                WebkitProxy.resetProxy(MainActivity.class.getName(), context);
                activeProxy = proxyType;
            }
            catch (Exception e) {
                Log.e(App.TAG, "ProxyHandler caught exception "+e.getClass().getName()+" while resetting proxy.");
            }
            restartApplication(context);
            return;
        }

        if(proxyType == ORBOT_PROXY) {
            try {
                NetCipher.setProxy(host, port);
                WebkitProxy.setProxy(MainActivity.class.getName(), context.getApplicationContext(), null, host, port);
                activeProxy = proxyType;
            } catch (Exception e) {
                Log.d(App.TAG, "ProxyHandler caught exception " + e.getClass().getName() + " while setting proxy.");
                e.printStackTrace();
            }
        }

        //Add further proxies here
    }

    private static void restartApplication(Context context) {
        Intent mStartActivity = new Intent(context, MainActivity.class);
        PendingIntent mPendingIntent = PendingIntent.getActivity(context, 12374, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager mgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
        System.exit(0);
    }

    private static class OrbotReceiver extends BroadcastReceiver {
        private boolean registered;

        private boolean orbotRunning = false;
        private int proxyPort = -1;
        private String proxyHost = "127.0.0.1";


        @Override
        public void onReceive(Context context, Intent intent) {
            if(OrbotHelper.ACTION_STATUS.equals(intent.getAction())) {

                if(OrbotHelper.STATUS_ON.equals(intent.getStringExtra(OrbotHelper.EXTRA_STATUS))) {
                    proxyPort = intent.getIntExtra(EXTRA_PROXY_PORT_HTTP, -1);
                    if(instance.appSettings.isProxyOrbot()) {
                        instance.setProxy(context, proxyHost, proxyPort, ORBOT_PROXY);
                    }
                    orbotRunning = true;
                }

                if(OrbotHelper.STATUS_OFF.equals(intent.getStringExtra(OrbotHelper.EXTRA_STATUS))) {
                    instance.mainActivity.requestOrbotStart(false);
                }

                if(OrbotHelper.STATUS_STARTS_DISABLED.equals(intent.getStringExtra(OrbotHelper.EXTRA_STATUS))) {
                    instance.mainActivity.requestOrbotStart(true);
                }
            }
        }

        public boolean isRegistered() {
            return registered;
        }

        public void setRegistered(boolean r) {
            registered = r;
        }
    }

    public int getActiveProxy() {
        return activeProxy;
    }
}
