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

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.github.dfa.diaspora_android.util.AppLog;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.net.ssl.HttpsURLConnection;

import info.guardianproject.netcipher.NetCipher;

/**
 * Task that can be used to download images from URLs and store them in storage
 * Created by gsantner (https://gsantner.github.io/) on 24.03.16.
 */
public class ImageDownloadTask extends AsyncTask<String, Void, Bitmap> {
    private final ImageView imageView;
    private String savePath;

    /**
     * Download image from URL
     *
     * @param imageView ImageView to set image to (null = don't set)
     * @param savePath  Save image to file (null = don't save)
     */
    public ImageDownloadTask(@Nullable ImageView imageView, @Nullable String savePath) {
        this.imageView = imageView;
        this.savePath = savePath;
    }

    protected Bitmap doInBackground(String... urls) {
        String url = urls[0];
        Bitmap bitmap = null;
        FileOutputStream out = null;
        InputStream inStream;
        HttpsURLConnection connection;
        try {
            connection = NetCipher.getHttpsURLConnection(url);
            inStream = connection.getInputStream();
            bitmap = BitmapFactory.decodeStream(inStream);

            // Save to file if not null
            if (savePath != null) {
                out = new FileOutputStream(savePath);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            }

            try {
                inStream.close();
            } catch (IOException e) {/*Nothing*/}

            connection.disconnect();

        } catch (Exception e) {
            AppLog.e(this, e.getMessage());
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException ignored) {
            }
        }
        return bitmap;
    }

    protected void onPostExecute(Bitmap result) {
        // Display on imageview if not null
        if (imageView != null) {
            imageView.setImageBitmap(result);
        }
    }
}
