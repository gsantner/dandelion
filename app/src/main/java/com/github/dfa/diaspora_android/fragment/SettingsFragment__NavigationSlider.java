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
 * SettingsFragment that controls the visibility of different NavigationSlider items
 * Created by vanitas on 16.10.16.
 */

public class SettingsFragment__NavigationSlider extends ThemedSettingsFragment {
    public static final String TAG = "com.github.dfa.diaspora_android.SettingsFragment__NavigationSlider";

    @BindView(R.id.settings_activity__header_appearance__nav_slider)
    protected TextView titleNavSlider;

    @BindView(R.id.settings_activity__navigation_slider_about)
    protected RelativeLayout optionAbout;

    @BindView(R.id.settings_activity__navigation_slider_activities)
    protected RelativeLayout optionActivities;

    @BindView(R.id.settings_activity__navigation_slider_aspects)
    protected RelativeLayout optionAspects;

    @BindView(R.id.settings_activity__navigation_slider_commented)
    protected RelativeLayout optionCommented;

    @BindView(R.id.settings_activity__navigation_slider_exit)
    protected RelativeLayout optionExit;

    @BindView(R.id.settings_activity__navigation_slider_followed)
    protected RelativeLayout optionFollowed;

    @BindView(R.id.settings_activity__navigation_slider_liked)
    protected RelativeLayout optionLiked;

    @BindView(R.id.settings_activity__navigation_slider_mentions)
    protected RelativeLayout optionMentions;

    @BindView(R.id.settings_activity__navigation_slider_profile)
    protected RelativeLayout optionProfile;

    @BindView(R.id.settings_activity__navigation_slider_public)
    protected RelativeLayout optionPublic;

    @BindView(R.id.settings_activity__navigation_slider_about_checkbox)
    protected CheckBox checkboxAbout;

    @BindView(R.id.settings_activity__navigation_slider_activities_checkbox)
    protected CheckBox checkboxActivities;

    @BindView(R.id.settings_activity__navigation_slider_aspects_checkbox)
    protected CheckBox checkboxAspects;

    @BindView(R.id.settings_activity__navigation_slider_commented_checkbox)
    protected CheckBox checkboxCommented;

    @BindView(R.id.settings_activity__navigation_slider_exit_checkbox)
    protected CheckBox checkboxExit;

    @BindView(R.id.settings_activity__navigation_slider_followed_checkbox)
    protected CheckBox checkboxFollowed;

    @BindView(R.id.settings_activity__navigation_slider_liked_checkbox)
    protected CheckBox checkboxLiked;

    @BindView(R.id.settings_activity__navigation_slider_mentions_checkbox)
    protected CheckBox checkboxMentions;

    @BindView(R.id.settings_activity__navigation_slider_profile_checkbox)
    protected CheckBox checkboxProfile;

    @BindView(R.id.settings_activity__navigation_slider_public_checkbox)
    protected CheckBox checkboxPublic;





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        AppLog.d(this, "onCreateView()");
        View layout = inflater.inflate(R.layout.settings_activity__subsection_nav_slider, container, false);
        ButterKnife.bind(this, layout);
        return layout;
    }

    @Override
    protected void applyColorToViews() {
        ThemeHelper.updateTitleColor(titleNavSlider);
        ThemeHelper.updateCheckBoxColor(checkboxAbout);
        ThemeHelper.updateCheckBoxColor(checkboxActivities);
        ThemeHelper.updateCheckBoxColor(checkboxAspects);
        ThemeHelper.updateCheckBoxColor(checkboxCommented);
        ThemeHelper.updateCheckBoxColor(checkboxExit);
        ThemeHelper.updateCheckBoxColor(checkboxFollowed);
        ThemeHelper.updateCheckBoxColor(checkboxLiked);
        ThemeHelper.updateCheckBoxColor(checkboxMentions);
        ThemeHelper.updateCheckBoxColor(checkboxProfile);
        ThemeHelper.updateCheckBoxColor(checkboxPublic);
    }

    @Override
    protected void applySettingsToViews() {
        checkboxAbout.setChecked(getAppSettings().isVisibleInNavHelp_license());
        checkboxActivities.setChecked(getAppSettings().isVisibleInNavActivities());
        checkboxAspects.setChecked(getAppSettings().isVisibleInNavAspects());
        checkboxCommented.setChecked(getAppSettings().isVisibleInNavCommented());
        checkboxExit.setChecked(getAppSettings().isVisibleInNavExit());
        checkboxFollowed.setChecked(getAppSettings().isVisibleInNavFollowed_tags());
        checkboxLiked.setChecked(getAppSettings().isVisibleInNavLiked());
        checkboxMentions.setChecked(getAppSettings().isVisibleInNavMentions());
        checkboxProfile.setChecked(getAppSettings().isVisibleInNavProfile());
        checkboxPublic.setChecked(getAppSettings().isVisibleInNavPublic_activities());
    }

    @Override
    protected void setOnClickListenersOnViews() {
        checkboxAbout.setOnClickListener(this);
        checkboxActivities.setOnClickListener(this);
        checkboxAspects.setOnClickListener(this);
        checkboxCommented.setOnClickListener(this);
        checkboxExit.setOnClickListener(this);
        checkboxFollowed.setOnClickListener(this);
        checkboxLiked.setOnClickListener(this);
        checkboxMentions.setOnClickListener(this);
        checkboxProfile.setOnClickListener(this);
        checkboxPublic.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.settings_activity__navigation_slider_about:
            case R.id.settings_activity__navigation_slider_about_checkbox:
                checkboxAbout.setChecked(!getAppSettings().isVisibleInNavHelp_license());
                getAppSettings().setVisibleInNavHelp_license(!getAppSettings().isVisibleInNavHelp_license());
                break;
            case R.id.settings_activity__navigation_slider_activities:
            case R.id.settings_activity__navigation_slider_activities_checkbox:
                checkboxActivities.setChecked(!getAppSettings().isVisibleInNavActivities());
                getAppSettings().setVisibleInNavActivities(!getAppSettings().isVisibleInNavActivities());
                break;
            case R.id.settings_activity__navigation_slider_aspects:
            case R.id.settings_activity__navigation_slider_aspects_checkbox:
                checkboxAspects.setChecked(!getAppSettings().isVisibleInNavAspects());
                getAppSettings().setVisibleInNavAspects(!getAppSettings().isVisibleInNavAspects());
                break;
            case R.id.settings_activity__navigation_slider_commented:
            case R.id.settings_activity__navigation_slider_commented_checkbox:
                checkboxCommented.setChecked(!getAppSettings().isVisibleInNavCommented());
                getAppSettings().setVisibleInNavCommented(!getAppSettings().isVisibleInNavCommented());
                break;
            case R.id.settings_activity__navigation_slider_exit:
            case R.id.settings_activity__navigation_slider_exit_checkbox:
                checkboxExit.setChecked(!getAppSettings().isVisibleInNavExit());
                getAppSettings().setVisibleInNavExit(!getAppSettings().isVisibleInNavExit());
                break;
            case R.id.settings_activity__navigation_slider_followed:
            case R.id.settings_activity__navigation_slider_followed_checkbox:
                checkboxFollowed.setChecked(!getAppSettings().isVisibleInNavFollowed_tags());
                getAppSettings().setVisibleInNavFollowedTags(!getAppSettings().isVisibleInNavFollowed_tags());
                break;
            case R.id.settings_activity__navigation_slider_liked:
            case R.id.settings_activity__navigation_slider_liked_checkbox:
                checkboxLiked.setChecked(!getAppSettings().isVisibleInNavLiked());
                getAppSettings().setVisibleInNavLiked(!getAppSettings().isVisibleInNavLiked());
                break;
            case R.id.settings_activity__navigation_slider_mentions:
            case R.id.settings_activity__navigation_slider_mentions_checkbox:
                checkboxMentions.setChecked(!getAppSettings().isVisibleInNavMentions());
                getAppSettings().setVisibleInNavMentions(!getAppSettings().isVisibleInNavMentions());
                break;
            case R.id.settings_activity__navigation_slider_profile:
            case R.id.settings_activity__navigation_slider_profile_checkbox:
                checkboxProfile.setChecked(!getAppSettings().isVisibleInNavProfile());
                getAppSettings().setVisibleInNavProfile(!getAppSettings().isVisibleInNavProfile());
                break;
            case R.id.settings_activity__navigation_slider_public:
            case R.id.settings_activity__navigation_slider_public_checkbox:
                checkboxPublic.setChecked(!getAppSettings().isVisibleInNavPublic_activities());
                getAppSettings().setVisibleInNavPublic_activities(!getAppSettings().isVisibleInNavPublic_activities());
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
