package com.github.dfa.diaspora_android.ui.theme;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.support.v7.app.AlertDialog;

import com.github.dfa.diaspora_android.util.AppSettings;

/**
 * AlertDialog Builder that colors its buttons
 * Created by vanitas on 06.11.16.
 */

public class ThemedAlertDialogBuilder extends AlertDialog.Builder {
    protected AppSettings appSettings;

    public ThemedAlertDialogBuilder(@NonNull Context context, AppSettings appSettings) {
        super(context);
        this.appSettings = appSettings;
    }

    public ThemedAlertDialogBuilder(@NonNull Context context, @StyleRes int themeResId, AppSettings appSettings) {
        super(context, themeResId);
        this.appSettings = appSettings;
    }

    @Override
    public AlertDialog create() {
        final AlertDialog dialog = super.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                applyColors(dialog);
            }
        });
        return dialog;
    }

    private void applyColors(AlertDialog alertDialog) {
        ThemeHelper.getInstance(appSettings);
        ThemeHelper.updateAlertDialogColor(alertDialog);
    }
}
