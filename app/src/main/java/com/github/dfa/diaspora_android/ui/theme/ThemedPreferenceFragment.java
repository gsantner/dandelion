package com.github.dfa.diaspora_android.ui.theme;

import android.os.Build;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.view.Window;

import com.github.dfa.diaspora_android.App;
import com.github.dfa.diaspora_android.util.AppSettings;

/**
 * PreferenceFragment with a colored status bar
 * Created by vanitas on 24.10.16.
 */

public abstract class ThemedPreferenceFragment extends PreferenceFragment {
    public abstract void updateViewColors();

    @Override
    public void onResume() {
        super.onResume();
        updateViewColors();
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen screen, Preference preference) {
        if (isAdded()) {
            App app = ((App) getActivity().getApplication());
            AppSettings appSettings = app.getSettings();
            if (Build.VERSION.SDK_INT >= 21) {
                if (preference instanceof PreferenceScreen && ((PreferenceScreen) preference).getDialog() != null) {
                    Window window = ((PreferenceScreen) preference).getDialog().getWindow();
                    if (window != null) {
                        ThemeHelper.getInstance(appSettings);
                        window.setStatusBarColor(ThemeHelper.getPrimaryDarkColor());
                    }
                }
            }
        }
        return super.onPreferenceTreeClick(screen, preference);
    }

    public abstract String getFragmentTag();
}
