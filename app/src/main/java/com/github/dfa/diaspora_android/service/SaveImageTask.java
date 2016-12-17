package com.github.dfa.diaspora_android.service;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Looper;
import android.os.StrictMode;
import android.widget.Toast;

import com.github.dfa.diaspora_android.R;
import com.github.dfa.diaspora_android.util.AppLog;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Created by vanitas on 06.11.16.
 */

public class SaveImageTask extends AsyncTask<String, Void, String> {

    protected Context context;

    public void setContext(Context context) {
        this.context = context;
    }

    protected String doInBackground(String... urls) {
        String url = urls[0];
            if (url != null) {
                Uri source = Uri.parse(url);
                DownloadManager.Request request = new DownloadManager.Request(source);
                URL sourceUrl;
                InputStream is;
                byte[] a = new byte[8];
                String extension = ".png";
                try {
                    sourceUrl = new URL(source.toString());
                    is = sourceUrl.openStream();
                    is.read(a);
                    is.close();
                    AppLog.d(this, "Array: " + new String(a));
                    //JPG
                    if (new String(a).startsWith(new String(new byte[]{-1, -40}))) {
                        AppLog.d(this, "is jpg");
                        extension = ".jpg";
                    } else
                        //GIF
                        if (new String(a).startsWith("GIF")) {
                            AppLog.d(this, "is gif");
                            extension = ".gif";
                        } else {
                            AppLog.d(this, "is SPARTAAAA! (GIF)");
                        }
                } catch (IOException ignored) {
                }
                File destinationFile = new File(Environment.getExternalStorageDirectory() + "/Pictures/Diaspora/"
                        + System.currentTimeMillis() + extension);
                request.setDestinationUri(Uri.fromFile(destinationFile));
                ((DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE)).enqueue(request);
                return destinationFile.getAbsolutePath();
            }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        Toast.makeText(context, context.getString(R.string.share__toast_saved_image_to_location)+" "+s, Toast.LENGTH_LONG).show();
    }
}