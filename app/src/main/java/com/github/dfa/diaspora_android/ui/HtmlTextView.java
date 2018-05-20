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
package com.github.dfa.diaspora_android.ui;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.text.Html;
import android.text.SpannableString;
import android.text.util.Linkify;
import android.util.AttributeSet;
import android.util.Patterns;

import com.github.dfa.diaspora_android.activity.MainActivity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * TextView, that renders HTML with highlited and clickable links and hashtags.
 * Links are opened in a webbrowser.
 * Hashtags open the MainActivity, load the new-post site of the selected pod and insert the
 * hashtag into the post editor. See data/HashtagContentProvider.
 */
public class HtmlTextView extends AppCompatTextView {

    public HtmlTextView(Context context) {
        super(context);
        init();
    }

    public HtmlTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public HtmlTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /**
     * Linkify, format markdown and escape the displayed message.
     */
    private void init() {
        formatHtmlAndCustomTags();
    }

    public void setTextFormatted(String text) {
        setText(text);
        formatHtmlAndCustomTags();
    }

    private void formatHtmlAndCustomTags() {
        setText(new SpannableString(Html.fromHtml(getText().toString())));
        Linkify.TransformFilter filter = new Linkify.TransformFilter() {
            public final String transformUrl(final Matcher match, String url) {
                return match.group();
            }
        };

        Pattern hashtagPattern = Pattern.compile("[#]+[A-Za-z0-9-_]+\\b");
        String hashtagScheme = MainActivity.CONTENT_HASHTAG;
        Linkify.addLinks(this, hashtagPattern, hashtagScheme, null, filter);

        Pattern urlPattern = Patterns.WEB_URL;
        Linkify.addLinks(this, urlPattern, null, null, filter);
    }
}