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

package com.github.dfa.diaspora_android.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.github.dfa.diaspora_android.App;
import com.github.dfa.diaspora_android.R;
import com.github.dfa.diaspora_android.util.Helpers;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ShareActivity extends MainActivity {

    private static final String TAG = "Diaspora Share";
    private WebView webView;
    private String podDomain;
    private ValueCallback<Uri[]> mFilePathCallback;
    private String mCameraPhotoPath;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeView;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main__activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (toolbar != null) {
            toolbar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (Helpers.isOnline(ShareActivity.this)) {
                        Intent intent = new Intent(ShareActivity.this, MainActivity.class);
                        startActivityForResult(intent, 100);
                        overridePendingTransition(0, 0);
                        finish();
                    } else {
                        Snackbar.make(swipeView, R.string.no_internet, Snackbar.LENGTH_LONG).show();
                    }
                }
            });
        }

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        swipeView = (SwipeRefreshLayout) findViewById(R.id.swipe);
        swipeView.setEnabled(false);

        podDomain = ((App) getApplication()).getSettings().getPodDomain();

        webView = (WebView) findViewById(R.id.webView);
        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);

        WebSettings wSettings = webView.getSettings();
        wSettings.setJavaScriptEnabled(true);
        wSettings.setBuiltInZoomControls(true);

        if (Build.VERSION.SDK_INT >= 21)
            wSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);

        /*
         * WebViewClient
         */
        webView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.d(TAG, url);
                if (!url.contains(podDomain)) {
                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(i);
                    return true;
                }
                return false;

            }

            public void onPageFinished(WebView view, String url) {
                Log.i(TAG, "Finished loading URL: " + url);
            }
        });


        /*
         * WebChromeClient
         */
        webView.setWebChromeClient(new WebChromeClient() {

            public void onProgressChanged(WebView wv, int progress) {
                progressBar.setProgress(progress);

                if (progress > 0 && progress <= 60) {
                    Helpers.getNotificationCount(wv);
                }

                if (progress > 60) {
                    Helpers.applyDiasporaMobileSiteChanges(wv);
                }

                if (progress == 100) {
                    progressBar.setVisibility(View.GONE);
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                if (mFilePathCallback != null) mFilePathCallback.onReceiveValue(null);

                mFilePathCallback = filePathCallback;

                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    // Create the File where the photo should go
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                        takePictureIntent.putExtra("PhotoPath", mCameraPhotoPath);
                    } catch (IOException ex) {
                        // Error occurred while creating the File
                        Snackbar.make(getWindow().findViewById(R.id.main__layout), "Unable to get image", Snackbar.LENGTH_LONG).show();
                    }

                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        mCameraPhotoPath = "file:" + photoFile.getAbsolutePath();
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                                Uri.fromFile(photoFile));
                    } else {
                        takePictureIntent = null;
                    }
                }

                Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
                contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
                contentSelectionIntent.setType("image/*");

                Intent[] intentArray;
                if (takePictureIntent != null) {
                    intentArray = new Intent[]{takePictureIntent};
                } else {
                    intentArray = new Intent[0];
                }

                Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
                chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
                chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);

                startActivityForResult(chooserIntent, INPUT_FILE_REQUEST_CODE);

                return true;
            }
        });

        if (savedInstanceState == null) {
            if (Helpers.isOnline(ShareActivity.this)) {
                webView.loadUrl("https://" + podDomain + "/status_messages/new");
            } else {
                Snackbar.make(getWindow().findViewById(R.id.main__layout), R.string.no_internet, Snackbar.LENGTH_LONG).show();
            }
        }

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                if (intent.hasExtra(Intent.EXTRA_SUBJECT)) {
                    handleSendSubject(intent);
                } else {
                    handleSendText(intent);}
            } else if (type.startsWith("image/")) {
                // TODO Handle single image being sent -> see manifest
                handleSendImage(intent);
            }
        //} else {
            // Handle other intents, such as being started from the home screen
        }

    }

    void handleSendText(Intent intent) {
        final String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        final String sharedBy = getString(R.string.shared_by_diaspora_android);

        if (sharedText != null) {
            webView.setWebViewClient(new WebViewClient() {

                public void onPageFinished(WebView view, String url) {

                    webView.setWebViewClient(new WebViewClient() {
                        @Override
                        public boolean shouldOverrideUrlLoading(WebView view, String url) {

                            finish();

                            Intent i = new Intent(ShareActivity.this, MainActivity.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(i);

                            return false;
                        }
                    });

                    webView.loadUrl("javascript:(function() { " +
                            "document.getElementsByTagName('textarea')[0].style.height='110px'; " +
                            "document.getElementsByTagName('textarea')[0].innerHTML = '> " + sharedText + " " + sharedBy + "'; " +
                            "    if(document.getElementById(\"main_nav\")) {" +
                            "        document.getElementById(\"main_nav\").parentNode.removeChild(" +
                            "        document.getElementById(\"main_nav\"));" +
                            "    } else if (document.getElementById(\"main-nav\")) {" +
                            "        document.getElementById(\"main-nav\").parentNode.removeChild(" +
                            "        document.getElementById(\"main-nav\"));" +
                            "    }" +
                            "})();");
                }
            });
        }
    }

    void handleSendSubject(Intent intent) {
        final String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        final String sharedSubject = intent.getStringExtra(Intent.EXTRA_SUBJECT);
        final String sharedBy = getString(R.string.shared_by_diaspora_android);
        if (sharedSubject != null) {
            webView.setWebViewClient(new WebViewClient() {

                public void onPageFinished(WebView view, String url) {

                    webView.setWebViewClient(new WebViewClient() {
                        @Override
                        public boolean shouldOverrideUrlLoading(WebView view, String url) {

                            finish();

                            Intent i = new Intent(ShareActivity.this, MainActivity.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(i);

                            return false;
                        }
                    });

                    webView.loadUrl("javascript:(function() { " +
                            "document.getElementsByTagName('textarea')[0].style.height='110px'; " +
                            "document.getElementsByTagName('textarea')[0].innerHTML = '**" + sharedSubject + "** " + sharedText + " " + sharedBy + "'; " +
                            "    if(document.getElementById(\"main_nav\")) {" +
                            "        document.getElementById(\"main_nav\").parentNode.removeChild(" +
                            "        document.getElementById(\"main_nav\"));" +
                            "    } else if (document.getElementById(\"main-nav\")) {" +
                            "        document.getElementById(\"main-nav\").parentNode.removeChild(" +
                            "        document.getElementById(\"main-nav\"));" +
                            "    }" +
                            "})();");
                }
            });
        }
    }

    // TODO Handle single image being sent -> see manifest

    void handleSendImage(Intent intent) {
        final Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (imageUri != null) {
            // Update UI to reflect text being shared
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != INPUT_FILE_REQUEST_CODE || mFilePathCallback == null) {
            super.onActivityResult(requestCode, resultCode, data);
            return;
        }
        Uri[] results = null;
        if (resultCode == RESULT_OK) {
            if (data == null) {
                if (mCameraPhotoPath != null) {
                    results = new Uri[]{Uri.parse(mCameraPhotoPath)};
                }
            } else {
                String dataString = data.getDataString();
                if (dataString != null) {
                    results = new Uri[]{Uri.parse(dataString)};
                }
            }
        }

        mFilePathCallback.onReceiveValue(results);
        mFilePathCallback = null;
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        return File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
            setTitle(R.string.app_name);
            Snackbar snackbar = Snackbar
                    .make(swipeView, R.string.confirm_exit, Snackbar.LENGTH_LONG)
                    .setAction(android.R.string.yes, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            finish();
                        }
                    });
            snackbar.show();
        } else {
            Snackbar snackbar = Snackbar
                    .make(swipeView, R.string.confirm_exit, Snackbar.LENGTH_LONG)
                    .setAction(android.R.string.yes, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            finish();
                        }
                    });
            snackbar.show();
        }
    }

}