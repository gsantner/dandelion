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
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.dfa.diaspora_android.R;
import com.github.dfa.diaspora_android.ui.ThemedCheckBoxPreference;
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
    protected ThemedCheckBoxPreference checkboxProxyEna;

    @BindView(R.id.settings_activity__proxy_host)
    protected LinearLayout optionProxyHost;

    @BindView(R.id.settings_activity__proxy_host_hint)
    protected TextView hintProxyHost;

    @BindView(R.id.settings_activity__proxy_port)
    protected LinearLayout optionProxyPort;

    @BindView(R.id.settings_activity__proxy_port_hint)
    protected TextView hintProxyPort;

    @BindView(R.id.settings_activity__proxy_orbot_preset)
    protected RelativeLayout optionProxyOrbotPreset;

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
        optionProxyHost.setVisibility(getAppSettings().isProxyHttpEnabled() ? View.VISIBLE : View.GONE);
        optionProxyPort.setVisibility(getAppSettings().isProxyHttpEnabled() ? View.VISIBLE : View.GONE);
        optionProxyOrbotPreset.setVisibility(getAppSettings().isProxyHttpEnabled() ? View.VISIBLE : View.GONE);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void applySettingsToViews() {
        optionProxyHost.setEnabled(getAppSettings().isProxyHttpEnabled());
        hintProxyHost.setText(getAppSettings().getProxyHttpHost());
        optionProxyPort.setEnabled(getAppSettings().isProxyHttpEnabled());
        hintProxyPort.setText(""+getAppSettings().getProxyHttpPort());
        optionProxyOrbotPreset.setEnabled(getAppSettings().isProxyHttpEnabled());
    }

    @Override
    protected void setOnClickListenersOnViews() {
        checkboxProxyEna.setOnCheckedChangedListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                optionProxyHost.setEnabled(b);
                optionProxyPort.setEnabled(b);
                optionProxyOrbotPreset.setEnabled(b);
                optionProxyHost.setVisibility(b ? View.VISIBLE : View.GONE);
                optionProxyPort.setVisibility(b ? View.VISIBLE : View.GONE);
                optionProxyOrbotPreset.setVisibility(b ? View.VISIBLE : View.GONE);
            }
        });
        optionProxyHost.setOnClickListener(this);
        optionProxyPort.setOnClickListener(this);
        optionProxyOrbotPreset.setOnClickListener(this);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.settings_activity__proxy_host:
                showProxyHostDialog();
                break;
            case R.id.settings_activity__proxy_port:
                showProxyPortDialog();
                break;
            case R.id.settings_activity__proxy_orbot_preset:
                final String presetHost = "localhost";
                final int presetPort = 8118;
                getAppSettings().setProxyHttpHost(presetHost);
                hintProxyHost.setText(presetHost);
                getAppSettings().setProxyHttpPort(presetPort);
                hintProxyPort.setText(""+presetPort);
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

    protected void showProxyHostDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        final EditText input = (EditText) getLayoutInflater(null).inflate(R.layout.settings_activity__dialog_proxy, null, false);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setText(getAppSettings().getProxyHttpHost());
        ThemeHelper.updateEditTextColor(input);
        builder.setTitle(R.string.pref_title__http_proxy_host)
                .setView(input).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                getAppSettings().setProxyHttpHost(input.getText().toString());
                hintProxyHost.setText(input.getText());
            }
        }).setNegativeButton(android.R.string.cancel, null).show();
    }

    @SuppressLint("SetTextI18n")
    protected void showProxyPortDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        final EditText input = (EditText) getLayoutInflater(null).inflate(R.layout.settings_activity__dialog_proxy, null, false);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setText(""+getAppSettings().getProxyHttpPort());
        ThemeHelper.updateEditTextColor(input);
        builder.setTitle(R.string.pref_title__http_proxy_port)
                .setView(input).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                getAppSettings().setProxyHttpPort(Integer.parseInt(input.getText().toString()));
                hintProxyPort.setText(input.getText());
            }
        }).setNegativeButton(android.R.string.cancel, null).show();
    }

}
