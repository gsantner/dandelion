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
package com.github.dfa.diaspora_android.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.view.View;

import com.github.dfa.diaspora_android.App;
import com.github.dfa.diaspora_android.R;
import com.github.dfa.diaspora_android.web.WebHelper;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

public class Helpers {

    public static void animateToActivity(Activity from, Class to, boolean finishFromActivity) {
        Intent intent = new Intent(from, to);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        from.startActivity(intent);
        from.overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        if (finishFromActivity) {
            from.finish();
        }
    }

    public static int getColorFromRessource(Context context, int ressourceId) {
        Resources res = context.getResources();
        if (Build.VERSION.SDK_INT >= 23) {
            return res.getColor(ressourceId, context.getTheme());
        } else {
            return res.getColor(ressourceId);
        }
    }

    public static void loadUrlInExternalBrowser(Context context, String url) {
        try {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            context.startActivity(browserIntent);
        } catch (Exception ignored) {
        }
    }

    public static File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("dd-MM-yy_HH-mm", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        AppLog.d(Helpers.class, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath());
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        return new File(
                imageFileName +  /* prefix */
                        ".jpg",         /* suffix */
                storageDir.getAbsolutePath()      /* directory */
        );
    }

    public static Locale getLocaleByAndroidCode(String code) {
        if (!TextUtils.isEmpty(code)) {
            return code.contains("-r")
                    ? new Locale(code.substring(0, 2), code.substring(4, 6)) // de-rAT
                    : new Locale(code); // de
        }
        return Locale.getDefault();
    }


    public static String readTextfileFromRawRessource(Context context, int rawRessourceId, String linePrefix, String linePostfix) {
        StringBuilder sb = new StringBuilder();
        String line;
        BufferedReader br = null;
        linePrefix = linePrefix == null ? "" : linePrefix;
        linePostfix = linePostfix == null ? "" : linePostfix;

        try {
            br = new BufferedReader(new InputStreamReader(context.getResources().openRawResource(rawRessourceId)));
            while ((line = br.readLine()) != null) {
                sb.append(linePrefix);
                sb.append(line);
                sb.append(linePostfix);
                sb.append("\n");
            }
        } catch (Exception ignored) {
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException ignored) {
                }
            }
        }
        return sb.toString();
    }

    public static String hexColorFromRessourceColor(Context context, int idColor) {
        return "#" + Integer.toHexString(context.getResources().getColor(idColor) & 0x00ffffff);
    }

    public static String colorToHex(int color) {
        return "#" + Integer.toHexString(color & 0x00ffffff);
    }

    public static void printBundle(Bundle savedInstanceState, String k) {
        if (savedInstanceState != null) {
            for (String key : savedInstanceState.keySet()) {
                AppLog.d("SAVED", key + " is a key in the bundle " + k);
                Object bun = savedInstanceState.get(key);
                if (bun != null) {
                    if (bun instanceof Bundle) {
                        printBundle((Bundle) bun, k + "." + key);
                    } else if (bun instanceof byte[]) {
                        AppLog.d("SAVED", "Key: " + k + "." + key + ": " + Arrays.toString((byte[]) bun));
                    } else {
                        AppLog.d("SAVED", "Key: " + k + "." + key + ": " + bun.toString());
                    }
                }
            }
        }
    }

    /**
     * Show Information if user is offline, returns true if is not connected to internet
     *
     * @param context Context
     * @param anchor  A view anchor
     */
    public static boolean showInfoIfUserNotConnectedToInternet(Context context, View anchor) {
        boolean isOnline = WebHelper.isOnline(context);
        if (!isOnline) {
            Snackbar.make(anchor, R.string.no_internet, Snackbar.LENGTH_LONG).show();
        }
        return !isOnline;
    }

    /**
     * Send an Intent that opens url in any browser
     *
     * @param context context
     * @param url     url
     */
    public static void openInExternalBrowser(Context context, String url) {
        Intent openBrowserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        openBrowserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(openBrowserIntent);
    }
}
