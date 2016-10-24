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

    This file is inspired from sourabhsoni.com/implementing-hashtags-in-android-application/
 */
package com.github.dfa.diaspora_android.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;

public class HashtagProvider extends ContentProvider {

    @Override
    public int delete(@NonNull Uri arg0, String arg1, String[] arg2) {
        return 0;
    }

    @Override
    public String getType(@NonNull Uri arg0) {
        return "vnd.android.cursor.item/vnd.cc.tag";
    }

    @Override
    public Uri insert(@NonNull Uri arg0, ContentValues arg1) {
        return null;
    }

    @Override
    public boolean onCreate() {
        return false;
    }

    @Override
    public Cursor query(@NonNull Uri arg0, String[] arg1, String arg2, String[] arg3,
                        String arg4) {
        return null;
    }

    @Override
    public int update(@NonNull Uri arg0, ContentValues arg1, String arg2, String[] arg3) {
        return 0;
    }
}