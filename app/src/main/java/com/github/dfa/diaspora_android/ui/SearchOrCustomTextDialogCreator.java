package com.github.dfa.diaspora_android.ui;

import android.app.Activity;
import android.support.v4.content.ContextCompat;

import com.github.dfa.diaspora_android.R;
import com.github.dfa.diaspora_android.data.DiasporaAspect;
import com.github.dfa.diaspora_android.util.AppSettings;

import net.gsantner.opoc.ui.SearchOrCustomTextDialog;
import net.gsantner.opoc.util.Callback;

import java.util.ArrayList;
import java.util.Arrays;

public class SearchOrCustomTextDialogCreator {
    public static final String SPECIAL_PREFIX = "\uD83D\uDCA0";

    public static void showDiasporaTagsDialog(final Activity activity, final Callback.a1<String> callback) {
        SearchOrCustomTextDialog.DialogOptions dopt = new SearchOrCustomTextDialog.DialogOptions();
        baseConf(activity, dopt);
        dopt.callback = callback;
        dopt.isSearchEnabled = true;
        dopt.searchHintText = R.string.search;
        dopt.titleText = R.string.tags;

        new Thread(() -> {
            AppSettings appSettings = AppSettings.get();
            ArrayList<String> hl = new ArrayList<>();
            ArrayList<String> data = new ArrayList<>(Arrays.asList(appSettings.getFollowedTags()));
            if (data.size() > 0) {
                String highlighted = surroundString(data.remove(0));
                data.add(0, highlighted);
                hl.add(highlighted);
            }

            for (int strid : new int[]{R.string.pref_title__manage_tags}) {
                String special = surroundString(appSettings.rstr(strid));
                data.add(0, special);
                hl.add(special);
            }
            dopt.data = data;
            dopt.highlightData = hl;
            activity.runOnUiThread(() -> SearchOrCustomTextDialog.showMultiChoiceDialogWithSearchFilterUI(activity, dopt));
        }).start();
    }

    private static String surroundString(String text) {
        return SPECIAL_PREFIX + " " + text + " ";
    }


    public static void showDiasporaAspectsDialog(final Activity activity, final Callback.a1<String> callback) {
        SearchOrCustomTextDialog.DialogOptions dopt = new SearchOrCustomTextDialog.DialogOptions();
        baseConf(activity, dopt);
        dopt.callback = callback;
        dopt.isSearchEnabled = false;
        dopt.titleText = R.string.contacts;

        new Thread(() -> {
            AppSettings appSettings = AppSettings.get();
            ArrayList<String> hl = new ArrayList<>();
            ArrayList<String> data = new ArrayList<>();
            for (DiasporaAspect aspect : AppSettings.get().getAspects()) {
                data.add(aspect.name);
            }
            for (int strid : new int[]{R.string.nav_profile, R.string.pref_desc__manage_contacts}) {
                String special = surroundString(appSettings.rstr(strid));
                data.add(0, special);
                hl.add(special);
            }
            dopt.data = data;
            dopt.highlightData = hl;
            activity.runOnUiThread(() -> SearchOrCustomTextDialog.showMultiChoiceDialogWithSearchFilterUI(activity, dopt));
        }).start();
    }


    private static void baseConf(Activity activity, SearchOrCustomTextDialog.DialogOptions dopt) {
        AppSettings as = new AppSettings(activity);
        dopt.isDarkDialog = as.isAmoledColorMode();
        dopt.textColor = ContextCompat.getColor(activity, dopt.isDarkDialog ? R.color.white : R.color.primary_text);
        dopt.highlightColor = as.getAccentColor();
    }
}