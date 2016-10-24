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
package com.github.dfa.diaspora_android.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.github.dfa.diaspora_android.R;
import com.github.dfa.diaspora_android.ui.ThemedCheckBoxPreference;
import com.github.dfa.diaspora_android.ui.ThemedIntEditTextPreference;
import com.github.dfa.diaspora_android.ui.ThemedPreference;
import com.github.dfa.diaspora_android.ui.ThemedStringEditTextPreference;
import com.github.dfa.diaspora_android.util.AppLog;
import com.github.dfa.diaspora_android.util.theming.ThemeHelper;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * SettingsFragment that contains some proxy related settings
 * Created by vanitas on 16.10.16.
 */

public class SettingsFragment__Proxy extends ThemedSettingsFragment {
    public static final String TAG = "com.github.dfa.diaspora_android.SettingsFragment__Proxy";

    @BindView(R.id.settings_activity__header_network__proxy)
    protected TextView titleProxy;

    @BindView(R.id.settings_activity__proxy_enabled)
    protected ThemedCheckBoxPreference checkboxProxyEnabled;

    @BindView(R.id.settings_activity__proxy_host)
    protected ThemedStringEditTextPreference editTextProxyHost;

    @BindView(R.id.settings_activity__proxy_port)
    protected ThemedIntEditTextPreference editTextProxyPort;

    @BindView(R.id.settings_activity__proxy_orbot_preset)
    protected ThemedPreference optionProxyOrbotPreset;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        AppLog.d(this, "onCreateView()");
        View layout = inflater.inflate(R.layout.settings_activity__subsection_proxy, container, false);
        ButterKnife.bind(this, layout);
        return layout;
    }

    @Override
    protected void applyColorToViews() {
        ThemeHelper.updateTitleColor(titleProxy);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void applySettingsToViews() {
        boolean enabled = getAppSettings().isProxyHttpEnabled();
        editTextProxyHost.setVisibility(enabled ? View.VISIBLE : View.GONE);
        editTextProxyHost.setEnabled(enabled);
        editTextProxyPort.setVisibility(enabled ? View.VISIBLE : View.GONE);
        editTextProxyPort.setEnabled(enabled);
        optionProxyOrbotPreset.setVisibility(enabled ? View.VISIBLE : View.GONE);
        optionProxyOrbotPreset.setEnabled(enabled);
    }

    @Override
    protected void setOnClickListenersOnViews() {
        optionProxyOrbotPreset.setOnClickListener(this);
        checkboxProxyEnabled.setOnCheckedChangedListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                AppLog.d(this, "Clicked: Proxy Enabled");
                applySettingsToViews();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.settings_activity__proxy_orbot_preset:
                AppLog.d(this, "Clicked: Proxy Preset");
                editTextProxyHost.setValue("localhost");
                editTextProxyPort.setValue(8118);
                Toast.makeText(getContext(), R.string.toast__proxy_orbot_preset_set, Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public String getFragmentTag() {
        return TAG;
    }

    @Override
    public void onCreateBottomOptionsMenu(Menu menu, MenuInflater inflater) {
        /* Nothing to do */
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }
}
