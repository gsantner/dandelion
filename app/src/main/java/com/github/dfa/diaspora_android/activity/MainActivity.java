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

import android.Manifest;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ActionMenuView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.github.dfa.diaspora_android.App;
import com.github.dfa.diaspora_android.R;
import com.github.dfa.diaspora_android.data.AppSettings;
import com.github.dfa.diaspora_android.data.PodUserProfile;
import com.github.dfa.diaspora_android.listener.WebUserProfileChangedListener;
import com.github.dfa.diaspora_android.ui.ContextMenuWebView;
import com.github.dfa.diaspora_android.ui.CustomWebViewClient;
import com.github.dfa.diaspora_android.util.Helpers;
import com.github.dfa.diaspora_android.util.OrbotStatusReceiver;

import org.json.JSONException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import info.guardianproject.netcipher.proxy.OrbotHelper;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, WebUserProfileChangedListener {


    static final int INPUT_FILE_REQUEST_CODE = 1;
    private static final int REQUEST_CODE_ASK_PERMISSIONS = 123;
    public static final int REQUEST_CODE_ASK_PERMISSIONS_SAVE_IMAGE = 124;
    private static final String URL_MESSAGE = "URL_MESSAGE";

    private App app;
    private String podDomain;
    private Menu menu;
    private int notificationCount = 0;
    private int conversationCount = 0;
    private ValueCallback<Uri[]> mFilePathCallback;
    private String mCameraPhotoPath;
    private WebSettings webSettings;
    private AppSettings appSettings;
    private PodUserProfile podUserProfile;
    private final Handler uiHandler = new Handler();
    private CustomWebViewClient webViewClient;

    /**
     * UI Bindings
     */
    @BindView(R.id.swipe)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @BindView(R.id.toolbar)
    Toolbar toolbarTop;

    @BindView(R.id.toolbar2)
    ActionMenuView toolbarBottom;

    @BindView(R.id.webView)
    ContextMenuWebView webView;

    @BindView(R.id.main__navigaion_view)
    NavigationView navView;

    @BindView(R.id.main__layout)
    DrawerLayout navDrawer;

    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout toolbarTopCollapsing;


    // NavHeader cannot be bound by Butterknife
    private TextView navheaderTitle;
    private TextView navheaderDescription;
    private ImageView navheaderImage;


    /**
     * END  UI Bindings
     */

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (android.os.Build.VERSION.SDK_INT >= 21)
            WebView.enableSlowWholeDocumentDraw();

        // Bind UI
        setContentView(R.layout.main__activity);
        ButterKnife.bind(this);

        app = (App) getApplication();
        appSettings = app.getSettings();
        podUserProfile = app.getPodUserProfile();
        podUserProfile.setCallbackHandler(uiHandler);
        podUserProfile.setListener(this);

        //Orbot integration
        OrbotStatusReceiver.setMainActivity(this);
        OrbotHelper.requestStartTor(this.getApplicationContext());
        if(appSettings.isProxyOrbot()) {
            if(!OrbotHelper.isOrbotInstalled(this)) {
                appSettings.setProxyOrbot(false);
                promptInstallOrbot();
            } else {
                //precautionary set Proxy
                OrbotStatusReceiver.setProxy(this.getApplicationContext(), OrbotStatusReceiver.defaultHost, OrbotStatusReceiver.defaultPort);
            }
        }

        this.registerForContextMenu(webView);
        webView.setParentActivity(this);
        webView.setOverScrollMode(WebView.OVER_SCROLL_ALWAYS);

        // Setup toolbar
        setSupportActionBar(toolbarTop);
        getMenuInflater().inflate(R.menu.main__menu_bottom, toolbarBottom.getMenu());
        toolbarBottom.setOnMenuItemClickListener(new ActionMenuView.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                return MainActivity.this.onOptionsItemSelected(item);
            }
        });

        // Load app settings
        setupNavigationSlider();

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        podDomain = appSettings.getPodDomain();

        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                R.color.fab_big);

        // Setup WebView
        webView.addJavascriptInterface(new JavaScriptInterface(), "AndroidBridge");
        if (savedInstanceState != null) {
            webView.restoreState(savedInstanceState);
        }

        webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setMinimumFontSize(appSettings.getMinimumFontSize());
        webSettings.setLoadsImagesAutomatically(appSettings.isLoadImages());

        if (android.os.Build.VERSION.SDK_INT >= 21)
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);

        /*
         * WebViewClient
         */
        webViewClient = new CustomWebViewClient(app, swipeRefreshLayout, webView);
        webView.setWebViewClient(webViewClient);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Helpers.isOnline(MainActivity.this)) {
                    webView.reload();
                } else {
                    Snackbar.make(swipeRefreshLayout, R.string.no_internet, Snackbar.LENGTH_LONG).show();
                    swipeRefreshLayout.setRefreshing(false);
                }
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
                    Helpers.getUserProfile(wv);
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
                    File photoFile;
                    try {
                        photoFile = createImageFile();
                        takePictureIntent.putExtra("PhotoPath", mCameraPhotoPath);
                    } catch (IOException ex) {
                        // Error occurred while creating the File
                        Snackbar.make(swipeRefreshLayout, R.string.image, Snackbar.LENGTH_LONG).show();
                        return false;
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

        afterOnCreate(savedInstanceState);
    }

    private void afterOnCreate(Bundle savedInstanceState) {
        OrbotHelper.requestStartTor(this.getApplicationContext());
        if (savedInstanceState == null) {
            if (Helpers.isOnline(MainActivity.this)) {
                webView.loadData("", "text/html", null);
                webView.loadUrl("https://" + podDomain);
            } else {
                Snackbar.make(swipeRefreshLayout, R.string.no_internet, Snackbar.LENGTH_LONG).show();
            }
        }
    }

    private void setupNavigationSlider() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, navDrawer, toolbarTop, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        navDrawer.addDrawerListener(toggle);
        toggle.syncState();

        //NavigationView navView = ButterKnife.findById(this, R.id.nav_view);
        navView.setNavigationItemSelectedListener(this);

        View navHeader = navView.getHeaderView(0);
        navheaderTitle = ButterKnife.findById(navHeader, R.id.navheader_title);
        navheaderDescription = ButterKnife.findById(navHeader, R.id.podselection__podupti_notice);
        navheaderImage = ButterKnife.findById(navHeader, R.id.navheader_user_image);

        if (!appSettings.getName().equals("")) {
            navheaderTitle.setText(appSettings.getName());
        }
        if (!appSettings.getPodDomain().equals("")) {
            navheaderDescription.setText(appSettings.getPodDomain());
        }
        if (!appSettings.getAvatarUrl().equals("")) {
            // Try to load image
            if (!app.getAvatarImageLoader().loadToImageView(navheaderImage)) {
                // If not yet loaded, start download
                app.getAvatarImageLoader().startImageDownload(navheaderImage, appSettings.getAvatarUrl());
            }
        }
    }

    @OnClick(R.id.toolbar)
    public void onToolBarClicked(View view) {
        onNavigationItemSelected(navView.getMenu().findItem(R.id.nav_stream));
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("dd-MM-yy_HH-mm", Locale.getDefault()).format(new Date());
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != INPUT_FILE_REQUEST_CODE || mFilePathCallback == null) {
            super.onActivityResult(requestCode, resultCode, data);
            return;
        }
        Uri[] results = null;
        if (resultCode == Activity.RESULT_OK) {
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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        webView.saveState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        webView.restoreState(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(brLoadUrl, new IntentFilter(URL_MESSAGE));
    }

    @Override
    public void onBackPressed() {
        if (navDrawer.isDrawerOpen(navView)) {
            navDrawer.closeDrawer(navView);
            return;
        }

        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            Snackbar snackbar = Snackbar
                    .make(swipeRefreshLayout, R.string.confirm_exit, Snackbar.LENGTH_LONG)
                    .setAction(android.R.string.yes, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            moveTaskToBack(true);
                        }
                    });
            snackbar.show();
        }
    }

    private final BroadcastReceiver brLoadUrl = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String url = intent.getStringExtra("url");
            webView.loadUrl(url);
        }
    };

    @Override
    protected void onPause() {
        unregisterReceiver(brLoadUrl);
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main__menu_top, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        this.menu = menu;
        MenuItem itemNotification = menu.findItem(R.id.action_notifications);
        if (itemNotification != null) {
            if (notificationCount > 0) {
                itemNotification.setIcon(R.drawable.ic_bell_ring_white_24dp);
            } else {
                itemNotification.setIcon(R.drawable.ic_bell_outline_white_24dp);
            }

            MenuItem itemConversation = menu.findItem(R.id.action_conversations);
            if (conversationCount > 0) {
                itemConversation.setIcon(R.drawable.ic_message_text_white_24dp);
            } else {
                itemConversation.setIcon(R.drawable.ic_message_text_outline_white_24dp);
            }
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_notifications: {
                if (Helpers.isOnline(MainActivity.this)) {
                    webView.loadUrl("https://" + podDomain + "/notifications");
                    return true;
                } else {
                    Snackbar.make(swipeRefreshLayout, R.string.no_internet, Snackbar.LENGTH_LONG).show();
                    return false;
                }
            }

            case R.id.action_conversations: {
                if (Helpers.isOnline(MainActivity.this)) {
                    webView.loadUrl("https://" + podDomain + "/conversations");
                    return true;
                } else {
                    Snackbar.make(swipeRefreshLayout, R.string.no_internet, Snackbar.LENGTH_LONG).show();
                    return false;
                }
            }

            case R.id.action_exit: {
                moveTaskToBack(true);
                return true;
            }

            case R.id.action_compose: {
                if (Helpers.isOnline(MainActivity.this)) {
                    webView.loadUrl("https://" + podDomain + "/status_messages/new");
                } else {
                    Snackbar.make(swipeRefreshLayout, R.string.no_internet, Snackbar.LENGTH_LONG).show();
                }
                return true;
            }

            case R.id.action_go_to_top: {
                // Scroll to top (animated)
                ObjectAnimator anim = ObjectAnimator.ofInt(webView, "scrollY", webView.getScrollY(), 0);
                anim.setDuration(400);
                anim.start();
                return true;
            }

            case R.id.action_share_link: {
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(Intent.EXTRA_SUBJECT, webView.getTitle());
                sharingIntent.putExtra(Intent.EXTRA_TEXT, webView.getUrl());
                startActivity(Intent.createChooser(sharingIntent, "Share using"));
                return true;
            }

            case R.id.action_take_screenshot: {
                makeScreenshotOfWebView(false);
                return true;
            }

            case R.id.action_share_screenshot: {
                makeScreenshotOfWebView(true);
                return true;
            }

            case R.id.action_search: {
                if (Helpers.isOnline(MainActivity.this)) {
                    final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    LinearLayout layout = new LinearLayout(this);
                    layout.setOrientation(LinearLayout.VERTICAL);
                    layout.setGravity(Gravity.CENTER_HORIZONTAL);
                    final EditText input = new EditText(this);
                    input.setSingleLine(true);
                    layout.setPadding(50, 0, 50, 0);
                    input.setHint(R.string.app_hashtag);
                    layout.addView(input);

                    final DialogInterface.OnClickListener onSearchAccepted = new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            boolean wasClickedOnSearchForPeople = which == DialogInterface.BUTTON_NEGATIVE;

                            String inputTag = input.getText().toString().trim();
                            String cleanTag = inputTag.replaceAll(wasClickedOnSearchForPeople ? "\\*" : "\\#", "");
                            // this validate the input data for tagfind
                            if (cleanTag == null || cleanTag.equals("")) {
                                Snackbar.make(swipeRefreshLayout, R.string.search_alert_bypeople_validate_needsomedata, Snackbar.LENGTH_LONG).show();
                            } else { // User have added a search tag
                                if (wasClickedOnSearchForPeople) {
                                    webView.loadUrl("https://" + podDomain + "/people.mobile?q=" + cleanTag);
                                } else {
                                    webView.loadUrl("https://" + podDomain + "/tags/" + cleanTag);
                                }
                            }

                            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                            imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
                        }
                    };

                    final AlertDialog dialog = new AlertDialog.Builder(this)
                            .setView(layout)
                            .setTitle(R.string.search_alert_title)
                            .setCancelable(true)
                            .setPositiveButton(R.string.search_alert_tag, onSearchAccepted)
                            .setNegativeButton(R.string.search_alert_people, onSearchAccepted)
                            .create();

                    input.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                            if (actionId == EditorInfo.IME_ACTION_DONE) {
                                dialog.hide();
                                onSearchAccepted.onClick(null, 0);
                                return true;
                            }
                            return false;
                        }
                    });

                    // Popup keyboard
                    dialog.show();
                    input.requestFocus();
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

                } else {
                    Snackbar.make(swipeRefreshLayout, R.string.no_internet, Snackbar.LENGTH_LONG).show();
                }
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean makeScreenshotOfWebView(boolean hasToShareScreenshot) {
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            int hasWRITE_EXTERNAL_STORAGE = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (hasWRITE_EXTERNAL_STORAGE != PackageManager.PERMISSION_GRANTED) {
                if (!shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    new AlertDialog.Builder(MainActivity.this)
                            .setMessage(R.string.permissions_screenshot)
                            .setNegativeButton(android.R.string.no, null)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (android.os.Build.VERSION.SDK_INT >= 23)
                                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                                REQUEST_CODE_ASK_PERMISSIONS);
                                }
                            })
                            .show();
                    return false;
                }
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_CODE_ASK_PERMISSIONS);
                return false;
            }
        }

        Date dateNow = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yy_MM_dd--HH_mm_ss", Locale.getDefault());
        File fileSaveDirectory = new File(hasToShareScreenshot ? getCacheDir().getAbsolutePath()
                : Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/Diaspora");

        String fileSaveName = String.format("DfA_%s.jpg", hasToShareScreenshot ? "share" : dateFormat.format(dateNow));
        if (!fileSaveDirectory.exists()) {
            fileSaveDirectory.mkdirs();
        }

        if (!hasToShareScreenshot) {
            Snackbar.make(swipeRefreshLayout, getString(R.string.toast_screenshot) + " " + fileSaveName, Snackbar.LENGTH_LONG).show();
        }

        Bitmap bitmap = null;
        webView.setDrawingCacheEnabled(true);
        bitmap = Bitmap.createBitmap(webView.getDrawingCache());
        webView.setDrawingCacheEnabled(false);

        OutputStream bitmapWriter = null;
        try {
            bitmapWriter = new FileOutputStream(new File(fileSaveDirectory, fileSaveName));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, bitmapWriter);
            bitmapWriter.flush();
            bitmap.recycle();
        } catch (Exception e) {
            return false;
        } finally {
            if (bitmapWriter != null) {
                try {
                    bitmapWriter.close();
                } catch (IOException _ignored) {
                }
            }
        }

        // Only show share intent when Action Share Screenshot was selected
        if (hasToShareScreenshot) {
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("image/jpeg");
            sharingIntent.putExtra(Intent.EXTRA_SUBJECT, webView.getTitle());
            sharingIntent.putExtra(Intent.EXTRA_TEXT, webView.getUrl());
            Uri bmpUri = Uri.fromFile(new File(fileSaveDirectory, fileSaveName));
            sharingIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
            startActivity(Intent.createChooser(sharingIntent, getString(R.string.share_dotdodot)));
        }
        else {
            // Broadcast that this file is indexable
            File file = new File(fileSaveDirectory, fileSaveName);
            Uri uri = Uri.fromFile(file);
            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri);
            sendBroadcast(intent);
        }
        return true;
    }


    private void alertFormElements() {

    /*
     * Inflate the XML view. activity_main is in
     * res/layout/form_elements.xml
     */
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View formElementsView = inflater.inflate(R.layout.ui__font_size_chooser,
                null, false);

        final RadioGroup rgFontSize = (RadioGroup) formElementsView
                .findViewById(R.id.genderRadioGroup);

        // the alert dialog
        new AlertDialog.Builder(MainActivity.this).setView(formElementsView)
                .setTitle("Set Font Size")
                .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        int selectedId = rgFontSize
                                .getCheckedRadioButtonId();

                        // find the radiobutton by returned id
                        RadioButton selectedRadioButton = (RadioButton) formElementsView
                                .findViewById(selectedId);

                        if (selectedRadioButton.getId() == R.id.radNormal) {
                            appSettings.setMinimumFontSize(8);
                        } else if (selectedRadioButton.getId() == R.id.radLarge) {
                            appSettings.setMinimumFontSize(16);
                        } else if (selectedRadioButton.getId() == R.id.radLarger) {
                            appSettings.setMinimumFontSize(20);
                        }

                        webSettings.setMinimumFontSize(appSettings.getMinimumFontSize());

                        if (Helpers.isOnline(MainActivity.this)) {
                            webView.loadUrl(webView.getUrl());
                        } else {
                            Snackbar.make(swipeRefreshLayout, R.string.no_internet, Snackbar.LENGTH_LONG).show();
                        }
                    }
                }).show();
    }

    @Override
    public void onUserProfileNameChanged(String name) {
        navheaderTitle.setText(name);
    }

    @Override
    public void onUserProfileAvatarChanged(String avatarUrl) {
        app.getAvatarImageLoader().startImageDownload(navheaderImage, avatarUrl);
    }

    // TODO: Move from Javascript interface
    @Override
    public void onNotificationCountChanged(int notificationCount) {

    }

    // TODO: Move from Javascript interface
    @Override
    public void onUnreadMessageCountChanged(int unreadMessageCount) {

    }

    private class JavaScriptInterface {
        @JavascriptInterface
        public void setNotificationCount(final String webMessage) {
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (menu == null) {
                        return;
                    }
                    notificationCount = Integer.valueOf(webMessage);

                    MenuItem item = menu.findItem(R.id.action_notifications);

                    if (item != null) {
                        if (notificationCount > 0) {
                            item.setIcon(R.drawable.ic_bell_ring_white_24dp);
                            Snackbar snackbar = Snackbar
                                    .make(swipeRefreshLayout, R.string.new_notifications, Snackbar.LENGTH_LONG)
                                    .setAction(android.R.string.yes, new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            if (Helpers.isOnline(MainActivity.this)) {
                                                webView.loadUrl("https://" + podDomain + "/notifications");
                                            } else {
                                                Snackbar.make(swipeRefreshLayout, R.string.no_internet, Snackbar.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                            snackbar.show();
                        } else {
                            item.setIcon(R.drawable.ic_bell_outline_white_24dp);
                        }
                    }


                }
            });
        }

        @JavascriptInterface
        public void setUserProfile(final String webMessage) throws JSONException {
            if (podUserProfile.isRefreshNeeded()) {
                podUserProfile.parseJson(webMessage);
            }
        }

        @JavascriptInterface
        public void setConversationCount(final String webMessage) {
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (menu == null) {
                        return;
                    }
                    conversationCount = Integer.valueOf(webMessage);

                    MenuItem item = menu.findItem(R.id.action_conversations);

                    if (item != null) {
                        if (conversationCount > 0) {
                            item.setIcon(R.drawable.ic_message_text_white_24dp);
                            Snackbar snackbar = Snackbar
                                    .make(swipeRefreshLayout, R.string.new_conversations, Snackbar.LENGTH_LONG)
                                    .setAction(android.R.string.yes, new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            if (Helpers.isOnline(MainActivity.this)) {
                                                webView.loadUrl("https://" + podDomain + "/conversations");
                                            } else {
                                                Snackbar.make(swipeRefreshLayout, R.string.no_internet, Snackbar.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                            snackbar.show();
                        } else {
                            item.setIcon(R.drawable.ic_message_text_outline_white_24dp);
                        }
                    }

                }
            });
        }

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        switch (item.getItemId()) {
            case R.id.nav_stream: {
                if (Helpers.isOnline(MainActivity.this)) {
                    webView.loadUrl("https://" + podDomain + "/stream");
                } else {
                    Snackbar.make(swipeRefreshLayout, R.string.no_internet, Snackbar.LENGTH_LONG).show();
                }
            }
            break;

            case R.id.nav_profile: {
                if (Helpers.isOnline(MainActivity.this)) {
                    webView.loadUrl("https://" + podDomain + "/people/" + appSettings.getProfileId());
                } else {
                    Snackbar.make(swipeRefreshLayout, R.string.no_internet, Snackbar.LENGTH_LONG).show();
                }
            }
            break;

            // TODO followed_tags currently not implemented as single viewable page (0.5.7.1-paf04894e, 2016 March 20)
            case R.id.nav_followed_tags: {
                if (Helpers.isOnline(MainActivity.this)) {
                    webView.loadUrl("https://" + podDomain + "/followed_tags");
                } else {
                    Snackbar.make(swipeRefreshLayout, R.string.no_internet, Snackbar.LENGTH_LONG).show();
                }
            }
            break;

            case R.id.nav_aspects: {
                if (Helpers.isOnline(MainActivity.this)) {
                   // webView.loadUrl("https://" + podDomain + "/aspects");
                    Helpers.showAspectList(webView, app);
                } else {
                    Snackbar.make(swipeRefreshLayout, R.string.no_internet, Snackbar.LENGTH_LONG).show();
                }
            }
            break;

            case R.id.nav_activities: {
                if (Helpers.isOnline(MainActivity.this)) {
                    webView.loadUrl("https://" + podDomain + "/activity");
                } else {
                    Snackbar.make(swipeRefreshLayout, R.string.no_internet, Snackbar.LENGTH_LONG).show();
                }
            }
            break;

            case R.id.nav_liked: {
                if (Helpers.isOnline(MainActivity.this)) {
                    webView.loadUrl("https://" + podDomain + "/liked");
                } else {
                    Snackbar.make(swipeRefreshLayout, R.string.no_internet, Snackbar.LENGTH_LONG).show();
                }
            }
            break;

            case R.id.nav_commented: {
                if (Helpers.isOnline(MainActivity.this)) {
                    webView.loadUrl("https://" + podDomain + "/commented");
                } else {
                    Snackbar.make(swipeRefreshLayout, R.string.no_internet, Snackbar.LENGTH_LONG).show();
                }
            }
            break;

            case R.id.nav_mentions: {
                if (Helpers.isOnline(MainActivity.this)) {
                    webView.loadUrl("https://" + podDomain + "/mentions");
                } else {
                    Snackbar.make(swipeRefreshLayout, R.string.no_internet, Snackbar.LENGTH_LONG).show();
                }
            }
            break;

            case R.id.nav_public: {
                if (Helpers.isOnline(MainActivity.this)) {
                    webView.loadUrl("https://" + podDomain + "/public");
                } else {
                    Snackbar.make(swipeRefreshLayout, R.string.no_internet, Snackbar.LENGTH_LONG).show();
                }
            }
            break;

            case R.id.nav_settings_app: {
                final CharSequence[] options = {getString(R.string.settings_font), getString(R.string.settings_view), appSettings.isLoadImages() ?
                        getString(R.string.settings_images_switch_off) : getString(R.string.settings_images_switch_on), getString(R.string.jb_pod),
                appSettings.isProxyOrbot() ? getString(R.string.orbot_proxy_enabled) : getString(R.string.orbot_proxy_disabled)};

                if (Helpers.isOnline(MainActivity.this)) {
                    new AlertDialog.Builder(MainActivity.this)
                            .setItems(options, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int item) {
                                    switch (item) {
                                        case 0:
                                            alertFormElements();
                                            break;
                                        case 1:
                                            webView.loadUrl("https://" + podDomain + "/mobile/toggle");
                                            break;
                                        case 2:
                                            webSettings.setLoadsImagesAutomatically(!appSettings.isLoadImages());
                                            appSettings.setLoadImages(!appSettings.isLoadImages());
                                            webView.loadUrl(webView.getUrl());
                                            break;
                                        case 3:
                                            new AlertDialog.Builder(MainActivity.this)
                                                    .setTitle(getString(R.string.confirmation))
                                                    .setMessage(getString(R.string.change_pod_warning))
                                                    .setNegativeButton(android.R.string.no, null)
                                                    .setPositiveButton(android.R.string.yes,
                                                            new DialogInterface.OnClickListener() {
                                                                public void onClick(DialogInterface dialog, int id) {
                                                                    app.resetPodData(webView);
                                                                    Helpers.animateToActivity(MainActivity.this, PodSelectionActivity.class, true);
                                                                }
                                                            })
                                                    .show();
                                            break;
                                        case 4:
                                            boolean before = appSettings.isProxyOrbot();
                                            appSettings.setProxyOrbot(!before);
                                            if(before) {
                                                OrbotStatusReceiver.resetProxy(MainActivity.this.getApplicationContext());
                                            } else {
                                                OrbotHelper.requestStartTor(MainActivity.this);
                                                webView.reload();
                                            }

                                    }
                                }
                            }).show();
                } else {
                    Snackbar.make(swipeRefreshLayout, R.string.no_internet, Snackbar.LENGTH_LONG).show();
                }
            }
            break;

            case R.id.nav_settings_diaspora: {
                final CharSequence[] options2 = {getString(R.string.jb_settings), getString(R.string.jb_manage_tags),
                        getString(R.string.jb_contacts)};
                if (Helpers.isOnline(MainActivity.this)) {
                    new AlertDialog.Builder(MainActivity.this)
                            .setItems(options2, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int item) {
                                    if (options2[item].equals(getString(R.string.jb_settings)))
                                        webView.loadUrl("https://" + podDomain + "/user/edit");
                                    if (options2[item].equals(getString(R.string.jb_manage_tags)))
                                        webView.loadUrl("https://" + podDomain + "/tag_followings/manage");
                                    if (options2[item].equals(getString(R.string.jb_contacts)))
                                        webView.loadUrl("https://" + podDomain + "/contacts");
                                }
                            }).show();
                } else {
                    Snackbar.make(swipeRefreshLayout, R.string.no_internet, Snackbar.LENGTH_LONG).show();
                }
            }
            break;

            case R.id.nav_license_help: {
                final CharSequence[] options = {getString(R.string.help_license), getString(R.string.help_about), getString(R.string.help_help)};
                new AlertDialog.Builder(MainActivity.this)
                        .setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int item) {
                                if (options[item].equals(getString(R.string.help_license))) {
                                    final SpannableString s = new SpannableString(Html.fromHtml(getString(R.string.license_text)));
                                    Linkify.addLinks(s, Linkify.WEB_URLS);
                                    final AlertDialog d = new AlertDialog.Builder(MainActivity.this)
                                            .setTitle(R.string.license_title)
                                            .setMessage(s)
                                            .setPositiveButton(android.R.string.yes, null).show();
                                    d.show();
                                    ((TextView) d.findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());
                                }
                                if (options[item].equals(getString(R.string.help_about))) {
                                    final SpannableString s = new SpannableString(Html.fromHtml(getString(R.string.about_text)));
                                    Linkify.addLinks(s, Linkify.WEB_URLS);
                                    final AlertDialog d = new AlertDialog.Builder(MainActivity.this)
                                            .setTitle(R.string.help_about)
                                            .setMessage(s)
                                            .setPositiveButton(android.R.string.yes, null).show();
                                    d.show();
                                    ((TextView) d.findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());
                                }
                                if (options[item].equals(getString(R.string.help_help))) {
                                    new AlertDialog.Builder(MainActivity.this)
                                            .setTitle(R.string.help_help)
                                            .setMessage(Html.fromHtml(getString(R.string.markdown_text)))
                                            .setPositiveButton(android.R.string.yes, null).show();
                                }
                            }
                        }).show();
            }
            break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.main__layout);
        if(drawer != null) drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS_SAVE_IMAGE:
                if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, R.string.permission_granted_try_again, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_SHORT).show();
                }
                return;

            default:
                super.onRequestPermissionsResult(requestCode, permissions,
                        grantResults);
        }
    }

    /**
     * Ask the user whether to install Orbot or not. Check if installing from
     * F-Droid or Google Play, otherwise take the user to the Orbot download
     * page on f-droid.org.
     */
    void promptInstallOrbot() {
        String message = this.getString(R.string.you_must_have_orbot) + "  ";

        final Intent intent = OrbotHelper.getOrbotInstallIntent(MainActivity.this);
        if (intent.getPackage() == null) {
            message += MainActivity.this.getString(R.string.download_orbot_from_fdroid);
        } else {
            message += MainActivity.this.getString(R.string.get_orbot_from_fdroid);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.install_orbot_);
        builder.setMessage(message);
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                MainActivity.this.startActivity(intent);
            }
        });
        builder.setNegativeButton(android.R.string.no, null);
        builder.show();
    }

    public void requestOrbotStart(boolean backgroundStartsDisabled) {
        AlertDialog.Builder startDialog = new AlertDialog.Builder(this);
        startDialog.setTitle(R.string.start_orbot_);
        if(backgroundStartsDisabled)
            startDialog.setMessage(R.string.orbot_starts_disabled_message);
        else
            startDialog.setMessage(R.string.orbot_doesn_t_appear_to_be_running_would_you_like_to_start_it_up_and_connect_to_tor_);

        startDialog.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startActivityForResult(OrbotHelper.getShowOrbotStartIntent(), 1);
            }
        });
        startDialog.setNegativeButton(android.R.string.no, null);
        startDialog.show();
    }

    public boolean useOrbot() {
        return appSettings.isProxyOrbot();
    }
}