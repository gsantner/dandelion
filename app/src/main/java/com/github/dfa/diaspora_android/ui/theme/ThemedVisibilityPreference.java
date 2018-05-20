package com.github.dfa.diaspora_android.ui.theme;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.CheckBox;

import com.github.dfa.diaspora_android.R;
import com.github.dfa.diaspora_android.util.AppSettings;

/**
 * ThemedCheckBoxPreference with visibility icons instead of checkbox. TODO: Make more flexible?
 * Created by vanitas on 25.10.16.
 */

public class ThemedVisibilityPreference extends ThemedCheckBoxPreference {
    public ThemedVisibilityPreference(Context context) {
        super(context);
    }

    public ThemedVisibilityPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ThemedVisibilityPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setColors() {
        CheckBox checkBox = rootLayout.findViewById(android.R.id.checkbox);
        checkBox.setButtonDrawable(R.drawable.ic_visibility_selector);
        ThemeHelper.getInstance(AppSettings.get());
        ThemeHelper.updateCheckBoxColor(checkBox);
    }
}
