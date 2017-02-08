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

import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ProgressBar;

/**
 * WebChromeClient that connects the ProgressBar and the WebView and updates the progress of the progressBar.
 * Created by vanitas on 26.09.16.
 */

public class ProgressBarWebChromeClient extends WebChromeClient {
    protected final ProgressBar progressBar;
    protected final WebView webView;

    public ProgressBarWebChromeClient(WebView webView, ProgressBar progressBar) {
        this.webView = webView;
        this.progressBar = progressBar;
    }

    public void onProgressChanged(WebView wv, int progress) {
        progressBar.setProgress(progress);
        progressBar.setVisibility(progress == 100 ? View.GONE : View.VISIBLE);
    }
}
