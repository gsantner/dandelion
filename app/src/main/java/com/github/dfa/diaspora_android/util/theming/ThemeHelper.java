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
    This class is inspired by org.horasapps.LeafPic
 */
package com.github.dfa.diaspora_android.util.theming;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.CompoundButtonCompat;
import android.support.v7.widget.ActionMenuView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.github.dfa.diaspora_android.R;
import com.github.dfa.diaspora_android.data.AppSettings;

/**
 * Singleton that can be used to color views
 * Created by vanitas on 06.10.16.
 */

public class ThemeHelper {
    private AppSettings appSettings;
    private static ThemeHelper instance;

    private ThemeHelper(AppSettings appSettings) {
        this.appSettings = appSettings;
    }

    public static ThemeHelper getInstance(AppSettings appSettings) {
        if (instance == null) {
            instance = new ThemeHelper(appSettings);
        }
        return instance;
    }

    public static ThemeHelper getInstance() {
        if (instance == null)
            throw new IllegalStateException("ThemeHelper must be initialized using getInstance(AppSettings) before it can be used!");
        return instance;
    }

    public static void updateEditTextColor(EditText editText) {
        if (editText != null) {
            editText.setHighlightColor(getInstance().appSettings.getAccentColor());
            if(Build.VERSION.SDK_INT >= 21) {
                editText.getBackground().mutate().setColorFilter(getAccentColor(), PorterDuff.Mode.SRC_ATOP);
            }
        }
    }

    public static void updateCheckBoxColor(CheckBox checkBox) {
        if (checkBox != null) {
            int states[][] = {{android.R.attr.state_checked}, {}};
            int colors[] = {ThemeHelper.getAccentColor(), getNeutralGreyColor()};
            CompoundButtonCompat.setButtonTintList(checkBox, new ColorStateList(states, colors));
        }
    }

    public static void updateTabLayoutColor(TabLayout tabLayout) {
        if (tabLayout != null) {
            tabLayout.setBackgroundColor(getInstance().appSettings.getPrimaryColor());
            tabLayout.setSelectedTabIndicatorColor(getInstance().appSettings.getAccentColor());
        }
    }

    public static void updateTextViewColor(TextView textView) {
        if (textView != null) {
            textView.setHighlightColor(getInstance().appSettings.getAccentColor());
            textView.setLinkTextColor(getInstance().appSettings.getAccentColor());
        } else {
            Log.d("ThemeHelper", "TextView is null!");
        }
    }

    public static void updateTitleColor(TextView textView) {
        if(textView != null) {
            textView.setTextColor(getInstance().appSettings.getAccentColor());
        }
    }

    public static void updateToolbarColor(Toolbar toolbar) {
        if (toolbar != null) {
            toolbar.setBackgroundColor(getInstance().appSettings.getPrimaryColor());
        }
    }

    public static void setToolbarColor(Toolbar toolbar, int color) {
        if(toolbar != null) {
            toolbar.setBackgroundColor(color);
        }
    }

    public static void updateActionMenuViewColor(ActionMenuView actionMenuView) {
        if (actionMenuView != null) {
            actionMenuView.setBackgroundColor(getInstance().appSettings.getPrimaryColor());
        }
    }

    public static int getPrimaryColor() {
        return getInstance().appSettings.getPrimaryColor();
    }

    public static int getAccentColor() {
        return getInstance().appSettings.getAccentColor();
    }

    public static void setPrimaryColorAsBackground(View view) {
        if (view != null) {
            view.setBackgroundColor(getPrimaryColor());
        }
    }

    public static int getPrimaryDarkColor() {
        return ColorPalette.getObscuredColor(getPrimaryColor());
    }

    public static int getNeutralGreyColor() {
        return ContextCompat.getColor(getInstance().appSettings.getApplicationContext(), R.color.md_grey_800);
    }

    public static void updateProgressBarColor(ProgressBar progressBar) {
        if (progressBar != null && progressBar.getProgressDrawable() != null) {
            progressBar.getProgressDrawable().setColorFilter(getAccentColor(), PorterDuff.Mode.SRC_IN);
        }
    }

    public static void updateAccentColorPreview(ImageView imageView) {
        if (imageView != null) {
            Drawable circle = imageView.getDrawable();
            if (circle != null) {
                circle.setColorFilter(getAccentColor(), PorterDuff.Mode.SRC_ATOP);
                imageView.setImageDrawable(circle);
            }
        }
    }

    public static void updateRadioGroupColor(RadioGroup radioGroup) {
        if(radioGroup != null && Build.VERSION.SDK_INT >= 21) {
            for (int i = 0; i < radioGroup.getChildCount(); ++i) {
                RadioButton btn = ((RadioButton) radioGroup.getChildAt(i));
                btn.setButtonTintList(new ColorStateList(
                        new int[][]{ new int[]{-android.R.attr.state_enabled}, new int[]{android.R.attr.state_enabled} },
                        new int[] { Color.BLACK ,ThemeHelper.getAccentColor() }));
                btn.invalidate();
            }
        }
    }

    public static void updatePrimaryColorPreview(ImageView imageView) {
        if(imageView != null) {
            Drawable circle = imageView.getDrawable();
            if(circle != null) {
                circle.setColorFilter(getPrimaryColor(), PorterDuff.Mode.SRC_ATOP);
            }
        }
    }

    public static void setViewEnabled(View v, boolean enabled) {
        v.setBackgroundColor(getInstance().appSettings.getApplicationContext().getResources().getColor(enabled ? R.color.white : R.color.layout_disabled));
    }
}
