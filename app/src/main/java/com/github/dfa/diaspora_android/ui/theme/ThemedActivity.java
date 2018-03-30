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
package com.github.dfa.diaspora_android.ui.theme;

import android.annotation.TargetApi;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;

import com.github.dfa.diaspora_android.App;
import com.github.dfa.diaspora_android.R;
import com.github.dfa.diaspora_android.util.AppSettings;
import com.github.dfa.diaspora_android.util.ContextUtils;

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
        updateLanguage();
        updateStatusBarColor();
        updateRecentAppColor();
        applyColorToViews();
        updateScreenRotation();
    }

    protected abstract void applyColorToViews();

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

    }

    protected void updateScreenRotation() {
        String setting = getAppSettings().getScreenRotation();
        int rotation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;  // Default (system settings)

        if (setting.equals(getString(R.string.rotation_val_sensor))) {
            rotation = ActivityInfo.SCREEN_ORIENTATION_SENSOR;
        } else if (setting.equals(getString(R.string.rotation_val_portrait))) {
            rotation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT;
        } else if (setting.equals(getString(R.string.rotation_val_landscape))) {
            rotation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE;
        }
        setRequestedOrientation(rotation);
    }

    public void updateLanguage() {
        AppSettings appSettings = getAppSettings();
        ContextUtils.get().setAppLanguage(appSettings.getLanguage());
    }
}
