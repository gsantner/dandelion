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
package com.github.dfa.diaspora_android.service;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;

import com.github.dfa.diaspora_android.data.DiasporaPodList;
import com.github.dfa.diaspora_android.util.AppLog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.net.ssl.HttpsURLConnection;

import info.guardianproject.netcipher.NetCipher;

public class FetchPodsService extends Service {
    public static final String MESSAGE_PODS_RECEIVED = "com.github.dfa.diaspora.podsreceived";
    public static final String EXTRA_PODLIST = "pods";

    public FetchPodsService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new GetPodsTask(this).execute();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}

class GetPodsTask extends AsyncTask<Void, Void, DiasporaPodList> {
    private static final String PODDY_PODLIST_URL = "https://raw.githubusercontent.com/gsantner/dandelion/master/app/src/main/res/raw/podlist.json";

    private final Service service;

    GetPodsTask(Service service) {
        this.service = service;
    }

    @Override
    protected DiasporaPodList doInBackground(Void... params) {
        StringBuilder sb = new StringBuilder();
        BufferedReader br = null;
        try {
            HttpsURLConnection con = NetCipher.getHttpsURLConnection(PODDY_PODLIST_URL);
            if (con.getResponseCode() == HttpsURLConnection.HTTP_OK) {
                br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }

                // Parse JSON & return pod list
                JSONObject json = new JSONObject(sb.toString());
                return new DiasporaPodList().fromJson(json);
            } else {
                AppLog.e(this, "Failed to download list of pods");
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException ignored) {
                }
            }
        }

        // Could not fetch list of pods :(
        return new DiasporaPodList();
    }

    @Override
    protected void onPostExecute(DiasporaPodList pods) {
        if (pods == null) {
            pods = new DiasporaPodList();
        }
        Intent broadcastIntent = new Intent(FetchPodsService.MESSAGE_PODS_RECEIVED);
        broadcastIntent.putExtra(FetchPodsService.EXTRA_PODLIST, pods);
        LocalBroadcastManager.getInstance(service.getApplicationContext()).sendBroadcast(broadcastIntent);
        service.stopSelf();
    }
}