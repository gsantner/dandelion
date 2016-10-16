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
package com.github.dfa.diaspora_android.activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.github.dfa.diaspora_android.R;
import com.github.dfa.diaspora_android.fragment.SettingsFragment__Overview;
import com.github.dfa.diaspora_android.util.AppLog;
import com.github.dfa.diaspora_android.util.ProxyHandler;
import com.github.dfa.diaspora_android.util.theming.ThemeHelper;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * SettingsActivity
 * Created by vanitas on 15.10.16.
 */

public class SettingsActivity extends ThemedActivity {

    //Toolbar
    @BindView(R.id.settings__appbar)
    protected AppBarLayout appBarLayout;

    @BindView(R.id.settings__toolbar)
    protected Toolbar toolbar;

    private ProxyHandler.ProxySettings oldProxySettings;

    public void onCreate(Bundle b) {
        super.onCreate(b);
        this.setContentView(R.layout.settings_activity);
        ButterKnife.bind(this);
        toolbar.setTitle(R.string.settings);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_white_24px));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingsActivity.this.onBackPressed();
            }
        });
        oldProxySettings = getAppSettings().getProxySettings();
        getSupportFragmentManager().beginTransaction().replace(
                R.id.settings__fragment_container, new SettingsFragment__Overview(), SettingsFragment__Overview.TAG).commit();
    }

    @Override
    public void applyColorToViews() {
        //Toolbar
        ThemeHelper.updateToolbarColor(toolbar);
    }

    @Override
    protected void onStop() {
        ProxyHandler.ProxySettings newProxySettings = getAppSettings().getProxySettings();
        if (!oldProxySettings.equals(newProxySettings)) {
            AppLog.d(this, "ProxySettings changed.");
            //Proxy on-off? => Restart app
            if (oldProxySettings.isEnabled() && !newProxySettings.isEnabled()) {
                Intent restartActivity = new Intent(SettingsActivity.this, MainActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(SettingsActivity.this, 12374, restartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
                AlarmManager mgr = (AlarmManager) SettingsActivity.this.getSystemService(Context.ALARM_SERVICE);
                mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, pendingIntent);
                System.exit(0);
            } //Proxy changed? => Update
            else {
                ProxyHandler.getInstance().updateProxySettings(this);
            }
        }
        super.onStop();
    }
}
