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

import android.os.Bundle;
import android.view.View;

/**
 * Abstract class of a SettingsFragment that is themed and listens for clicks on views
 * Created by vanitas on 16.10.16.
 */

public abstract class ThemedSettingsFragment extends ThemedFragment implements View.OnClickListener {

    @Override
    public void onViewCreated(View layout, Bundle savedInstanceState) {
        super.onViewCreated(layout, savedInstanceState);
        applyColorToViews();
        applySettingsToViews();
        setOnClickListenersOnViews();
    }

    /**
     * Apply current settings to views (like checked checkboxes...)
     */
    protected abstract void applySettingsToViews();

    /**
     * Set the onClickListener (normally this) on views.
     */
    protected abstract void setOnClickListenersOnViews();
}
