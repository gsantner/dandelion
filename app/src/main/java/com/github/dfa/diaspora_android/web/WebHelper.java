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

package com.github.dfa.diaspora_android.web;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Html;
import android.webkit.URLUtil;
import android.webkit.WebView;

import com.github.dfa.diaspora_android.activity.MainActivity;

/**
 * Created by Gregor Santner on 07.08.16.
 * https://gsantner.github.io
 */
public class WebHelper {

    public static boolean isOnline(Context context) {
        ConnectivityManager cnm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cnm.getActiveNetworkInfo();
        return ni != null && ni.isConnectedOrConnecting();
    }

    public static String replaceUrlWithMarkdown(String url) {
        if (url != null && URLUtil.isHttpUrl(url) || URLUtil.isHttpsUrl(url)) {
            return "<" + url + ">";
        }
        return url;
    }

    public static String escapeHtmlText(String text) {
        text = Html.escapeHtml(text);
        text = text.replace("\n", "&#10;");
        return text;
    }

    public static void optimizeMobileSiteLayout(final WebView wv) {
        wv.loadUrl("javascript: ( function() {" +
                "    if (document.documentElement == null || document.documentElement.style == null) { return; }" +
                "    document.documentElement.style.paddingBottom = '50px';" +
                "    document.getElementById('main').style.paddingTop = '5px';" +
                "    if(document.getElementById('main_nav')) {" +
                "        document.getElementById('main_nav').parentNode.removeChild(" +
                "        document.getElementById('main_nav'));" +
                "    } else if (document.getElementById('main-nav')) {" +
                "        document.getElementById('main-nav').parentNode.removeChild(" +
                "        document.getElementById('main-nav'));" +
                "    }" +
                "})();");
    }

    public static void getUserProfile(final WebView wv) {
        // aspects":[{"id":124934,"name":"Friends","selected":true},{"id":124937,"name":"Liked me","selected":false},{"id":124938,"name":"Follow","selected":false},{"id":128327,"name":"Nur ich","selected":false}]
        wv.loadUrl("javascript: ( function() {" +
                "    if (typeof gon !== 'undefined' && typeof gon.user !== 'undefined') {" +
                "        var followed_tags = document.getElementById(\"followed_tags\");" +
                "        if(followed_tags != null) {" +
                "            try {" +
                "                var links = followed_tags.nextElementSibling.children[0].children;" +
                "                var tags = [];" +
                "                for(var i = 0; i < links.length - 1; i++) {" + // the last element is "Manage followed tags" link
                "                    tags.push(links[i].innerText.replace('#',''));" +
                "                }" +
                "                gon.user[\"android_app.followed_tags\"] = tags;" +
                "            } catch(e) {}" +
                "        }" +
                "       var userProfile = JSON.stringify(gon.user);" +
                "       AndroidBridge.setUserProfile(userProfile.toString());" +
                "    } " +
                "})();");
    }

    public static void shareTextIntoWebView(final WebView webView, String sharedText) {
        sharedText = sharedText.replace("'", "&apos;").replace("\"", "&quot;");
        webView.loadUrl("javascript:(function() { " +
                "        document.documentElement.style.paddingBottom = '500px';" +
                "    if (typeof window.hasBeenSharedTo !== 'undefined') { AndroidBridge.contentHasBeenShared(); return; }" +
                "    var textbox = document.getElementsByTagName('textarea')[0];" +
                "    var textToBeShared = '" + sharedText + "';" +
                "    if (textbox) { " +
                "        textbox.style.height='210px'; " +
                "        textbox.innerHTML = textToBeShared; " +
                "        window.hasBeenSharedTo = true;" +
                "        window.lastShared = textToBeShared;" +
                "    }" +
                "})();");
    }

    private static String lastUpdateTitleByUrl ="";
    public static synchronized void sendUpdateTitleByUrlIntent(String url, Context context){
        // Ignore javascript stuff
        if (url != null && url.startsWith("javascript:")){
            return;
        }

        // Don't spam intents
        if (lastUpdateTitleByUrl != null && !lastUpdateTitleByUrl.equals(url) && url != null) {
            Intent updateActivityTitleIntent = new Intent(MainActivity.ACTION_UPDATE_TITLE_FROM_URL);
            updateActivityTitleIntent.putExtra(MainActivity.EXTRA_URL, url);
            LocalBroadcastManager.getInstance(context).sendBroadcast(updateActivityTitleIntent);
        }
        lastUpdateTitleByUrl = url;
    }
}
