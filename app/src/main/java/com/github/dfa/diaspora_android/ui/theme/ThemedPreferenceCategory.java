package com.github.dfa.diaspora_android.ui.theme;

import android.content.Context;
import android.preference.PreferenceCategory;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.dfa.diaspora_android.util.AppSettings;

/**
 * PreferenceCategory with a colored title
 * Created by vanitas on 24.10.16.
 */

public class ThemedPreferenceCategory extends PreferenceCategory implements Themeable {
    protected TextView titleTextView;

    @SuppressWarnings("unused")
    public ThemedPreferenceCategory(Context context) {
        super(context);
    }

    @SuppressWarnings("unused")
    public ThemedPreferenceCategory(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @SuppressWarnings("unused")
    public ThemedPreferenceCategory(Context context, AttributeSet attrs,
                                    int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected View onCreateView(ViewGroup parent) {
        View rootLayout = super.onCreateView(parent);
        this.titleTextView = rootLayout.findViewById(android.R.id.title);
        setColors();
        return rootLayout;
    }

    @Override
    public void setColors() {
        if (titleTextView != null) {
            ThemeHelper.getInstance(AppSettings.get());
            ThemeHelper.updateTextViewTextColor(titleTextView);
        }
    }
}
