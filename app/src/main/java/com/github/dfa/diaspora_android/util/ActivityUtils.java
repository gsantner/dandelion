package com.github.dfa.diaspora_android.util;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.view.View;

import com.github.dfa.diaspora_android.BuildConfig;
import com.github.dfa.diaspora_android.R;
import com.github.dfa.diaspora_android.web.WebHelper;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

@SuppressWarnings({"WeakerAccess", "unused", "SameParameterValue"})
public class ActivityUtils extends net.gsantner.opoc.util.ActivityUtils {
    public ActivityUtils(Activity activity) {
        super(activity);
    }


    public static ActivityUtils get(Activity activity) {
        return new ActivityUtils(activity);
    }

    public File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("dd-MM-yy_HH-mm", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        AppLog.d(ActivityUtils.class, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath());
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        return new File(
                imageFileName +  /* prefix */
                        ".jpg",         /* suffix */
                storageDir.getAbsolutePath()      /* directory */
        );
    }

    /**
     * Show Information if user is offline, returns true if is not connected to internet
     *
     * @param anchor A view anchor
     */
    public boolean showInfoIfUserNotConnectedToInternet(View anchor) {
        boolean isOnline = WebHelper.isOnline(_context);
        if (!isOnline) {
            showSnackBar(R.string.sorry_need_to_be_connected_to_internet, true);
        }
        return !isOnline;
    }

    public void logBundle(Bundle savedInstanceState, String k) {
        if (savedInstanceState != null) {
            for (String key : savedInstanceState.keySet()) {
                AppLog.d("Bundle", key + " is a key in the bundle " + k);
                Object bun = savedInstanceState.get(key);
                if (bun != null) {
                    if (bun instanceof Bundle) {
                        logBundle((Bundle) bun, k + "." + key);
                    } else if (bun instanceof byte[]) {
                        AppLog.d("Bundle", "Key: " + k + "." + key + ": " + Arrays.toString((byte[]) bun));
                    } else {
                        AppLog.d("Bundle", "Key: " + k + "." + key + ": " + bun.toString());
                    }
                }
            }
        }
    }

    /**
     * This method creates file sharing uri by using FileProvider
     *
     * @return
     */
    public static Uri getFileSharingUri(Context context, File file) {
        return FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID, file);
    }
}
