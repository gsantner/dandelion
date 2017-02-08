/*
    This file is part of the dandelion*.

    dandelion* is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    dandelion* is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with the dandelion*.

    If not, see <http://www.gnu.org/licenses/>.
 */
package com.github.dfa.diaspora_android.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.github.dfa.diaspora_android.App;
import com.github.dfa.diaspora_android.R;
import com.github.dfa.diaspora_android.activity.MainActivity;
import com.github.dfa.diaspora_android.util.AppLog;
import com.github.dfa.diaspora_android.util.AppSettings;
import com.github.dfa.diaspora_android.util.DiasporaUrlHelper;

/**
 * BroadcastReceiver used to update the title of the MainActivity depending on the url of the ui__webview
 * Created by vanitas on 11.09.16.
 */
public class UpdateTitleReceiver extends BroadcastReceiver {
    private DiasporaUrlHelper urls;
    private AppSettings appSettings;
    private App app;
    private TitleCallback callback;
    private String lastUrl;

    public UpdateTitleReceiver(App app, DiasporaUrlHelper urls, TitleCallback callback) {
        this.urls = urls;
        this.app = app;
        this.appSettings = app.getSettings();
        this.callback = callback;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        lastUrl = intent.getStringExtra(MainActivity.EXTRA_URL);
        if (lastUrl != null && lastUrl.startsWith(urls.getPodUrl())) {
            String subUrl = lastUrl.substring((urls.getPodUrl()).length());
            AppLog.spam(this, "onReceive()- Set title for subUrl " + subUrl);
            if (subUrl.startsWith(DiasporaUrlHelper.SUBURL_STREAM)) {
                setTitle(R.string.nav_stream);
            } else if (subUrl.startsWith(DiasporaUrlHelper.SUBURL_POSTS)) {
                setTitle(R.string.app_name);
            } else if (subUrl.startsWith(DiasporaUrlHelper.SUBURL_NOTIFICATIONS)) {
                setTitle(R.string.notifications);
            } else if (subUrl.startsWith(DiasporaUrlHelper.SUBURL_CONVERSATIONS)) {
                setTitle(R.string.conversations);
            } else if (subUrl.startsWith(DiasporaUrlHelper.SUBURL_NEW_POST)) {
                setTitle(R.string.new_post);
            } else if (subUrl.startsWith(DiasporaUrlHelper.SUBURL_PEOPLE + appSettings.getProfileId())) {
                setTitle(R.string.nav_profile);
            } else if (subUrl.startsWith(DiasporaUrlHelper.SUBURL_ACTIVITY)) {
                setTitle(R.string.nav_activities);
            } else if (subUrl.startsWith(DiasporaUrlHelper.SUBURL_LIKED)) {
                setTitle(R.string.nav_liked);
            } else if (subUrl.startsWith(DiasporaUrlHelper.SUBURL_COMMENTED)) {
                setTitle(R.string.nav_commented);
            } else if (subUrl.startsWith(DiasporaUrlHelper.SUBURL_MENTIONS)) {
                setTitle(R.string.nav_mentions);
            } else if (subUrl.startsWith(DiasporaUrlHelper.SUBURL_PUBLIC)) {
                setTitle(R.string.public_);
            } else if (urls.isAspectUrl(lastUrl)) {
                setTitle(urls.getAspectNameFromUrl(lastUrl, app));
            }
        } else {
            AppLog.spam(this, "onReceive()- Invalid url: " + lastUrl);
        }
    }

    private void setTitle(int rId) {
        callback.setTitle(lastUrl, rId);
    }

    private void setTitle(String title) {
        callback.setTitle(lastUrl, title);
    }

    public interface TitleCallback {
        void setTitle(String url, int resId);

        void setTitle(String url, String title);
    }
}
