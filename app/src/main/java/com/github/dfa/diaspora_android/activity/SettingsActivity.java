package com.github.dfa.diaspora_android.activity;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.dfa.diaspora_android.App;
import com.github.dfa.diaspora_android.R;
import com.github.dfa.diaspora_android.ui.theme.ColorPalette;
import com.github.dfa.diaspora_android.ui.theme.ThemeHelper;
import com.github.dfa.diaspora_android.ui.theme.ThemedActivity;
import com.github.dfa.diaspora_android.ui.theme.ThemedPreferenceFragment;
import com.github.dfa.diaspora_android.util.AppLog;
import com.github.dfa.diaspora_android.util.AppSettings;
import com.github.dfa.diaspora_android.util.DiasporaUrlHelper;
import com.github.dfa.diaspora_android.web.ProxyHandler;

import butterknife.BindView;
import butterknife.ButterKnife;
import uz.shift.colorpicker.LineColorPicker;
import uz.shift.colorpicker.OnColorChangedListener;

/**
 * SettingsActivity
 * Created by vanitas on 24.10.16.
 */

public class SettingsActivity extends ThemedActivity {

    //Toolbar
    @BindView(R.id.settings__appbar)
    protected AppBarLayout appBarLayout;

    @BindView(R.id.settings__toolbar)
    protected Toolbar toolbar;

    private ProxyHandler.ProxySettings oldProxySettings;

    public void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.settings__activity);
        ButterKnife.bind(this);
        toolbar.setTitle(R.string.settings);
        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_white_24px));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingsActivity.this.onBackPressed();
            }
        });

        oldProxySettings = getAppSettings().getProxySettings();
        showFragment(SettingsFragmentMaster.TAG, false);
    }

    protected void showFragment(String tag, boolean addToBackStack) {
        PreferenceFragment fragment = (PreferenceFragment) getFragmentManager().findFragmentByTag(tag);
        if (fragment == null) {
            switch (tag) {
                case SettingsFragmentThemes.TAG:
                    fragment = new SettingsFragmentThemes();
                    break;
                case SettingsFragmentNavSlider.TAG:
                    fragment = new SettingsFragmentNavSlider();
                    break;
                case SettingsFragmentProxy.TAG:
                    fragment = new SettingsFragmentProxy();
                    break;
                case SettingsFragmentDebugging.TAG:
                    fragment = new SettingsFragmentDebugging();
                    break;
                case SettingsFragmentMaster.TAG:
                default:
                    fragment = new SettingsFragmentMaster();
                    break;
            }
        }
        FragmentTransaction t = getFragmentManager().beginTransaction();
        if (addToBackStack) {
            t.addToBackStack(tag);
        }
        t.replace(R.id.settings__fragment_container, fragment, tag).commit();
    }

    @Override
    public void applyColorToViews() {
        //Toolbar
        ThemeHelper.updateToolbarColor(toolbar);
    }

    @Override
    protected void onStop() {
        ProxyHandler.ProxySettings newProxySettings = getAppSettings().getProxySettings();
        if (!oldProxySettings.equals(newProxySettings)) {
            AppLog.d(this, "ProxySettings changed.");
            //Proxy on-off? => Restart app
            if (oldProxySettings.isEnabled() && !newProxySettings.isEnabled()) {
                AppLog.d(this, "Proxy deactivated. Restarting app...");
                Intent restartActivity = new Intent(SettingsActivity.this, MainActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(SettingsActivity.this, 12374, restartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
                AlarmManager mgr = (AlarmManager) SettingsActivity.this.getSystemService(Context.ALARM_SERVICE);
                mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, pendingIntent);
                System.exit(0);
            } //Proxy changed? => Update
            else {
                ProxyHandler.getInstance().updateProxySettings(this);
            }
        }
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        ThemedPreferenceFragment top = getTopFragment();
        if(top != null && top.getFragmentTag().equals(SettingsFragmentProxy.TAG)) {
            ProxyHandler.ProxySettings newProxySettings = getAppSettings().getProxySettings();
            if(oldProxySettings.isEnabled() && !newProxySettings.isEnabled()) {
                Toast.makeText(this, R.string.toast__proxy_disabled__restart_required, Toast.LENGTH_LONG).show();
            }
        }
        super.onBackPressed();
    }

    /**
     * Return the fragment which is currently displayed in R.id.fragment_container
     *
     * @return top fragment or null if there is none displayed
     */
    private ThemedPreferenceFragment getTopFragment() {
        return (ThemedPreferenceFragment) getFragmentManager().findFragmentById(R.id.settings__fragment_container);
    }

    public static class SettingsFragmentMaster extends ThemedPreferenceFragment {
        public static final String TAG = "com.github.dfa.diaspora_android.settings.SettingsFragmentMaster";

        public void onCreate(Bundle savedInstances) {
            super.onCreate(savedInstances);
            getPreferenceManager().setSharedPreferencesName("app");
            addPreferencesFromResource(R.xml.preferences__master);
        }

        @Override
        public void updateViewColors() {

        }

        @Override
        public boolean onPreferenceTreeClick(PreferenceScreen screen, Preference preference) {
            if (isAdded() && preference.hasKey()) {
                AppSettings settings = ((App) getActivity().getApplication()).getSettings();
                DiasporaUrlHelper diasporaUrlHelper = new DiasporaUrlHelper(settings);
                String key = preference.getKey();
                /** Sub-Categories */
                if (settings.isKeyEqual(key, R.string.pref_key__cat_themes)) {
                    ((SettingsActivity) getActivity()).showFragment(SettingsFragmentThemes.TAG, true);
                    return true;
                } else if (settings.isKeyEqual(key, R.string.pref_key__cat_nav_slider)) {
                    ((SettingsActivity) getActivity()).showFragment(SettingsFragmentNavSlider.TAG, true);
                    return true;
                } else if (settings.isKeyEqual(key, R.string.pref_key__cat_proxy)) {
                    ((SettingsActivity) getActivity()).showFragment(SettingsFragmentProxy.TAG, true);
                    return true;
                } else if (settings.isKeyEqual(key, R.string.pref_key__cat_debugging)) {
                    ((SettingsActivity) getActivity()).showFragment(SettingsFragmentDebugging.TAG, true);
                    return true;
                }
                /** Network */
                else if (settings.isKeyEqual(key, R.string.pref_key__clear_cache)) {
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    intent.setAction(MainActivity.ACTION_CLEAR_CACHE);
                    startActivity(intent);
                    getActivity().finish();
                    return true;
                }
                /** Pod Settings */
                if (settings.isKeyEqual(key, R.string.pref_key__personal_settings)) {
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    intent.setAction(MainActivity.ACTION_OPEN_URL);
                    intent.putExtra(MainActivity.URL_MESSAGE, diasporaUrlHelper.getPersonalSettingsUrl());
                    startActivity(intent);
                    getActivity().finish();
                    return true;
                } else if (settings.isKeyEqual(key, R.string.pref_key__manage_tags)) {
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    intent.setAction(MainActivity.ACTION_OPEN_URL);
                    intent.putExtra(MainActivity.URL_MESSAGE, diasporaUrlHelper.getManageTagsUrl());
                    startActivity(intent);
                    getActivity().finish();
                    return true;
                } else if (settings.isKeyEqual(key, R.string.pref_key__manage_contacts)) {
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    intent.setAction(MainActivity.ACTION_OPEN_URL);
                    intent.putExtra(MainActivity.URL_MESSAGE, diasporaUrlHelper.getManageContactsUrl());
                    startActivity(intent);
                    getActivity().finish();
                    return true;
                } else if (settings.isKeyEqual(key, R.string.pref_key__change_account)) {
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
            }
            return super.onPreferenceTreeClick(screen, preference);
        }

        @Override
        public String getFragmentTag() {
            return TAG;
        }
    }

    public static class SettingsFragmentThemes extends ThemedPreferenceFragment {
        public static final String TAG = "com.github.dfa.diaspora_android.settings.SettingsFragmentThemes";

        public void onCreate(Bundle savedInstances) {
            super.onCreate(savedInstances);
            getPreferenceManager().setSharedPreferencesName("app");
            addPreferencesFromResource(R.xml.preferences__sub_themes);
        }

        @Override
        public void updateViewColors() {
            if (isAdded()) {
                //Trigger redraw of whole preference screen in order to reflect changes
                setPreferenceScreen(null);
                addPreferencesFromResource(R.xml.preferences__sub_themes);
            }
        }

        @Override
        public boolean onPreferenceTreeClick(PreferenceScreen screen, Preference preference) {
            if (isAdded() && preference.hasKey()) {
                String key = preference.getKey();
                if (key.equals(getString(R.string.pref_key__primary_color__preference_click))) {
                    showColorPickerDialog(1);
                    return true;
                } else if (key.equals(getString(R.string.pref_key__accent_color__preference_click))) {
                    showColorPickerDialog(2);
                    return true;
                }
            }
            return super.onPreferenceTreeClick(screen, preference);
        }

        @Override
        public String getFragmentTag() {
            return TAG;
        }

        /**
         * Show a colorPicker Dialog
         *
         * @param type 1 -> Primary Color, 2 -> Accent Color
         */
        public void showColorPickerDialog(final int type) {
            final AppSettings appSettings = ((App) getActivity().getApplication()).getSettings();
            final Context context = getActivity();

            //Inflate dialog layout
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View dialogLayout = inflater.inflate(R.layout.ui__dialog__color_picker, null);
            final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(context);
            builder.setView(dialogLayout);

            final FrameLayout titleBackground = (FrameLayout) dialogLayout.findViewById(R.id.color_picker_dialog__title_background);
            final TextView title = (TextView) dialogLayout.findViewById(R.id.color_picker_dialog__title);
            final LineColorPicker base = (LineColorPicker) dialogLayout.findViewById(R.id.color_picker_dialog__base_picker);
            final LineColorPicker shade = (LineColorPicker) dialogLayout.findViewById(R.id.color_picker_dialog__shade_picker);

            title.setText(type == 1 ? R.string.pref_title__primary_color : R.string.pref_title__accent_color);
            title.setTextColor(getResources().getColor(R.color.white));
            final int[] current = (type == 1 ? appSettings.getPrimaryColorSettings() : appSettings.getAccentColorSettings());
            base.setColors((type == 1 ? ColorPalette.getBaseColors(context) : ColorPalette.getAccentColors(context)));
            base.setSelectedColor(current[0]);
            shade.setColors(ColorPalette.getColors(context, current[0]));
            shade.setSelectedColor(current[1]);
            titleBackground.setBackgroundColor(shade.getColor());
            title.setTextColor(ThemeHelper.getTextColorFromBackgroundColor(shade.getColor()));
            base.setOnColorChangedListener(new OnColorChangedListener() {
                @Override
                public void onColorChanged(int i) {
                    shade.setColors(ColorPalette.getColors(context, i));
                    titleBackground.setBackgroundColor(i);
                    title.setTextColor(ThemeHelper.getTextColorFromBackgroundColor(i));
                    if (i == current[0]) {
                        shade.setSelectedColor(current[1]);
                        titleBackground.setBackgroundColor(shade.getColor());
                    } else {
                        shade.setSelectedColor(i);
                    }
                }
            });
            shade.setOnColorChangedListener(new OnColorChangedListener() {
                @Override
                public void onColorChanged(int i) {
                    titleBackground.setBackgroundColor(i);
                    title.setTextColor(ThemeHelper.getTextColorFromBackgroundColor(i));
                }
            });

            //Build dialog
            builder
                    .setNegativeButton(android.R.string.cancel, null)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (type == 1) {
                                appSettings.setPrimaryColorSettings(base.getColor(), shade.getColor());
                                if (Build.VERSION.SDK_INT >= 21) {
                                    getActivity().getWindow().setStatusBarColor(ThemeHelper.getPrimaryDarkColor());
                                }
                                ((SettingsActivity) getActivity()).applyColorToViews();
                            } else {
                                appSettings.setAccentColorSettings(base.getColor(), shade.getColor());
                            }
                            updateViewColors();
                        }
                    }).show();
        }
    }

    public static class SettingsFragmentNavSlider extends ThemedPreferenceFragment {
        public static final String TAG = "com.github.dfa.diaspora_android.settings.SettingsFragmentNavSlider";

        public void onCreate(Bundle savedInstances) {
            super.onCreate(savedInstances);
            getPreferenceManager().setSharedPreferencesName("app");
            addPreferencesFromResource(R.xml.preferences__sub_navslider_vis);
        }

        @Override
        public void updateViewColors() {

        }

        @Override
        public String getFragmentTag() {
            return TAG;
        }
    }

    public static class SettingsFragmentProxy extends ThemedPreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
        public static final String TAG = "com.github.dfa.diaspora_android.settings.SettingsFragmentProxy";

        public void onCreate(Bundle savedInstances) {
            super.onCreate(savedInstances);
            getPreferenceManager().setSharedPreferencesName("app");
            addPreferencesFromResource(R.xml.preferences__sub_proxy);
            SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
            sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onActivityCreated(Bundle bundle) {
            super.onActivityCreated(bundle);
            updateSummaries();
        }

        public void updateSummaries() {
            if (isAdded()) {
                AppSettings settings = ((App) getActivity().getApplication()).getSettings();
                findPreference(settings.getKey(R.string.pref_key__http_proxy_host)).setSummary(settings.getProxyHttpHost());
                findPreference(settings.getKey(R.string.pref_key__http_proxy_port)).setSummary(Integer.toString(settings.getProxyHttpPort()));
            }
        }

        @Override
        public boolean onPreferenceTreeClick(PreferenceScreen screen, Preference preference) {
            if (isAdded() && preference.hasKey()) {
                AppSettings appSettings = ((App) getActivity().getApplication()).getSettings();
                String key = preference.getKey();
                if (appSettings.isKeyEqual(key, R.string.pref_key__http_proxy_load_tor_preset)) {
                    appSettings.setProxyHttpHost("127.0.0.1");
                    appSettings.setProxyHttpPort(8118);
                    Toast.makeText(screen.getContext(), R.string.toast__proxy_orbot_preset_loaded, Toast.LENGTH_SHORT).show();
                    return true;
                }
            }
            return super.onPreferenceTreeClick(screen, preference);
        }

        @Override
        public String getFragmentTag() {
            return TAG;
        }

        @Override
        public void updateViewColors() {

        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (isAdded()) {
                if (key.equals(getString(R.string.pref_key__http_proxy_host)) ||
                        key.equals(getString(R.string.pref_key__http_proxy_port))) {
                    updateSummaries();
                }
            }
        }
    }

    public static class SettingsFragmentDebugging extends ThemedPreferenceFragment {
        public static final String TAG = "com.github.dfa.diaspora_android.settings.SettingsFragmentDebugging";

        public void onCreate(Bundle savedInstances) {
            super.onCreate(savedInstances);
            getPreferenceManager().setSharedPreferencesName("app");
            addPreferencesFromResource(R.xml.preferences__sub_debugging);
        }

        @Override
        public void updateViewColors() {

        }

        @Override
        public String getFragmentTag() {
            return TAG;
        }
    }
}
