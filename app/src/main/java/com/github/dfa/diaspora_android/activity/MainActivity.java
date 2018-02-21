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
package com.github.dfa.diaspora_android.activity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.customtabs.CustomTabsSession;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.dfa.diaspora_android.App;
import com.github.dfa.diaspora_android.BuildConfig;
import com.github.dfa.diaspora_android.R;
import com.github.dfa.diaspora_android.data.DiasporaPodList;
import com.github.dfa.diaspora_android.data.DiasporaUserProfile;
import com.github.dfa.diaspora_android.listener.DiasporaUserProfileChangedListener;
import com.github.dfa.diaspora_android.listener.IntellihideToolbarActivityListener;
import com.github.dfa.diaspora_android.receiver.OpenExternalLinkReceiver;
import com.github.dfa.diaspora_android.receiver.UpdateTitleReceiver;
import com.github.dfa.diaspora_android.ui.BadgeDrawable;
import com.github.dfa.diaspora_android.ui.PodSelectionDialog;
import com.github.dfa.diaspora_android.ui.theme.CustomFragment;
import com.github.dfa.diaspora_android.ui.theme.ThemeHelper;
import com.github.dfa.diaspora_android.ui.theme.ThemedActivity;
import com.github.dfa.diaspora_android.ui.theme.ThemedAlertDialogBuilder;
import com.github.dfa.diaspora_android.util.ActivityUtils;
import com.github.dfa.diaspora_android.util.AndroidBug5497Workaround;
import com.github.dfa.diaspora_android.util.AppLog;
import com.github.dfa.diaspora_android.util.AppSettings;
import com.github.dfa.diaspora_android.util.ContextUtils;
import com.github.dfa.diaspora_android.util.DiasporaUrlHelper;
import com.github.dfa.diaspora_android.web.BrowserFragment;
import com.github.dfa.diaspora_android.web.ContextMenuWebView;
import com.github.dfa.diaspora_android.web.ProxyHandler;
import com.github.dfa.diaspora_android.web.WebHelper;
import com.github.dfa.diaspora_android.web.custom_tab.CustomTabActivityHelper;

import net.gsantner.opoc.util.SimpleMarkdownParser;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends ThemedActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        DiasporaUserProfileChangedListener,
        CustomTabActivityHelper.ConnectionCallback,
        IntellihideToolbarActivityListener,
        PodSelectionDialog.PodSelectionDialogResultListener {


    public static final int REQUEST_CODE_ASK_PERMISSIONS = 123;
    public static final int REQUEST_CODE__ACCESS_EXTERNAL_STORAGE = 124;
    public static final int INPUT_FILE_REQUEST_CODE_NEW = 1;
    public static final int INPUT_FILE_REQUEST_CODE_OLD = 2;

    public static final String ACTION_OPEN_URL = "com.github.dfa.diaspora_android.MainActivity.open_url";
    public static final String ACTION_OPEN_EXTERNAL_URL = "com.github.dfa.diaspora_android.MainActivity.open_external_url";
    public static final String ACTION_CHANGE_ACCOUNT = "com.github.dfa.diaspora_android.MainActivity.change_account";
    public static final String ACTION_CLEAR_CACHE = "com.github.dfa.diaspora_android.MainActivity.clear_cache";
    public static final String ACTION_UPDATE_TITLE_FROM_URL = "com.github.dfa.diaspora_android.MainActivity.set_title";
    public static final String URL_MESSAGE = "URL_MESSAGE";
    public static final String EXTRA_URL = "com.github.dfa.diaspora_android.extra_url";
    public static final String CONTENT_HASHTAG = "content://com.github.dfa.diaspora_android.mainactivity/";

    private App app;
    private CustomTabActivityHelper customTabActivityHelper;
    private AppSettings _appSettings;
    private DiasporaUrlHelper urls;
    private DiasporaUserProfile diasporaUserProfile;
    private final Handler uiHandler = new Handler();
    private OpenExternalLinkReceiver brOpenExternalLink;
    private BroadcastReceiver brSetTitle;
    private Snackbar snackbarExitApp, snackbarNoInternet, snackbarLastVisitedTimestampInStream;
    private FragmentManager fm;
    private CustomTabsSession customTabsSession;

    /**
     * UI Bindings
     */
    @BindView(R.id.main__appbar)
    AppBarLayout appBarLayout;

    @BindView(R.id.main__topbar)
    Toolbar toolbarTop;

    @BindView(R.id.fragment_container)
    FrameLayout fragmentContainer;

    @BindView(R.id.main__navigaion_view)
    NavigationView navView;

    @BindView(R.id.main__navdrawer)
    DrawerLayout navDrawer;

    RelativeLayout navDrawerLayout;
    LinearLayout navProfilePictureArea;


    // NavHeader cannot be bound by Butterknife
    private TextView navheaderTitle;
    private TextView navheaderDescription;
    private ImageView navheaderImage;

    private String textToBeShared;


    /**
     * END  UI Bindings
     */

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppLog.v(this, "onCreate()");

        // Pre UI
        ContextUtils.get().setAppLanguage(AppSettings.get().getLanguage());
        if (AppSettings.get().isEditorStatusBarHidden()) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }


        // Bind UI
        setContentView(R.layout.main__activity);
        ButterKnife.bind(this);
        if (AppSettings.get().isEditorStatusBarHidden()) {
            AndroidBug5497Workaround.assistActivity(this);
        }

        app = (App) getApplication();
        _appSettings = app.getSettings();
        diasporaUserProfile = app.getDiasporaUserProfile();
        diasporaUserProfile.setCallbackHandler(uiHandler);
        diasporaUserProfile.setListener(this);
        urls = new DiasporaUrlHelper(_appSettings);
        customTabActivityHelper = new CustomTabActivityHelper();
        customTabActivityHelper.setConnectionCallback(this);
        ProxyHandler.getInstance().updateProxySettings(this);

        fm = getSupportFragmentManager();
        setupUI();

        brOpenExternalLink = new OpenExternalLinkReceiver(this);
        brSetTitle = new UpdateTitleReceiver(app, urls, new UpdateTitleReceiver.TitleCallback() {
            public void setTitle(String url, int resId) {
                CustomFragment top = getTopFragment();
                if (top != null && top.getFragmentTag().equals(DiasporaStreamFragment.TAG)) {
                    MainActivity.this.setTitle(resId);
                    showLastVisitedTimestampMessageIfNeeded(url);
                }
            }

            public void setTitle(String url, String title) {
                CustomFragment top = getTopFragment();
                if (top != null && top.getFragmentTag().equals(DiasporaStreamFragment.TAG)) {
                    MainActivity.this.setTitle(title);
                }
            }
        });

        if (!_appSettings.hasPod()) {
            AppLog.d(this, "We have no pod. Show PodSelectionFragment");
            updateNavigationViewEntryVisibilities();
            showFragment(getFragment(PodSelectionFragment.TAG));
        } else {
            AppLog.d(this, "Pod found. Handle intents.");
            //Handle intent
            Intent intent = getIntent();
            if (intent != null && intent.getAction() != null) {
                handleIntent(intent);
            } else {
                openDiasporaUrl(urls.getStreamUrl());
            }
        }

        // Show first start dialog
        try {
            SimpleMarkdownParser mdParser = SimpleMarkdownParser.get().setDefaultSmpFilter(SimpleMarkdownParser.FILTER_ANDROID_TEXTVIEW);
            if (_appSettings.isAppFirstStart()) {
                mdParser.parse(
                        getResources().openRawResource(R.raw.license), "");
                String html = mdParser.getHtml()
                        + "<br/><br/><br/>"
                        + "<h1>" + getString(R.string.fragment_license__thirdparty_libs) + "</h1>"
                        + mdParser.parse(getResources().openRawResource(R.raw.license_third_party), "");
                html = mdParser.setHtml(html).removeMultiNewlines().getHtml();
                ActivityUtils.get(this).showDialogWithHtmlTextView(R.string.about_activity__title_about_license, html);
                _appSettings.isAppCurrentVersionFirstStart();
            } else if (_appSettings.isAppCurrentVersionFirstStart()) {
                SimpleMarkdownParser smp = new SimpleMarkdownParser().parse(
                        getResources().openRawResource(R.raw.changelog), "");
                ActivityUtils.get(this).showDialogWithHtmlTextView(R.string.changelog, smp.getHtml());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Setup the user interface. Set up both toolbars and initialize the snackbars.
     * Initialize the navigation drawer and apply intellihide settings.
     */
    private void setupUI() {
        AppLog.i(this, "setupUI()");

        // Setup _toolbar
        setSupportActionBar(toolbarTop);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        setTitle(R.string.app_name);

        //Setup snackbar
        snackbarExitApp = Snackbar
                .make(fragmentContainer, R.string.confirm_exit, Snackbar.LENGTH_LONG)
                .setAction(android.R.string.yes, new View.OnClickListener() {
                    public void onClick(View view) {
                        finish();
                        moveTaskToBack(true);
                    }
                });
        snackbarLastVisitedTimestampInStream =
                Snackbar.make(fragmentContainer,
                        R.string.jump_to_last_visited_timestamp_in_stream, Snackbar.LENGTH_LONG)
                        .setAction(android.R.string.yes, new View.OnClickListener() {
                            public void onClick(View view) {
                                openDiasporaUrl(urls.getStreamWithTimestampUrl(diasporaUserProfile.getLastVisitedPositionInStream()));
                            }
                        });
        snackbarNoInternet = Snackbar.make(fragmentContainer, R.string.no_internet, Snackbar.LENGTH_LONG);

        // Load app settings
        setupNavigationSlider();
        AppLog.v(this, "UI successfully set up");
    }

    /**
     * Get an instance of the CustomFragment with the tag fragmentTag.
     * If there was no instance so far, create a new one and add it to the FragmentManagers pool.
     * If there is no Fragment with the corresponding Tag, return the top fragment.
     *
     * @param fragmentTag tag
     * @return corresponding Fragment
     */
    protected CustomFragment getFragment(String fragmentTag) {
        CustomFragment fragment = (CustomFragment) fm.findFragmentByTag(fragmentTag);
        if (fragment != null) {
            return fragment;
        } else {
            switch (fragmentTag) {
                case DiasporaStreamFragment.TAG:
                    DiasporaStreamFragment dsf = new DiasporaStreamFragment();
                    fm.beginTransaction().add(dsf, fragmentTag).commit();
                    return dsf;
                case BrowserFragment.TAG:
                    BrowserFragment bf = new BrowserFragment();
                    fm.beginTransaction().add(bf, fragmentTag).commit();
                    return bf;
                case TagListFragment.TAG:
                    TagListFragment hlf = new TagListFragment();
                    fm.beginTransaction().add(hlf, fragmentTag).commit();
                    return hlf;
                case AspectListFragment.TAG:
                    AspectListFragment alf = new AspectListFragment();
                    fm.beginTransaction().add(alf, fragmentTag).commit();
                    return alf;
                case PodSelectionFragment.TAG:
                    PodSelectionFragment psf = new PodSelectionFragment();
                    fm.beginTransaction().add(psf, fragmentTag).commit();
                    return psf;
                default:
                    AppLog.e(this, "Invalid Fragment Tag: " + fragmentTag
                            + "\nAdd Fragments Tag to getFragment()'s switch case.");
                    return getTopFragment();
            }
        }
    }

    /**
     * Show DiasporaStreamFragment if necessary and load URL url
     *
     * @param url URL to load in the DiasporaStreamFragment
     */
    public void openDiasporaUrl(String url) {
        AppLog.v(this, "openDiasporaUrl()");
        DiasporaStreamFragment streamFragment = (DiasporaStreamFragment) getFragment(DiasporaStreamFragment.TAG);
        showFragment(streamFragment);
        showLastVisitedTimestampMessageIfNeeded(url);
        streamFragment.loadUrl(url);
    }

    public void showLastVisitedTimestampMessageIfNeeded(String url) {
        if (url.equals(urls.getStreamUrl()) && diasporaUserProfile.hasLastVisitedTimestampInStream()) {
            snackbarLastVisitedTimestampInStream.show();
            diasporaUserProfile.resetLastVisitedPositionInStream();
        }
    }

    /**
     * Show the Fragment fragment in R.id.fragment_container. If the fragment was already visible, do nothing.
     *
     * @param fragment Fragment to show
     */
    protected void showFragment(CustomFragment fragment) {
        AppLog.v(this, "showFragment()");
        CustomFragment currentTop = (CustomFragment) fm.findFragmentById(R.id.fragment_container);
        if (currentTop == null || !currentTop.getFragmentTag().equals(fragment.getFragmentTag())) {
            AppLog.v(this, "Fragment was not visible. Replace it.");
            fm.beginTransaction().addToBackStack(null).replace(R.id.fragment_container, fragment, fragment.getFragmentTag()).commit();
            invalidateOptionsMenu();
            setToolbarIntellihide(_appSettings.isIntellihideToolbars() && fragment.isAllowedIntellihide());
        } else {
            AppLog.v(this, "Fragment was already visible. Do nothing.");
        }
    }

    /**
     * Initialize the navigation slider
     */
    private void setupNavigationSlider() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, navDrawer, toolbarTop, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        navDrawer.addDrawerListener(toggle);
        toggle.syncState();

        //NavigationView navView = ButterKnife.findById(this, R.id.nav_view);
        navView.setNavigationItemSelectedListener(this);

        View navHeader = navView.getHeaderView(0);
        navProfilePictureArea = ButterKnife.findById(navHeader, R.id.nav_profile_picture);
        navDrawerLayout = ButterKnife.findById(navHeader, R.id.nav_drawer);
        //Handle clicks on profile picture
        navProfilePictureArea.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                navDrawer.closeDrawer(GravityCompat.START);
                if (!_appSettings.getProfileId().equals("")) {
                    openDiasporaUrl(urls.getProfileUrl());
                }
            }
        });
        navheaderTitle = ButterKnife.findById(navHeader, R.id.navheader_title);
        navheaderDescription = ButterKnife.findById(navHeader, R.id.podselection__podupti_notice);
        navheaderImage = ButterKnife.findById(navHeader, R.id.navheader_user_image);

        if (!_appSettings.getName().equals("")) {
            navheaderTitle.setText(_appSettings.getName());
        }
        if (_appSettings.getPod() != null) {
            navheaderDescription.setText(_appSettings.getPod().getName());
        }
        String avatarUrl = _appSettings.getAvatarUrl();
        if (!avatarUrl.equals("")) {
            //Display app launcher icon instead of default avatar asset
            //(Which would by the way not load because of missing pod domain prefix in the url)
            if (avatarUrl.startsWith("/assets/user/default")) {
                AppLog.v(this, "Avatar appears to be an asset. Display launcher icon instead (avatarUrl=" + avatarUrl + ")");
                navheaderImage.setImageResource(R.drawable.ic_launcher);
            } else {
                // Try to load image
                if (!app.getAvatarImageLoader().loadToImageView(navheaderImage)) {
                    // If not yet loaded, start download
                    AppLog.v(this, "Avatar not cached. Start download: " + avatarUrl);
                    app.getAvatarImageLoader().startImageDownload(navheaderImage, avatarUrl);
                }
            }
        } else if (BuildConfig.IS_TEST_BUILD) {
            navheaderImage.setImageResource(R.drawable.ic_launcher_test);
        }
        updateNavigationViewEntryVisibilities();
    }

    protected void updateNavigationViewEntryVisibilities() {
        Menu navMenu = navView.getMenu();

        // Initially show all items visible when logged in
        navMenu.setGroupVisible(navMenu.findItem(R.id.nav_exit).getGroupId(), true);

        // Hide by app settings
        navMenu.findItem(R.id.nav_exit).setVisible(_appSettings.isVisibleInNavExit());
        navMenu.findItem(R.id.nav_activities).setVisible(_appSettings.isVisibleInNavActivities());
        navMenu.findItem(R.id.nav_aspects).setVisible(_appSettings.isVisibleInNavAspects());
        navMenu.findItem(R.id.nav_contacts).setVisible(_appSettings.isVisibleInNavContacts());
        navMenu.findItem(R.id.nav_commented).setVisible(_appSettings.isVisibleInNavCommented());
        navMenu.findItem(R.id.nav_followed_tags).setVisible(_appSettings.isVisibleInNavFollowed_tags());
        navMenu.findItem(R.id.nav_about).setVisible(_appSettings.isVisibleInNavHelp_license());
        navMenu.findItem(R.id.nav_liked).setVisible(_appSettings.isVisibleInNavLiked());
        navMenu.findItem(R.id.nav_mentions).setVisible(_appSettings.isVisibleInNavMentions());
        navMenu.findItem(R.id.nav_profile).setVisible(_appSettings.isVisibleInNavProfile());
        navMenu.findItem(R.id.nav_public).setVisible(_appSettings.isVisibleInNavPublic_activities());
        navMenu.findItem(R.id.nav_stream).setVisible(true);
        navMenu.findItem(R.id.nav_statistics).setVisible(_appSettings.isVisibleInNavStatistics());
        navMenu.findItem(R.id.nav_reports).setVisible(_appSettings.isVisibleInNavReports());
        navMenu.findItem(R.id.nav_toggle_desktop_page).setVisible(_appSettings.isVisibleInNavToggleMobileDesktop());
        navMenu.findItem(R.id.nav_dandelion).setVisible(_appSettings.isVisibleInNavDandelionAccount());


        // Hide whole group (for logged in use) if no pod was selected
        if (!_appSettings.hasPod()) {
            navMenu.setGroupVisible(navMenu.findItem(R.id.nav_exit).getGroupId(), false);
        }
    }

    /**
     * Open Stream when clicked on top _toolbar AND preference stream shortcut is true
     *
     * @param view selected view
     */
    @OnClick(R.id.main__topbar)
    public void onToolBarClicked(View view) {
        AppLog.i(this, "onToolBarClicked()");
        if (_appSettings.isTopbarStreamShortcutEnabled() && _appSettings.hasPod()) {
            onNavigationItemSelected(navView.getMenu().findItem(R.id.nav_stream));
        }
    }

    /**
     * Forward incoming intents to handleIntent()
     *
     * @param intent incoming
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    /**
     * Handle intents and execute intent specific actions
     *
     * @param intent intent to get handled
     */
    private void handleIntent(Intent intent) {
        AppLog.i(this, "handleIntent()");
        if (intent == null) {
            AppLog.v(this, "Intent was null");
            return;
        }

        String action = intent.getAction();
        String type = intent.getType();
        String loadUrl = null;
        AppLog.v(this, "Action: " + action + " Type: " + type);
        if (Intent.ACTION_MAIN.equals(action)) {
            loadUrl = urls.getStreamUrl();
        } else if (ACTION_OPEN_URL.equals(action)) {
            loadUrl = intent.getStringExtra(URL_MESSAGE);
        } else if (Intent.ACTION_VIEW.equals(action) && intent.getDataString() != null) {
            Uri data = intent.getData();
            if (data != null && data.toString().startsWith(CONTENT_HASHTAG)) {
                handleHashtag(intent);
                return;
            } else {
                loadUrl = intent.getDataString();
                AppLog.v(this, "Intent has a delicious URL for us: " + loadUrl);
            }
        } else if (ACTION_CHANGE_ACCOUNT.equals(action)) {
            AppLog.v(this, "Reset pod data and  show PodSelectionFragment");
            _appSettings.setPod(null);
            runOnUiThread(new Runnable() {
                public void run() {
                    navheaderTitle.setText(R.string.app_name);
                    navheaderDescription.setText(R.string.app_subtitle);
                    navheaderImage.setImageResource(R.drawable.ic_launcher);
                    app.resetPodData(((DiasporaStreamFragment) getFragment(DiasporaStreamFragment.TAG)).getWebView());
                }
            });
            showFragment(getFragment(PodSelectionFragment.TAG));
        } else if (ACTION_CLEAR_CACHE.equals(action)) {
            AppLog.v(this, "Clear WebView cache");
            runOnUiThread(new Runnable() {
                public void run() {
                    ContextMenuWebView wv = ((DiasporaStreamFragment) getFragment(DiasporaStreamFragment.TAG)).getWebView();
                    if (wv != null) {
                        wv.clearCache(true);
                    }
                }
            });

        } else if (Intent.ACTION_SEND.equals(action) && type != null) {
            switch (type) {
                case "text/plain":
                    if (intent.hasExtra(Intent.EXTRA_SUBJECT)) {
                        handleSendSubject(intent);
                    } else {
                        handleSendText(intent);
                    }
                    break;
                case "image/*":
                    handleSendImage(intent); //TODO: Add intent filter to Manifest and implement method
                    break;
            }
        } else if (Intent.ACTION_SEND_MULTIPLE.equals(action) && type != null) {
            /* TODO: Implement and add filter to manifest */
            return;
        } else if ("sc_new_post".equals(action)) {
            openDiasporaUrl(urls.getNewPostUrl());
            return;
        } else if ("sc_nav_followed_tags".equals(action)) {
            showFragment(getFragment(TagListFragment.TAG));
            return;
        } else if ("sc_aspects".equals(action)) {
            showFragment(getFragment(AspectListFragment.TAG));
            return;
        } else if ("sc_activities".equals(action)) {
            openDiasporaUrl(urls.getActivityUrl());
            return;
        }
        //Catch split screen recreation
        if (action != null && action.equals(Intent.ACTION_MAIN) && getTopFragment() != null) {
            return;
        }

        if (loadUrl != null) {
            navDrawer.closeDrawers();
            openDiasporaUrl(loadUrl);
        }
    }

    /**
     * Handle activity results
     *
     * @param requestCode reqCode
     * @param resultCode  resCode
     * @param data        data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        AppLog.v(this, "onActivityResult(): " + requestCode);
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Return the fragment which is currently displayed in R.id.fragment_container
     *
     * @return top fragment or null if there is none displayed
     */
    private CustomFragment getTopFragment() {
        return (CustomFragment) fm.findFragmentById(R.id.fragment_container);
    }

    /**
     * Handle presses on the back button
     */
    @Override
    public void onBackPressed() {
        AppLog.v(this, "onBackPressed()");
        if (navDrawer.isDrawerOpen(navView)) {
            navDrawer.closeDrawer(navView);
            return;
        }
        CustomFragment top = getTopFragment();
        if (top != null) {
            AppLog.v(this, "Top Fragment is not null");
            if (!top.onBackPressed()) {
                AppLog.v(this, "Top Fragment.onBackPressed was false");
                AppLog.v(this, "BackStackEntryCount: " + fm.getBackStackEntryCount());
                if (fm.getBackStackEntryCount() > 0) {
                    fm.popBackStack();
                } else {
                    snackbarExitApp.show();
                }
                return;
            } else {
                AppLog.v(this, "Top Fragment.onBackPressed was true");
                return;
            }
        }

        if (!snackbarExitApp.isShown()) {
            snackbarExitApp.show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        customTabActivityHelper.bindCustomTabsService(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        customTabActivityHelper.unbindCustomTabsService(this);
    }

    @Override
    protected void onPause() {
        AppLog.v(this, "onPause()");
        AppLog.v(this, "Unregister BroadcastReceivers");
        LocalBroadcastManager.getInstance(this).unregisterReceiver(brSetTitle);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(brOpenExternalLink);
        super.onPause();
    }

    @Override
    protected void onResume() {
        AppLog.v(this, "onResume()");
        super.onResume();
        AppLog.v(this, "Register BroadcastReceivers");
        LocalBroadcastManager.getInstance(this).registerReceiver(brSetTitle, new IntentFilter(ACTION_UPDATE_TITLE_FROM_URL));
        LocalBroadcastManager.getInstance(this).registerReceiver(brOpenExternalLink, new IntentFilter(ACTION_OPEN_EXTERNAL_URL));
        invalidateOptionsMenu();
        _appSettings = getAppSettings();
        if (_appSettings.isRecreateMainActivity()) {
            recreate();
        }
        setToolbarIntellihide(_appSettings.isIntellihideToolbars());
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(AppSettings.get().isShowTitleInMainView());
        }
        updateNavigationViewEntryVisibilities();
    }

    /**
     * Clear and repopulate top and bottom _toolbar.
     * Also add menu items of the displayed fragment
     *
     * @param menu top _toolbar
     * @return boolean
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        AppLog.v(this, "onCreateOptionsMenu()");
        boolean cache;

        //Clear the menus
        menu.clear();

        CustomFragment top = getTopFragment();
        if (top != null) {
            if (!top.getFragmentTag().equals(PodSelectionFragment.TAG)) {
                cache = _appSettings.isExtendedNotificationsActivated();
                getMenuInflater().inflate(R.menu.main__menu_top, menu);
                menu.findItem(R.id.action_notifications).setVisible(!cache);
                menu.findItem(R.id.action_notifications_extended).setVisible(cache);
            }
        }

        final boolean darkBg = ContextUtils.get().shouldColorOnTopBeLight(AppSettings.get().getPrimaryColor());
        ContextUtils.get()
                .tintMenuItems(menu, true, ContextCompat.getColor(this, darkBg ? R.color.white : R.color.black))
                .setSubMenuIconsVisiblity(menu, true);

        return true;
    }

    /**
     * Set the notification and messages counter in the top _toolbar
     *
     * @param menu menu
     * @return boolean
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item;
        updateNavigationViewEntryVisibilities();

        if ((item = menu.findItem(R.id.action_notifications)) != null) {
            LayerDrawable icon = (LayerDrawable) item.getIcon();
            BadgeDrawable.setBadgeCount(this, icon, diasporaUserProfile.getNotificationCount());
        }

        if ((item = menu.findItem(R.id.action_conversations)) != null) {
            LayerDrawable icon = (LayerDrawable) item.getIcon();
            BadgeDrawable.setBadgeCount(this, icon, diasporaUserProfile.getUnreadMessagesCount());
        }
        return super.onPrepareOptionsMenu(menu);
    }

    /**
     * Handle clicks on the optionsmenu
     *
     * @param item item
     * @return boolean
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        AppLog.i(this, "onOptionsItemSelected()");
        switch (item.getItemId()) {
            case R.id.action_notifications: {
                if (_appSettings.isExtendedNotificationsActivated()) {
                    return true;
                }
                //Otherwise we execute the action of action_notifications_all
            }
            case R.id.action_notifications_all: {
                if (WebHelper.isOnline(MainActivity.this)) {
                    openDiasporaUrl(urls.getNotificationsUrl());
                    return true;
                } else {
                    snackbarNoInternet.show();
                    return false;
                }
            }


            case R.id.action_notifications_also_commented: {
                if (WebHelper.isOnline(MainActivity.this)) {
                    openDiasporaUrl(urls.getSuburlNotificationsAlsoCommentedUrl());
                    return true;
                } else {
                    snackbarNoInternet.show();
                    return false;
                }
            }

            case R.id.action_notifications_comment_on_post: {
                if (WebHelper.isOnline(MainActivity.this)) {
                    openDiasporaUrl(urls.getSuburlNotificationsCommentOnPostUrl());
                    return true;
                } else {
                    snackbarNoInternet.show();
                    return false;
                }
            }

            case R.id.action_notifications_liked: {
                if (WebHelper.isOnline(MainActivity.this)) {
                    openDiasporaUrl(urls.getSuburlNotificationsLikedUrl());
                    return true;
                } else {
                    snackbarNoInternet.show();
                    return false;
                }
            }

            case R.id.action_notifications_mentioned: {
                if (WebHelper.isOnline(MainActivity.this)) {
                    openDiasporaUrl(urls.getSuburlNotificationsMentionedUrl());
                    return true;
                } else {
                    snackbarNoInternet.show();
                    return false;
                }
            }

            case R.id.action_notifications_reshared: {
                if (WebHelper.isOnline(MainActivity.this)) {
                    openDiasporaUrl(urls.getSuburlNotificationsResharedUrl());
                    return true;
                } else {
                    snackbarNoInternet.show();
                    return false;
                }
            }

            case R.id.action_notifications_started_sharing: {
                if (WebHelper.isOnline(MainActivity.this)) {
                    openDiasporaUrl(urls.getSuburlNotificationsStartedSharingUrl());
                    return true;
                } else {
                    snackbarNoInternet.show();
                    return false;
                }
            }

            case R.id.action_conversations: {
                if (WebHelper.isOnline(MainActivity.this)) {
                    openDiasporaUrl(urls.getConversationsUrl());
                    return true;
                } else {
                    snackbarNoInternet.show();
                    return false;
                }
            }

            case R.id.action_compose: {
                if (WebHelper.isOnline(MainActivity.this)) {
                    openDiasporaUrl(urls.getNewPostUrl());
                } else {
                    snackbarNoInternet.show();
                }
                return true;
            }

            case R.id.action_search: {
                if (WebHelper.isOnline(MainActivity.this)) {
                    final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

                    @SuppressLint("InflateParams") View layout = getLayoutInflater().inflate(R.layout.ui__dialog_search__people_tags, null, false);
                    final EditText input = (EditText) layout.findViewById(R.id.dialog_search__input);
                    input.setMaxLines(1);
                    input.setSingleLine(true);
                    ThemeHelper.updateEditTextColor(input);
                    final DialogInterface.OnClickListener clickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int which) {
                            String query = input.getText().toString().trim().replaceAll((which == DialogInterface.BUTTON_NEGATIVE ? "\\*" : "\\#"), "");
                            if (query.equals("")) {
                                Snackbar.make(fragmentContainer, R.string.search_alert_bypeople_validate_needsomedata, Snackbar.LENGTH_LONG).show();
                            } else {
                                openDiasporaUrl(which == DialogInterface.BUTTON_NEGATIVE ? urls.getSearchPeopleUrl(query) : urls.getSearchTagsUrl(query));
                            }
                            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                            imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
                        }
                    };

                    final AlertDialog dialog = new ThemedAlertDialogBuilder(this, _appSettings)
                            .setView(layout).setTitle(R.string.search_alert_title)
                            .setCancelable(true)
                            .setPositiveButton(R.string.search_alert_tag, clickListener)
                            .setNegativeButton(R.string.search_alert_people, clickListener)
                            .create();

                    input.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                            if (actionId == EditorInfo.IME_ACTION_DONE) {
                                dialog.hide();
                                clickListener.onClick(null, 0);
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
                    snackbarNoInternet.show();
                }
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onUserProfileNameChanged(DiasporaUserProfile diasporaUserProfile, String name) {
        AppLog.i(this, "onUserProfileNameChanged()");
        // Update the profile name in the navigation slider
        navheaderTitle.setText(name);
    }

    @Override
    public void onUserProfileAvatarChanged(DiasporaUserProfile diasporaUserProfile, String avatarUrl) {
        AppLog.i(this, "onUserProfileAvatarChanged()");
        // Update the profile picture in the navigation slider
        app.getAvatarImageLoader().startImageDownload(navheaderImage, avatarUrl);
    }

    /**
     * Handle hashtag clicks. Open the new-post-url and inject the clicked hashtag into the post-editor
     *
     * @param intent intent
     */
    private void handleHashtag(Intent intent) {
        AppLog.v(this, "handleHashtag()");
        try {
            setSharedTexts(null, intent.getData().toString().split("/")[3]);
        } catch (Exception e) {
            AppLog.e(this, e.toString());
        }
        openDiasporaUrl(urls.getNewPostUrl());
    }

    /**
     * Open the new-post-url and inject text that was shared into the app into the post editors text field
     *
     * @param intent shareTextIntent
     */
    private void handleSendText(Intent intent) {
        AppLog.v(this, "handleSendText()");
        try {
            setSharedTexts(null, intent.getStringExtra(Intent.EXTRA_TEXT));
            openDiasporaUrl(urls.getNewPostUrl());
        } catch (Exception e) {
            AppLog.e(this, e.toString());
        }
    }

    /**
     * Handle sent text + subject
     *
     * @param intent intent
     */
    private void handleSendSubject(Intent intent) {
        AppLog.v(this, "handleSendSubject()");
        try {
            setSharedTexts(intent.getStringExtra(Intent.EXTRA_SUBJECT), intent.getStringExtra(Intent.EXTRA_TEXT));
            openDiasporaUrl(urls.getNewPostUrl());
        } catch (Exception e) {
            AppLog.e(this, e.toString());
        }
    }

    /**
     * TODO: MOVE
     * Set sharedText variable to escaped and formatted subject + body.
     * If subject is null, only the body will be set. Else the subject will be set as header.
     * Depending on whether the user has the setting isAppendSharedViaApp set, a reference to
     * the app will be added at the bottom
     *
     * @param sharedSubject post subject or null
     * @param sharedBody    post text
     */
    private void setSharedTexts(String sharedSubject, String sharedBody) {
        AppLog.i(this, "setSharedTexts()");
        String body = WebHelper.replaceUrlWithMarkdown(sharedBody);
        if (_appSettings.isAppendSharedViaApp()) {
            AppLog.v(this, "Append app reference to shared text");
            body = body + "\n\n" + getString(R.string.shared_via_app);
        }
        final String escapedBody = WebHelper.escapeHtmlText(body);
        if (sharedSubject != null) {
            AppLog.v(this, "Append subject to shared text");
            String escapedSubject = WebHelper.escapeHtmlText(WebHelper.replaceUrlWithMarkdown(sharedSubject));
            AppLog.v(this, "Set shared text; Subject: \"" + escapedSubject + "\" Body: \"" + escapedBody + "\"");
            textToBeShared = "**" + escapedSubject + "** " + escapedBody;
        } else {
            AppLog.v(this, "Set shared text; Subject: \"null\" Body: \"" + sharedBody + "\"");
            textToBeShared = escapedBody;
        }
    }

    /**
     * Share an image shared to the app via diaspora
     *
     * @param intent shareImageIntent
     */
    //TODO: Implement some day
    private void handleSendImage(Intent intent) {
        AppLog.i(this, "handleSendImage()");
        final Uri imageUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (imageUri != null) {
            AppLog.v(this, "imageUri is not null. Handle shared image");
        } else {
            AppLog.w(this, "imageUri is null. Cannot precede.");
        }
        Toast.makeText(this, "Not yet implemented.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNotificationCountChanged(DiasporaUserProfile diasporaUserProfile, int notificationCount) {
        AppLog.i(this, "onNotificationCountChanged()");
        // Count saved in DiasporaUserProfile
        // Invalidate the top _toolbar to update the unread messages counter
        invalidateOptionsMenu();
    }


    @Override
    public void onUnreadMessageCountChanged(DiasporaUserProfile diasporaUserProfile, int unreadMessageCount) {
        AppLog.i(this, "onUnreadMessageCountChanged()");
        // Count saved in DiasporaUserProfile
        // Invalidate the top _toolbar to update the unread messages counter
        invalidateOptionsMenu();
    }

    @Override
    public void onCustomTabsConnected() {
        if (customTabsSession == null) {
            AppLog.i(this, "CustomTabs warmup: " + customTabActivityHelper.warmup(0));
            customTabsSession = customTabActivityHelper.getSession();
        }
    }

    @Override
    public void onPodSelectionDialogResult(DiasporaPodList.DiasporaPod pod, boolean accepted) {
        if (accepted) {
            invalidateOptionsMenu();
            navheaderDescription.setText(pod.getName());
        }
    }

    @Override
    public void onCustomTabsDisconnected() {

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        AppLog.v(this, "onNavigationItemsSelected()");
        // Handle navigation view item clicks here.
        switch (item.getItemId()) {
            case R.id.nav_stream: {
                if (WebHelper.isOnline(MainActivity.this)) {
                    openDiasporaUrl(urls.getStreamUrl());
                } else {
                    snackbarNoInternet.show();
                }
            }
            break;

            case R.id.nav_profile: {
                if (!_appSettings.getProfileId().equals("")) {
                    openDiasporaUrl(urls.getProfileUrl());
                }
            }
            break;

            case R.id.nav_followed_tags: {
                showFragment(getFragment(TagListFragment.TAG));
            }
            break;

            case R.id.nav_aspects: {
                showFragment(getFragment(AspectListFragment.TAG));
            }
            break;

            case R.id.nav_contacts: {
                if (WebHelper.isOnline(MainActivity.this)) {
                    openDiasporaUrl(urls.getContactsUrl());
                } else {
                    snackbarNoInternet.show();
                }
            }
            break;

            case R.id.nav_activities: {
                if (WebHelper.isOnline(MainActivity.this)) {
                    openDiasporaUrl(urls.getActivityUrl());
                } else {
                    snackbarNoInternet.show();
                }
            }
            break;

            case R.id.nav_liked: {
                if (WebHelper.isOnline(MainActivity.this)) {
                    openDiasporaUrl(urls.getLikedPostsUrl());
                } else {
                    snackbarNoInternet.show();
                }
            }
            break;

            case R.id.nav_commented: {
                if (WebHelper.isOnline(MainActivity.this)) {
                    openDiasporaUrl(urls.getCommentedUrl());
                } else {
                    snackbarNoInternet.show();
                }
            }
            break;

            case R.id.nav_mentions: {
                if (WebHelper.isOnline(MainActivity.this)) {
                    openDiasporaUrl(urls.getMentionsUrl());
                } else {
                    snackbarNoInternet.show();
                }
            }
            break;

            case R.id.nav_public: {
                if (WebHelper.isOnline(MainActivity.this)) {
                    openDiasporaUrl(urls.getPublicUrl());
                } else {
                    snackbarNoInternet.show();
                }
            }
            break;

            case R.id.nav_reports: {
                if (WebHelper.isOnline(MainActivity.this)) {
                    openDiasporaUrl(urls.getReportsUrl());
                } else {
                    snackbarNoInternet.show();
                }
            }
            break;

            case R.id.nav_statistics: {
                if (WebHelper.isOnline(MainActivity.this)) {
                    openDiasporaUrl(urls.getStatisticsUrl());
                } else {
                    snackbarNoInternet.show();
                }
            }
            break;

            case R.id.nav_toggle_desktop_page: {
                openDiasporaUrl(urls.getToggleMobileUrl());
            }
            break;

            case R.id.nav_dandelion: {
                openDiasporaUrl(urls.getProfileUrl("48b78420923501341ef3782bcb452bd5"));
            }
            break;

            case R.id.nav_exit: {
                moveTaskToBack(true);
                finish();
            }
            break;

            case R.id.nav_settings: {
                startActivity(new Intent(this, SettingsActivity.class));
            }
            break;

            case R.id.nav_about: {
                startActivity(new Intent(MainActivity.this, AboutActivity.class));
            }
            break;
        }

        navDrawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * React to results of requestPermission
     *
     * @param requestCode  resCode
     * @param permissions  requested permissions
     * @param grantResults granted results
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE__ACCESS_EXTERNAL_STORAGE:
                if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    AppLog.i(this, "onRequestPermissionsResult: Permission to access external storage granted");
                    Toast.makeText(this, R.string.permission_granted_try_again, Toast.LENGTH_SHORT).show();
                } else {
                    AppLog.w(this, "onRequestPermissionsResult: Permission to access external storage denied");
                    Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_SHORT).show();
                }
                return;

            default:
                super.onRequestPermissionsResult(requestCode, permissions,
                        grantResults);
        }
    }

    /**
     * Return the string that will be shared into the new-post-editor
     *
     * @return String
     */
    public String getTextToBeShared() {
        return textToBeShared;
    }

    /**
     * Set the string that will be shared into the new-post-editor
     *
     * @param textToBeShared text that will be shared into the post-editor
     */
    public void setTextToBeShared(String textToBeShared) {
        this.textToBeShared = textToBeShared;
    }

    @Override
    protected void applyColorToViews() {
        ThemeHelper.updateToolbarColor(toolbarTop);
        navDrawerLayout.setBackgroundColor(_appSettings.getPrimaryColor());
        navProfilePictureArea.setBackgroundColor(_appSettings.getPrimaryColor());
        if (_appSettings.isAmoledColorMode()) {
            navView.setItemTextColor(ColorStateList.valueOf(Color.GRAY));
            navView.setItemIconTintList(ColorStateList.valueOf(Color.GRAY));
            navView.setBackgroundColor(Color.BLACK);
            navheaderTitle.setTextColor(Color.GRAY);
            navheaderDescription.setTextColor(Color.DKGRAY);
        }

        int popupTheme = ContextUtils.get().shouldColorOnTopBeLight(AppSettings.get().getPrimaryColor())
                ? R.style.AppTheme_PopupOverlay_Dark : R.style.AppTheme_PopupOverlay_Light;
        toolbarTop.setPopupTheme(popupTheme);
    }

    public void setToolbarIntellihide(boolean enable) {
        AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) toolbarTop.getLayoutParams();
        if (enable) {
            AppLog.d(this, "Enable Intellihide");
            params.setScrollFlags(toolbarDefaultScrollFlags);

        } else {
            AppLog.d(this, "Disable Intellihide");
            params.setScrollFlags(0);  // clear all scroll flags
        }
        appBarLayout.setExpanded(true, true);
    }
}
