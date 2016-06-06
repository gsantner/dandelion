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

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.github.dfa.diaspora_android.App;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import info.guardianproject.netcipher.NetCipher;

public class GetPodsService extends Service {
    public static final String MESSAGE_PODS_RECEIVED = "com.github.dfa.diaspora.podsreceived";
    private static final String TAG = App.TAG;

    public GetPodsService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        getPods();
        return super.onStartCommand(intent, flags, startId);
    }

    private void getPods() {
        /*
         * Most of the code in this AsyncTask is from the file getPodlistTask.java
         * from the app "Diaspora Webclient".
         * A few modifications and adaptations were made by me.
         * Source:
         * https://github.com/voidcode/Diaspora-Webclient/blob/master/src/com/voidcode/diasporawebclient/getPodlistTask.java
         * Thanks to Terkel Sørensen ; License : GPLv3
         */
        AsyncTask<Void, Void, String[]> getPodsAsync = new AsyncTask<Void, Void, String[]>() {
            @Override
            protected String[] doInBackground(Void... params) {

                // TODO: Update deprecated code

                StringBuilder builder = new StringBuilder();
                //HttpClient client = new DefaultHttpClient();
                List<String> list = null;
                try {
                    HttpsURLConnection connection = NetCipher.getHttpsURLConnection("https://podupti.me/api.php?key=4r45tg&format=json");
                    int statusCode = connection.getResponseCode();
                    if (statusCode == 200) {
                        InputStream content = connection.getInputStream();
                        BufferedReader reader = new BufferedReader(
                                new InputStreamReader(content));
                        String line;
                        while ((line = reader.readLine()) != null) {
                            builder.append(line);
                        }
                    } else {
                        Log.e(TAG, "Failed to download list of pods");
                    }
                } catch (IOException e) {
                    //TODO handle json buggy feed
                    e.printStackTrace();
                }
                //Parse the JSON Data
                try {
                    JSONObject jsonObjectAll = new JSONObject(builder.toString());
                    JSONArray jsonArrayAll = jsonObjectAll.getJSONArray("pods");
                    Log.d(TAG, "Number of entries " + jsonArrayAll.length());
                    list = new ArrayList<>();
                    for (int i = 0; i < jsonArrayAll.length(); i++) {
                        JSONObject jo = jsonArrayAll.getJSONObject(i);
                        if (jo.getString("secure").equals("true"))
                            list.add(jo.getString("domain"));
                    }

                } catch (Exception e) {
                    //TODO Handle Parsing errors here
                    e.printStackTrace();
                }
                if (list != null)
                    return list.toArray(new String[list.size()]);
                else
                    return null;
            }

            @Override
            protected void onPostExecute(String[] pods) {
                Intent broadcastIntent = new Intent(MESSAGE_PODS_RECEIVED);
                broadcastIntent.putExtra("pods", pods != null ? pods : new String[0]);
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(broadcastIntent);
                stopSelf();
            }
        };
        getPodsAsync.execute();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

}
