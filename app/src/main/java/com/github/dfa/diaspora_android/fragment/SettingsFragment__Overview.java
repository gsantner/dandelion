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

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.dfa.diaspora_android.R;
import com.github.dfa.diaspora_android.activity.MainActivity;
import com.github.dfa.diaspora_android.util.AppLog;
import com.github.dfa.diaspora_android.util.DiasporaUrlHelper;
import com.github.dfa.diaspora_android.util.theming.ThemeHelper;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Fragment that holds the main settings screen
 * Created by vanitas on 15.10.16.
 */

public class SettingsFragment__Overview extends ThemedSettingsFragment {
    public static final String TAG = "com.github.dfa.diaspora_android.SettingsFragment__Overview";

    //Appearance
    @BindView(R.id.settings_activity__header_appearance)
    protected TextView titleAppearance;

    @BindView(R.id.settings_activity__theme_colors)
    protected LinearLayout optionThemeColors;

    @BindView(R.id.settings_activity__navigation_slider)
    protected LinearLayout optionNavigationSlider;

    @BindView(R.id.settings_activity__font_size)
    protected LinearLayout optionFontSize;

    //@BindView(R.id.settings_activity__intellihide_toolbars)
    protected RelativeLayout optionIntellihideToolbars;

    @BindView(R.id.settings_activity__extended_notifications)
    protected RelativeLayout optionExtendedNotifications;

    @BindView(R.id.settings_activity__append_shared_via_app)
    protected RelativeLayout optionAppendSharedViaApp;

    @BindView(R.id.settings_activity__chrome_custom_tabs)
    protected RelativeLayout optionCustomTabs;

    @BindView(R.id.settings_activity__font_size_hint)
    protected TextView hintFontSize;

    //@BindView(R.id.settings_activity__intellihide_toolbars_checkbox)
    protected CheckBox checkboxIntellihide;

    @BindView(R.id.settings_activity__extended_notifications_checkbox)
    protected CheckBox checkboxExtendedNotifications;

    @BindView(R.id.settings_activity__append_shared_via_app_checkbox)
    protected CheckBox checkboxAppendSharedViaApp;

    @BindView(R.id.settings_activity__chrome_custom_tabs_checkbox)
    protected CheckBox checkboxCustomTabs;

    //Pod Settings
    @BindView(R.id.settings_activity__header_pod_settings)
    protected TextView titlePodSettings;

    @BindView(R.id.settings_activity__personal_settings)
    protected LinearLayout optionPersonalSettings;

    @BindView(R.id.settings_activity__manage_tags)
    protected LinearLayout optionManageTags;

    @BindView(R.id.settings_activity__manage_contacts)
    protected LinearLayout optionManageContacts;

    @BindView(R.id.settings_activity__change_account)
    protected LinearLayout optionChangeAccount;

    //Network
    @BindView(R.id.settings_activity__header_network)
    protected TextView titleNetwork;

    @BindView(R.id.settings_activity__load_images)
    protected RelativeLayout optionLoadImages;

    @BindView(R.id.settings_activity__clear_cache)
    protected LinearLayout optionClearCache;

    @BindView(R.id.settings_activity__proxy_settings)
    protected LinearLayout optionProxySettings;

    @BindView(R.id.settings_activity__load_images_checkbox)
    protected CheckBox checkboxLoadImages;

    //More
    @BindView(R.id.settings_activity__header_more)
    protected TextView titleMore;

    @BindView(R.id.settings_activity__debugging)
    LinearLayout optionDebugging;

    protected DiasporaUrlHelper urls;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        AppLog.d(this, "onCreateView()");
        View layout = inflater.inflate(R.layout.settings_activity__overview, container, false);
        ButterKnife.bind(this, layout);
        return layout;
    }

    @Override
    public void onViewCreated(View layout, Bundle savedInstanceState) {
        super.onViewCreated(layout, savedInstanceState);
        urls = new DiasporaUrlHelper(getAppSettings());
    }

    protected void applySettingsToViews() {
        hintFontSize.setText(getAppSettings().getMinimumFontSizeString());
        //checkboxIntellihide.setChecked(getAppSettings().isIntellihideToolbars());
        checkboxExtendedNotifications.setChecked(getAppSettings().isExtendedNotifications());
        checkboxAppendSharedViaApp.setChecked(getAppSettings().isAppendSharedViaApp());
        checkboxCustomTabs.setChecked(getAppSettings().isChromeCustomTabsEnabled());
        checkboxLoadImages.setChecked(getAppSettings().isLoadImages());
    }

    protected void setOnClickListenersOnViews() {
        /** Appearance */
        optionThemeColors.setOnClickListener(this);
        optionNavigationSlider.setOnClickListener(this);
        optionFontSize.setOnClickListener(this);
        //optionIntellihideToolbars.setOnClickListener(this);
        //checkboxIntellihide.setOnClickListener(this);
        optionExtendedNotifications.setOnClickListener(this);
        checkboxExtendedNotifications.setOnClickListener(this);
        optionAppendSharedViaApp.setOnClickListener(this);
        checkboxAppendSharedViaApp.setOnClickListener(this);
        optionCustomTabs.setOnClickListener(this);
        checkboxCustomTabs.setOnClickListener(this);
        /** Pod Settings */
        optionPersonalSettings.setOnClickListener(this);
        optionManageTags.setOnClickListener(this);
        optionManageContacts.setOnClickListener(this);
        optionChangeAccount.setOnClickListener(this);
        /** Network */
        optionLoadImages.setOnClickListener(this);
        checkboxLoadImages.setOnClickListener(this);
        optionClearCache.setOnClickListener(this);
        optionProxySettings.setOnClickListener(this);
        /** More */
        optionDebugging.setOnClickListener(this);
    }

    @Override
    protected void applyColorToViews() {
        AppLog.d(this, "applyColorToViews()");
        ThemeHelper.getInstance(getAppSettings());
        //Card Titles
        ThemeHelper.updateTitleColor(titleAppearance);
        ThemeHelper.updateTitleColor(titlePodSettings);
        ThemeHelper.updateTitleColor(titleNetwork);
        ThemeHelper.updateTitleColor(titleMore);
        //Checkboxes
        //ThemeHelper.updateCheckBoxColor(checkboxIntellihide);
        ThemeHelper.updateCheckBoxColor(checkboxExtendedNotifications);
        ThemeHelper.updateCheckBoxColor(checkboxAppendSharedViaApp);
        ThemeHelper.updateCheckBoxColor(checkboxCustomTabs);
        ThemeHelper.updateCheckBoxColor(checkboxLoadImages);
    }

    protected void showFontSizeDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        ListView listView = (ListView) getLayoutInflater(null).inflate(R.layout.settings_activity__dialog_font_size, null, false);
        builder.setView(listView)
                .setTitle(R.string.pref_title__font_size);
        final AlertDialog dialog = builder.create();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                getAppSettings().setMinimumFontSizeIndex(i);
                hintFontSize.setText(getAppSettings().getMinimumFontSizeString());
                dialog.dismiss();
            }
        });
        dialog.show();
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
            /** Appearance */
            case R.id.settings_activity__theme_colors:
                getFragmentManager().beginTransaction()
                        .addToBackStack(null)
                        .replace(R.id.settings__fragment_container, new SettingsFragment__ThemeColors(), SettingsFragment__ThemeColors.TAG).commit();
                break;
            case R.id.settings_activity__navigation_slider:
                getFragmentManager().beginTransaction().addToBackStack(null)
                        .replace(R.id.settings__fragment_container, new SettingsFragment__NavigationSlider(), SettingsFragment__NavigationSlider.TAG).commit();
                break;
            case R.id.settings_activity__font_size:
                showFontSizeDialog();
                break;
            //case R.id.settings_activity__intellihide_toolbars:
            //case R.id.settings_activity__intellihide_toolbars_checkbox:
            //    checkboxIntellihide.setChecked(!getAppSettings().isIntellihideToolbars());
            //    getAppSettings().setIntellihideToolbars(!getAppSettings().isIntellihideToolbars());
            //    break;
            case R.id.settings_activity__extended_notifications:
            case R.id.settings_activity__extended_notifications_checkbox:
                checkboxExtendedNotifications.setChecked(!getAppSettings().isExtendedNotifications());
                getAppSettings().setExtendedNotifications(!getAppSettings().isExtendedNotifications());
                break;
            case R.id.settings_activity__append_shared_via_app:
            case R.id.settings_activity__append_shared_via_app_checkbox:
                checkboxAppendSharedViaApp.setChecked(!getAppSettings().isAppendSharedViaApp());
                getAppSettings().setAppendSharedViaApp(!getAppSettings().isAppendSharedViaApp());
                break;
            case R.id.settings_activity__chrome_custom_tabs:
            case R.id.settings_activity__chrome_custom_tabs_checkbox:
                checkboxCustomTabs.setChecked(!getAppSettings().isChromeCustomTabsEnabled());
                getAppSettings().setChromeCustomTabsEnabled(!getAppSettings().isChromeCustomTabsEnabled());
                break;
            /** Pod Settings */
            case R.id.settings_activity__personal_settings: {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.setAction(MainActivity.ACTION_OPEN_URL);
                intent.putExtra(MainActivity.URL_MESSAGE, urls.getPersonalSettingsUrl());
                startActivity(intent);
                getActivity().finish();
                break;
            }
            case R.id.settings_activity__manage_tags: {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.setAction(MainActivity.ACTION_OPEN_URL);
                intent.putExtra(MainActivity.URL_MESSAGE, urls.getManageTagsUrl());
                startActivity(intent);
                getActivity().finish();
                break;
            }
            case R.id.settings_activity__manage_contacts: {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.setAction(MainActivity.ACTION_OPEN_URL);
                intent.putExtra(MainActivity.URL_MESSAGE, urls.getManageContactsUrl());
                startActivity(intent);
                getActivity().finish();
                break;
            }
            case R.id.settings_activity__change_account: {
                new android.app.AlertDialog.Builder(getActivity())
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
            }
            /** Network */
            case R.id.settings_activity__load_images:
            case R.id.settings_activity__load_images_checkbox:
                checkboxLoadImages.setChecked(!getAppSettings().isLoadImages());
                getAppSettings().setLoadImages(!getAppSettings().isLoadImages());
                break;
            case R.id.settings_activity__clear_cache: {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.setAction(MainActivity.ACTION_CLEAR_CACHE);
                getActivity().sendBroadcast(intent);
                getActivity().finish();
                break;
            }
            case R.id.settings_activity__proxy_settings:
                getFragmentManager().beginTransaction().addToBackStack(null)
                        .replace(R.id.settings__fragment_container, new SettingsFragment__Proxy(), SettingsFragment__Proxy.TAG).commit();
                break;
            /** More */
            case R.id.settings_activity__debugging:
                getFragmentManager().beginTransaction().addToBackStack(null)
                        .replace(R.id.settings__fragment_container, new SettingsFragment__Debugging(), SettingsFragment__Debugging.TAG).commit();
                break;
        }
    }
}
