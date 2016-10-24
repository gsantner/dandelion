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
package com.github.dfa.diaspora_android.webview;

import android.webkit.WebView;
import android.widget.ProgressBar;

import com.github.dfa.diaspora_android.util.AppLog;
import com.github.dfa.diaspora_android.util.WebHelper;

/**
 * WebChromeClient for DiasporaStreams
 * Created by vanitas on 26.09.16.
 */

public class DiasporaStreamWebChromeClient extends FileUploadWebChromeClient {
    protected SharedTextCallback sharedTextCallback;

    public DiasporaStreamWebChromeClient(WebView webView, ProgressBar progressBar, FileUploadCallback fileUploadCallback, SharedTextCallback callback) {
        super(webView, progressBar, fileUploadCallback);
        this.sharedTextCallback = callback;
    }

    @Override
    public void onProgressChanged(WebView wv, int progress) {
        super.onProgressChanged(wv, progress);
        WebHelper.optimizeMobileSiteLayout(wv);

        if (progress > 0 && progress <= 85) {
            WebHelper.getUserProfile(wv);
        }

        if (progress > 60) {
            String textToBeShared = sharedTextCallback.getSharedText();
            if (textToBeShared != null) {
                AppLog.d(this, "Share text into webView");
                WebHelper.shareTextIntoWebView(wv, textToBeShared);
            }
        }
    }

    public interface SharedTextCallback {
        String getSharedText();

        void setSharedText(String shared);
    }
}
