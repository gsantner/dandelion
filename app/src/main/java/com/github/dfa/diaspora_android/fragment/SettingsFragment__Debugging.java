package com.github.dfa.diaspora_android.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.dfa.diaspora_android.R;
import com.github.dfa.diaspora_android.util.AppLog;
import com.github.dfa.diaspora_android.util.theming.ThemeHelper;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * SettingsFragment that contains debugging options
 * Created by vanitas on 16.10.16.
 */

public class SettingsFragment__Debugging extends ThemedSettingsFragment {
    public static final String TAG = "com.github.dfa.diaspora_android.SettingsFragment__Debugging";

    @BindView(R.id.settings_activity__header_more__debugging)
    protected TextView titleDebugging;

    @BindView(R.id.settings_activity__debugging_activated)
    protected RelativeLayout optionDebuggingActivated;

    @BindView(R.id.settings_activity__debugging_activated_checkbox)
    protected CheckBox checkboxDebuggingActivated;

    @BindView(R.id.settings_activity__debugging_verbose)
    protected RelativeLayout optionDebuggingVerbose;

    @BindView(R.id.settings_activity__debugging_verbose_checkbox)
    protected CheckBox checkboxDebuggingVerbose;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        AppLog.d(this, "onCreateView()");
        View layout = inflater.inflate(R.layout.settings_activity__subsection_debugging, container, false);
        ButterKnife.bind(this, layout);
        return layout;
    }

    @Override
    protected void applyColorToViews() {
        ThemeHelper.updateTitleColor(titleDebugging);
        ThemeHelper.updateCheckBoxColor(checkboxDebuggingActivated);
        ThemeHelper.updateCheckBoxColor(checkboxDebuggingVerbose);
    }

    @Override
    protected void applySettingsToViews() {
        checkboxDebuggingActivated.setChecked(getAppSettings().isLoggingEnabled());
        checkboxDebuggingVerbose.setChecked(getAppSettings().isLoggingSpamEnabled());
    }

    @Override
    protected void setOnClickListenersOnViews() {
        optionDebuggingActivated.setOnClickListener(this);
        checkboxDebuggingActivated.setOnClickListener(this);
        optionDebuggingVerbose.setOnClickListener(this);
        checkboxDebuggingVerbose.setOnClickListener(this);
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.settings_activity__debugging_activated:
            case R.id.settings_activity__debugging_activated_checkbox:
                checkboxDebuggingActivated.setChecked(!getAppSettings().isLoggingEnabled());
                getAppSettings().setLoggingEnabled(!getAppSettings().isLoggingEnabled());
                break;
            case R.id.settings_activity__debugging_verbose:
            case R.id.settings_activity__debugging_verbose_checkbox:
                checkboxDebuggingVerbose.setChecked(!getAppSettings().isLoggingSpamEnabled());
                getAppSettings().setLoggingSpamEnabled(!getAppSettings().isLoggingSpamEnabled());
                break;
        }
    }
}
