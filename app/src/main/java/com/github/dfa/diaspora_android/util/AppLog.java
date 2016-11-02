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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by gregor on 18.09.16.
 */
public class AppLog {
    private final static String APP_TAG = "d*";
    private static boolean loggingEnabled = true;
    private static boolean loggingSpamEnabled = false;

    public static boolean isLoggingEnabled() {
        return loggingEnabled;
    }

    public static void setLoggingEnabled(boolean loggingEnabled) {
        AppLog.loggingEnabled = loggingEnabled;
    }

    public static boolean isLoggingSpamEnabled() {
        return loggingSpamEnabled;
    }

    public static void setLoggingSpamEnabled(boolean loggingSpamEnabled) {
        AppLog.loggingSpamEnabled = loggingSpamEnabled;
    }

    private static String getLogPrefix(Object source) {
        return APP_TAG + "-" + source.getClass().getCanonicalName();
    }

    /*
     *
     * LOGGER METHODS
     *
     */
    public static void v(Object source, String _text) {
        if (isLoggingEnabled()) {
            Log.v(source != null? getLogPrefix(source) : "null", _text);
        }
    }

    public static void i(Object source, String _text) {
        if (isLoggingEnabled()) {
            Log.i(source != null? getLogPrefix(source) : "null", _text);
        }
    }

    public static void d(Object source, String _text) {
        if (isLoggingEnabled()) {
            Log.d(source != null? getLogPrefix(source) : "null", _text);
        }
    }

    public static void e(Object source, String _text) {
        if (isLoggingEnabled()) {
            Log.e(source != null? getLogPrefix(source) : "null", _text);
        }
    }

    public static void w(Object source, String _text) {
        if (isLoggingEnabled()) {
            Log.w(source != null? getLogPrefix(source) : "null", _text);
        }
    }

    public static void spam(Object source, String _text) {
        if (isLoggingEnabled() && isLoggingSpamEnabled()) {
            Log.v(source != null? getLogPrefix(source) : "null", _text);
        }
    }


    /**
     * Class that saves logs eg. for later debugging.
     * TODO: Differentiate log types (error/debug/info...)
     */
    public static class Log extends Observable {
        public static final int MAX_BUFFER_SIZE = 100;

        public static Log instance;
        private AppSettings appSettings;
        private SimpleDateFormat dateFormat;
        private ArrayList<String> logBuffer;
        private ArrayList<Observer> observers;

        private Log() {
            this(null);
        }

        private Log(AppSettings appSettings) {
            if (appSettings != null) {
                //TODO: Store/Restore logBuffer between app starts
                logBuffer = new ArrayList<>();
            } else {
                logBuffer = new ArrayList<>();
            }
            dateFormat = new SimpleDateFormat("HH:mm:ss");
            observers = new ArrayList<>();
        }

        public static Log getInstance() {
            if (instance == null) instance = new Log();
            return instance;
        }

        public static Log getInstance(AppSettings appSettings) {
            if (instance == null) instance = new Log(appSettings);
            return instance;
        }

        private static String time() {
            return getInstance().dateFormat.format(new Date()) + ": ";
        }

        public static void d(String tag, String msg) {
            Log l = getInstance();
            android.util.Log.d(tag, msg);
            l.addLogEntry(msg);
            l.notifyLogBufferChanged();
        }

        public static void e(String tag, String msg) {
            Log l = getInstance();
            android.util.Log.e(tag, msg);
            l.addLogEntry(msg);
            l.notifyLogBufferChanged();
        }

        public static void i(String tag, String msg) {
            Log l = getInstance();
            android.util.Log.i(tag, msg);
            l.addLogEntry(msg);
            l.notifyLogBufferChanged();
        }

        public static void v(String tag, String msg) {
            Log l = getInstance();
            android.util.Log.v(tag, msg);
            l.addLogEntry(msg);
            l.notifyLogBufferChanged();
        }

        public static void w(String tag, String msg) {
            Log l = getInstance();
            android.util.Log.w(tag, msg);
            l.addLogEntry(msg);
            l.notifyLogBufferChanged();
        }

        public static void wtf(String tag, String msg) {
            Log l = getInstance();
            android.util.Log.wtf(tag, msg);
            l.addLogEntry(msg);
            l.notifyLogBufferChanged();
        }

        public synchronized static ArrayList<String> getLogBufferArray() {
            return getInstance().logBuffer;
        }

        public synchronized static String getLogBuffer() {
            String out = "";
            for (String s : getInstance().logBuffer) {
                out = out + s + "\n";
            }
            return out;
        }

        private void notifyLogBufferChanged() {
            if (observers == null) return;
            for (Observer o : observers) {
                if (o != null) {
                    o.update(this, null);
                }
            }
        }

        private synchronized void addLogEntry(String msg) {
            logBuffer.add(time() + msg);
            while (logBuffer.size() > MAX_BUFFER_SIZE) {
                logBuffer.remove(0);
            }
        }

        public static void addLogObserver(Observer observer) {
            getInstance().observers.add(observer);
        }

        public static void removeLogObserver(Observer o) {
            getInstance().observers.remove(o);
        }
    }
}
