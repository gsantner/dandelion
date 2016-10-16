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
package com.github.dfa.diaspora_android.activity;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;

import com.github.dfa.diaspora_android.App;
import com.github.dfa.diaspora_android.R;
import com.github.dfa.diaspora_android.data.AppSettings;
import com.github.dfa.diaspora_android.util.theming.ThemeHelper;

/**
 * Activity that supports color schemes
 * Created by vanitas on 06.10.16.
 */

public abstract class ThemedActivity extends AppCompatActivity {

    protected AppSettings getAppSettings() {
        return ((App) getApplication()).getSettings();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ThemeHelper.getInstance(getAppSettings());
        updateStatusBarColor();
        updateRecentAppColor();
        applyColorToViews();
    }

    public abstract void applyColorToViews();

    /**
     * Update color of the status bar
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void updateStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ThemeHelper.getPrimaryDarkColor());
        }
    }

    /**
     * Update primary color in recent apps overview
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void updateRecentAppColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            BitmapDrawable drawable = ((BitmapDrawable) getDrawable(R.drawable.ic_launcher));
            if (drawable != null) {
                setTaskDescription(new ActivityManager.TaskDescription(
                        getResources().getString(R.string.app_name),
                        drawable.getBitmap(),
                        getAppSettings().getPrimaryColor()));
            }
        }
    }
}
