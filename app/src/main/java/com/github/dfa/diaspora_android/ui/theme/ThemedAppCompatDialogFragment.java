package com.github.dfa.diaspora_android.ui.theme;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatDialogFragment;

import com.github.dfa.diaspora_android.util.AppSettings;

/**
 * Themed DialogFragment
 * Created by vanitas on 22.10.16.
 */

public abstract class ThemedAppCompatDialogFragment extends AppCompatDialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        ThemeHelper.getInstance(getAppSettings());
        return dialog;
    }

    protected abstract void applyColorsToViews();

    protected abstract AppSettings getAppSettings();
}
