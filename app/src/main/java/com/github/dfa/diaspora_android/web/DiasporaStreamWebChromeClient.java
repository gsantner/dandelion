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

import android.content.DialogInterface;
import android.webkit.JsResult;
import android.webkit.WebView;
import android.widget.ProgressBar;

import com.github.dfa.diaspora_android.R;
import com.github.dfa.diaspora_android.ui.theme.ThemedAlertDialogBuilder;
import com.github.dfa.diaspora_android.util.AppLog;
import com.github.dfa.diaspora_android.util.AppSettings;

/**
 * WebChromeClient that handles sharing text to diaspora*
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
        WebHelper.sendUpdateTitleByUrlIntent(wv.getUrl(), wv.getContext());

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

    @Override
    public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
        ThemedAlertDialogBuilder builder = new ThemedAlertDialogBuilder(view.getContext(), new AppSettings(view.getContext()));
        builder.setTitle(view.getContext().getString(R.string.confirmation))
                .setMessage(message)
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                result.confirm();
                            }
                        })
                .setNegativeButton(android.R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                result.cancel();
                            }
                        })
                .create()
                .show();
        return true;
    }

    public interface SharedTextCallback {
        String getSharedText();

        void setSharedText(String shared);
    }
}
