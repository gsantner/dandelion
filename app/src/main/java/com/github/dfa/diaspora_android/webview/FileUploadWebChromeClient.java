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

import android.net.Uri;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.widget.ProgressBar;

/**
 * WebChromeClient that supports uploading images
 * Created by vanitas on 26.09.16.
 */

public class FileUploadWebChromeClient extends ProgressBarWebChromeClient {
    protected FileUploadCallback fileUploadCallback;

    public FileUploadWebChromeClient(WebView webView, ProgressBar progressBar, FileUploadCallback fileUploadCallback) {
        super(webView, progressBar);
        this.fileUploadCallback = fileUploadCallback;
    }

    //For Android 4.1/4.2 only. DO NOT REMOVE!
    @SuppressWarnings("unused")
    protected void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
        fileUploadCallback.legacyImageUpload(uploadMsg, acceptType, capture);
    }

    @Override
    public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
        return fileUploadCallback.imageUpload(webView, filePathCallback, fileChooserParams);
    }

    public interface FileUploadCallback {
        boolean imageUpload(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams);

        void legacyImageUpload(ValueCallback<Uri> uploadMsg, String acceptType, String capture);
    }
}
