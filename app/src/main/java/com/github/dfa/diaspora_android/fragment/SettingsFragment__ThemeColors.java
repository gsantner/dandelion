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

import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.dfa.diaspora_android.App;
import com.github.dfa.diaspora_android.R;
import com.github.dfa.diaspora_android.activity.ThemedActivity;
import com.github.dfa.diaspora_android.data.AppSettings;
import com.github.dfa.diaspora_android.util.AppLog;
import com.github.dfa.diaspora_android.util.theming.ColorPalette;
import com.github.dfa.diaspora_android.util.theming.ThemeHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import uz.shift.colorpicker.LineColorPicker;
import uz.shift.colorpicker.OnColorChangedListener;

/**
 * SettingsFragment that contains Theme and Color related settings
 * Created by vanitas on 15.10.16.
 */

public class SettingsFragment__ThemeColors extends ThemedSettingsFragment {
    public static final String TAG = "com.github.dfa.diaspora_android.SettingsFragment__ThemeColors";

    @BindView(R.id.settings_activity__header_appearance__theme_and_colors)
    protected TextView titleThemeColors;

    @BindView(R.id.settings_activity__theme_colors__primary_color)
    protected RelativeLayout optionPrimaryColor;

    @BindView(R.id.settings_activity__theme_colors__primary_color__preview)
    protected ImageView previewPrimaryColor;

    @BindView(R.id.settings_activity__theme_colors__accent_color)
    protected RelativeLayout optionAccentColor;

    @BindView(R.id.settings_activity__theme_colors__accent_color__preview)
    protected ImageView previewAccentColor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        AppLog.d(this, "onCreateView()");
        View layout = inflater.inflate(R.layout.settings_activity__subsection_theming, container, false);
        ButterKnife.bind(this, layout);
        return layout;
    }

    protected void applySettingsToViews() {
        ThemeHelper.updatePrimaryColorPreview(previewPrimaryColor);
        ThemeHelper.updateAccentColorPreview(previewAccentColor);
    }

    protected void setOnClickListenersOnViews() {
        optionPrimaryColor.setOnClickListener(this);
        previewPrimaryColor.setOnClickListener(this);
        optionAccentColor.setOnClickListener(this);
        previewAccentColor.setOnClickListener(this);
    }

    @Override
    protected void applyColorToViews() {
        ThemeHelper.updateTitleColor(titleThemeColors);
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
            case R.id.settings_activity__theme_colors__primary_color:
            case R.id.settings_activity__theme_colors__primary_color__preview:
                showColorPickerDialog(1);
                break;
            case R.id.settings_activity__theme_colors__accent_color:
            case R.id.settings_activity__theme_colors__accent_color__preview:
                showColorPickerDialog(2);
                break;
        }
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
        View dialogLayout = inflater.inflate(R.layout.color_picker__dialog, null);
        final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(context);
        builder.setView(dialogLayout);

        final FrameLayout titleBackground = (FrameLayout) dialogLayout.findViewById(R.id.color_picker_dialog__title_background);
        final TextView title = (TextView) dialogLayout.findViewById(R.id.color_picker_dialog__title);
        final LineColorPicker base = (LineColorPicker) dialogLayout.findViewById(R.id.color_picker_dialog__base_picker);
        final LineColorPicker shade = (LineColorPicker) dialogLayout.findViewById(R.id.color_picker_dialog__shade_picker);

        title.setText(type == 1 ? R.string.pref_title__primary_color : R.string.pref_title__accent_color);
        title.setTextColor(getResources().getColor(R.color.white));
        final int[] current = (type == 1 ? appSettings.getPrimaryColorPickerSettings() : appSettings.getAccentColorPickerSettings());
        base.setColors((type == 1 ? ColorPalette.getBaseColors(context) : ColorPalette.getAccentColors(context)));
        base.setSelectedColor(current[0]);
        shade.setColors(ColorPalette.getColors(context, current[0]));
        shade.setSelectedColor(current[1]);
        titleBackground.setBackgroundColor(shade.getColor());
        base.setOnColorChangedListener(new OnColorChangedListener() {
            @Override
            public void onColorChanged(int i) {
                AppLog.d(this, "Selected Base color changed: " + i);
                shade.setColors(ColorPalette.getColors(context, i));
                titleBackground.setBackgroundColor(i);
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
            }
        });

        //Build dialog
        builder
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (type == 1) {
                            appSettings.setPrimaryColorPickerSettings(base.getColor(), shade.getColor());
                            if (Build.VERSION.SDK_INT >= 21) {
                                getActivity().getWindow().setStatusBarColor(ThemeHelper.getPrimaryDarkColor());
                            }
                            ((ThemedActivity) getActivity()).applyColorToViews();
                        } else {
                            appSettings.setAccentColorPickerSettings(base.getColor(), shade.getColor());
                        }
                        applyColorToViews();
                        applySettingsToViews();
                    }
                }).show();
    }
}
