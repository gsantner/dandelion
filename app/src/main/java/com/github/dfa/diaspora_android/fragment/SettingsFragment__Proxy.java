package com.github.dfa.diaspora_android.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.dfa.diaspora_android.R;
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

    @BindView(R.id.settings_activity__proxy_activated)
    protected RelativeLayout optionProxyActivated;

    @BindView(R.id.settings_activity__proxy_activated_checkbox)
    protected CheckBox checkboxProxyActivated;

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
        ThemeHelper.updateCheckBoxColor(checkboxProxyActivated);
        optionProxyHost.setVisibility(getAppSettings().isProxyEnabled() ? View.VISIBLE : View.GONE);
        optionProxyPort.setVisibility(getAppSettings().isProxyEnabled() ? View.VISIBLE : View.GONE);
        optionProxyOrbotPreset.setVisibility(getAppSettings().isProxyEnabled() ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void applySettingsToViews() {
        checkboxProxyActivated.setChecked(getAppSettings().isProxyEnabled());
        optionProxyHost.setEnabled(getAppSettings().isProxyEnabled());
        hintProxyHost.setText(getAppSettings().getProxyHost());
        optionProxyPort.setEnabled(getAppSettings().isProxyEnabled());
        hintProxyPort.setText(""+getAppSettings().getProxyPort());
        optionProxyOrbotPreset.setEnabled(getAppSettings().isProxyEnabled());
    }

    @Override
    protected void setOnClickListenersOnViews() {
        optionProxyActivated.setOnClickListener(this);
        checkboxProxyActivated.setOnClickListener(this);
        optionProxyHost.setOnClickListener(this);
        optionProxyPort.setOnClickListener(this);
        optionProxyOrbotPreset.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.settings_activity__proxy_activated:
            case R.id.settings_activity__proxy_activated_checkbox:
                boolean proxyEnabled = !getAppSettings().isProxyEnabled();
                checkboxProxyActivated.setChecked(proxyEnabled);
                getAppSettings().setProxyEnabled(proxyEnabled);
                optionProxyHost.setEnabled(proxyEnabled);
                optionProxyPort.setEnabled(proxyEnabled);
                optionProxyOrbotPreset.setEnabled(proxyEnabled);
                optionProxyHost.setVisibility(getAppSettings().isProxyEnabled() ? View.VISIBLE : View.GONE);
                optionProxyPort.setVisibility(getAppSettings().isProxyEnabled() ? View.VISIBLE : View.GONE);
                optionProxyOrbotPreset.setVisibility(getAppSettings().isProxyEnabled() ? View.VISIBLE : View.GONE);
                break;
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
        input.setText(getAppSettings().getProxyHost());
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

    protected void showProxyPortDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        final EditText input = (EditText) getLayoutInflater(null).inflate(R.layout.settings_activity__dialog_proxy, null, false);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setText(""+getAppSettings().getProxyPort());
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
