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
package com.github.dfa.diaspora_android.data;

import com.github.dfa.diaspora_android.App;
import com.github.dfa.diaspora_android.util.AppSettings;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

public class DiasporaAspect {
    public long id;
    public String name;
    public boolean selected;

    public DiasporaAspect(long id, String name, boolean selected) {
        this.id = id;
        this.name = name;
        this.selected = selected;
    }


    public DiasporaAspect(String shareabletext) {
        // fromShareAbleText
        String[] str = shareabletext.split("%");
        selected = Integer.parseInt(str[0]) == 1;
        id = Long.parseLong(str[1]);
        name = shareabletext.substring(shareabletext.indexOf(str[1]) + str[1].length() + 1);
    }

    public DiasporaAspect(JSONObject json) throws JSONException {
        if (json.has("id")) {
            id = json.getLong("id");
        }
        if (json.has("name")) {
            name = json.getString("name");
        }
        if (json.has("selected")) {
            selected = json.getBoolean("selected");
        }
    }

    public String toJsonString() {
        JSONObject j = new JSONObject();
        try {
            j.put("id", id);
            j.put("name", name);
            j.put("selected", selected);
        } catch (JSONException e) {/*Nothing*/}
        return j.toString();
    }

    public String toHtmlLink(final App app) {
        final AppSettings appSettings = app.getSettings();
        return String.format(Locale.getDefault(),
                "<a href='%s/aspects?a_ids[]=%d' style='color: #000000; text-decoration: none;'>%s</a>",
                appSettings.getPod().getPodUrl().getBaseUrl(), id, name);
    }

    @Override
    public String toString() {
        return toShareAbleText();
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof DiasporaAspect && ((DiasporaAspect) o).id == id;
    }

    public String toShareAbleText() {
        return String.format(Locale.getDefault(), "%d%%%d%%%s", selected ? 1 : 0, id, name);
    }
}
