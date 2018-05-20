package com.github.dfa.diaspora_android.util;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;

import com.github.dfa.diaspora_android.App;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

@SuppressWarnings({"WeakerAccess", "unused", "SameParameterValue"})
public class ContextUtils extends net.gsantner.opoc.util.ContextUtils {
    protected ContextUtils(Context context) {
        super(context);
    }


    public static ContextUtils get() {
        return new ContextUtils(App.get());
    }

    public File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("dd-MM-yy_HH-mm", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        AppLog.d(ContextUtils.class, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath());
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        return new File(
                imageFileName +  /* prefix */
                        ".jpg",         /* suffix */
                storageDir.getAbsolutePath()      /* directory */
        );
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
}
