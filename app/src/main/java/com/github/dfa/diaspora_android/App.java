package com.github.dfa.diaspora_android;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.github.dfa.diaspora_android.data.AppSettings;
import com.github.dfa.diaspora_android.data.PodUserProfile;
import com.github.dfa.diaspora_android.ui.CustomWebViewClient;
import com.github.dfa.diaspora_android.util.AvatarImageLoader;

/**
 * Created by gregor on 24.03.16.
 */
public class App extends Application {
    public static final String TAG = "DIASPORA_";

    private AppSettings appSettings;
    private AvatarImageLoader avatarImageLoader;
    private CookieManager cookieManager;
    private PodUserProfile podUserProfile;

    @Override
    public void onCreate() {
        super.onCreate();
        final Context c = getApplicationContext();
        appSettings = new AppSettings(c);
        avatarImageLoader = new AvatarImageLoader(c);
        podUserProfile = new PodUserProfile(this);


        // Get cookie manager
        cookieManager = CookieManager.getInstance();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            CookieSyncManager.createInstance(c);
        }
        cookieManager.setAcceptCookie(true);
    }

    public void resetPodData(@Nullable WebView webView){
        if(webView != null){
            webView.stopLoading();
            webView.loadUrl("about:blank");
            webView.clearFormData();
            webView.clearHistory();
            webView.clearCache(true);
        }

        // Clear avatar image
        new AvatarImageLoader(this).clearAvatarImage();

        // Clear preferences
        appSettings.clearPodSettings();

        // Clear cookies
        cookieManager.removeAllCookie();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cookieManager.removeAllCookies(null);
        }
    }

    public PodUserProfile getPodUserProfile(){
        return podUserProfile;
    }

    public AppSettings getSettings() {
        return appSettings;
    }

    public AvatarImageLoader getAvatarImageLoader() {
        return avatarImageLoader;
    }

    public CookieManager getCookieManager() {
        return cookieManager;
    }
}
