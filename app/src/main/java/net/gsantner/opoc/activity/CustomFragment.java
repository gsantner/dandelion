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
package net.gsantner.opoc.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;

/**
 * Customized abstract Fragment class with some useful methods
 * Created by vanitas on 21.09.16.
 */

public abstract class CustomFragment extends Fragment {

    public static final String TAG = "net.gsantner.opoc.activity.CustomFragment";

    /**
     * We have an optionsMenu
     *
     * @param savedInstanceState state
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    /**
     * Return the tag used to identify the Fragment.
     *
     * @return tag
     */
    public abstract String getFragmentTag();

    /**
     * Return true if the fragment reacted to a back button press, false else.
     * In case the fragment returned false, the parent activity should handle the backPress.
     *
     * @return did we react to the back press?
     */
    public abstract boolean onBackPressed();
}

