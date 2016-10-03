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

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.github.dfa.diaspora_android.App;
import com.github.dfa.diaspora_android.R;
import com.github.dfa.diaspora_android.data.AppSettings;
import com.github.dfa.diaspora_android.util.AppLog;

/**
 * @author vanitas
 */
public class SettingsActivity extends AppCompatActivity {
    private boolean activityRestartRequired;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar toolbar = getSupportActionBar();
        if (toolbar != null)
            toolbar.setDisplayHomeAsUpEnabled(true);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment()).commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    private void setActivityRestartRequired() {
        this.activityRestartRequired = true;
    }

    public static class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
        private SharedPreferences sharedPreferences;

        public void onCreate(Bundle savedInstances) {
            super.onCreate(savedInstances);
            getPreferenceManager().setSharedPreferencesName("app");
            addPreferencesFromResource(R.xml.preferences);
            sharedPreferences = getPreferenceScreen().getSharedPreferences();
            sharedPreferences.registerOnSharedPreferenceChangeListener(this);
            setPreferenceSummaries();
            sharedPreferences.edit().putBoolean(getString(R.string.pref_key__proxy_was_enabled),
                    sharedPreferences.getBoolean(getString(R.string.pref_key__proxy_enabled), false)).apply();
        }

        private void setPreferenceSummaries() {
            String[] editTextKeys = new String[]{
                    getString(R.string.pref_key__proxy_host), getString(R.string.pref_key__proxy_port)
            };
            for (String key : editTextKeys) {
                EditTextPreference p = (EditTextPreference) findPreference(key);
                p.setSummary(p.getText());
            }
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            updatePreference(findPreference(key));
            if (key != null && isAdded() && (key.equals(getString(R.string.pref_key__font_size)) ||
                    key.equals(getString(R.string.pref_key__load_images)) ||
                    key.equals(getString(R.string.pref_key__intellihide_toolbars)) ||
                    key.equals(getString(R.string.pref_key__proxy_enabled)) ||
                    key.equals(getString(R.string.pref_key__proxy_host)) ||
                    key.equals(getString(R.string.pref_key__proxy_port)) ||
                    key.startsWith("pref_key__visibility_nav__"))) {
                ((SettingsActivity) getActivity()).setActivityRestartRequired();
            }
        }

        private void updatePreference(Preference preference) {
            if (preference == null) {
                return;
            }
            if (preference instanceof EditTextPreference) {
                EditTextPreference textPref = (EditTextPreference) preference;
                textPref.setSummary(textPref.getText());
                return;
            }
            if (preference instanceof ListPreference) {
                ListPreference listPref = (ListPreference) preference;
                listPref.setSummary(listPref.getEntry());
            }
        }

        @Override
        public boolean onPreferenceTreeClick(PreferenceScreen screen, Preference preference) {
            Intent intent = new Intent(getActivity(), MainActivity.class);
            String podDomain = ((App) getActivity().getApplication()).getSettings().getPodDomain();
            switch (preference.getTitleRes()) {
                case R.string.pref_title__personal_settings: {
                    intent.setAction(MainActivity.ACTION_OPEN_URL);
                    intent.putExtra(MainActivity.URL_MESSAGE, "https://" + podDomain + "/user/edit");
                    break;
                }
                case R.string.pref_title__manage_tags: {
                    intent.setAction(MainActivity.ACTION_OPEN_URL);
                    intent.putExtra(MainActivity.URL_MESSAGE, "https://" + podDomain + "/tag_followings/manage");
                    break;
                }
                case R.string.pref_title__manage_contacts: {
                    intent.setAction(MainActivity.ACTION_OPEN_URL);
                    intent.putExtra(MainActivity.URL_MESSAGE, "https://" + podDomain + "/contacts");
                    break;
                }
                case R.string.pref_title__change_account: {
                    new AlertDialog.Builder(getActivity())
                            .setTitle(getString(R.string.confirmation))
                            .setMessage(getString(R.string.pref_warning__change_account))
                            .setNegativeButton(android.R.string.no, null)
                            .setPositiveButton(android.R.string.yes,
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            Intent intent = new Intent(getActivity(), MainActivity.class);
                                            intent.setAction(MainActivity.ACTION_CHANGE_ACCOUNT);
                                            startActivity(intent);
                                            getActivity().finish();
                                        }
                                    })
                            .show();
                    return true;
                }
                case R.string.pref_title__clear_cache: {
                    intent.setAction(MainActivity.ACTION_CLEAR_CACHE);
                    break;
                }

                default: {
                    intent = null;
                    break;
                }
            }
            if (intent != null) {
                startActivity(intent);
                getActivity().finish();
                return true;
            }
            return super.onPreferenceTreeClick(screen, preference);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Reset logging
        AppSettings settings = new AppSettings(getApplicationContext());
        AppLog.setLoggingEnabled(settings.isLoggingEnabled());
        AppLog.setLoggingSpamEnabled(settings.isLoggingSpamEnabled());
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (activityRestartRequired) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setAction(MainActivity.ACTION_RELOAD_ACTIVITY);
            startActivity(intent);
        }
    }
}
