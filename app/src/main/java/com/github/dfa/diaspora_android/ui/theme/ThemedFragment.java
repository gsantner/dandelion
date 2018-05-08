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
package com.github.dfa.diaspora_android.ui.theme;

import com.github.dfa.diaspora_android.App;
import com.github.dfa.diaspora_android.util.AppSettings;

import net.gsantner.opoc.activity.GsFragmentBase;

/**
 * Fragment that supports color schemes
 * Created by vanitas on 06.10.16.
 */

public abstract class ThemedFragment extends GsFragmentBase {
    protected AppSettings getAppSettings() {
        return ((App) getActivity().getApplication()).getSettings();
    }

    protected abstract void applyColorToViews();

    @Override
    public void onResume() {
        super.onResume();
        ThemeHelper.getInstance(getAppSettings());
        applyColorToViews();
    }


    public boolean isAllowedIntellihide() {
        return true;
    }
}
