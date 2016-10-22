package com.github.dfa.diaspora_android.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;

import com.github.dfa.diaspora_android.data.AppSettings;
import com.github.dfa.diaspora_android.util.theming.ThemeHelper;

/**
 * Themed DialogFragment
 * Created by vanitas on 22.10.16.
 */

public abstract class ThemedAppCompatDialogFragment extends AppCompatDialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        ThemeHelper.getInstance(getAppSettings());
        return dialog;
    }

    protected abstract void applyColorsToViews();

    protected abstract AppSettings getAppSettings();
}
