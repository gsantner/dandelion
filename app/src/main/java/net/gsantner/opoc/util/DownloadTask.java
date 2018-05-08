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
package net.gsantner.opoc.util;

import android.os.AsyncTask;
import android.support.annotation.Nullable;

import java.io.File;
import java.io.IOException;

import javax.net.ssl.HttpsURLConnection;

import info.guardianproject.netcipher.NetCipher;

public class DownloadTask extends AsyncTask<String, Void, Boolean> {
    private File _targetFile;
    private Callback.a2<Boolean, File> _callback;

    public DownloadTask(File targetFile, @Nullable Callback.a2<Boolean, File> callback) {
        _targetFile = targetFile;
        _callback = callback;
    }

    protected Boolean doInBackground(String... urls) {
        if (urls != null && urls.length > 0 && urls[0] != null) {
            try {
                HttpsURLConnection connection = NetCipher.getHttpsURLConnection(urls[0]);
                return NetworkUtils.downloadFile(null, _targetFile, connection, null);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    protected void onPostExecute(Boolean result) {
        if (_callback != null) {
            _callback.callback(result, _targetFile);
        }
    }
}
