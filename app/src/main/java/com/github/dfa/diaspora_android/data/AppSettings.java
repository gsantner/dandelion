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
package com.github.dfa.diaspora_android.data;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import com.github.dfa.diaspora_android.R;

/**
 * Settings
 * Created by gsantner (https://gsantner.github.io/) on 20.03.16. Part of Diaspora for Android.
 */
public class AppSettings {
    private final SharedPreferences prefApp;
    private final SharedPreferences prefPod;
    private final Context context;

    public AppSettings(Context context) {
        this.context = context.getApplicationContext();
        prefApp = this.context.getSharedPreferences("app", Context.MODE_PRIVATE);
        prefPod = this.context.getSharedPreferences("pod0", Context.MODE_PRIVATE);
    }

    public Context getApplicationContext() {
        return context;
    }

    public void clearPodSettings() {
        prefPod.edit().clear().apply();
    }

    public void clearAppSettings() {
        prefApp.edit().clear().apply();
    }

    private void setString(SharedPreferences pref, int keyRessourceId, String value) {
        pref.edit().putString(context.getString(keyRessourceId), value).apply();
    }

    private void setInt(SharedPreferences pref, int keyRessourceId, int value) {
        pref.edit().putInt(context.getString(keyRessourceId), value).apply();
    }

    private void setBool(SharedPreferences pref, int keyRessourceId, boolean value) {
        pref.edit().putBoolean(context.getString(keyRessourceId), value).apply();
    }

    private void setStringArray(SharedPreferences pref, int keyRessourceId, Object[] values) {
        StringBuilder sb = new StringBuilder();
        for (Object value : values) {
            sb.append("%%%");
            sb.append(value.toString());
        }
        setString(pref, keyRessourceId, sb.toString().replaceFirst("%%%", ""));
    }

    private String[] getStringArray(SharedPreferences pref, int keyRessourceId) {
        String value = pref.getString(context.getString(keyRessourceId), "%%%");
        if (value.equals("%%%")) {
            return new String[0];
        }
        return value.split("%%%");
    }

    private String getString(SharedPreferences pref, int ressourceId, String defaultValue) {
        return pref.getString(context.getString(ressourceId), defaultValue);
    }

    private boolean getBoolean(SharedPreferences pref, int ressourceId, boolean defaultValue) {
        return pref.getBoolean(context.getString(ressourceId), defaultValue);
    }

    private int getInt(SharedPreferences pref, int ressourceId, int defaultValue) {
        return pref.getInt(context.getString(ressourceId), defaultValue);
    }


    /*
    //     Setters & Getters
    */
    public String getProfileId() {
        return getString(prefPod, R.string.pref_key__podprofile_id, "");
    }

    public void setProfileId(String profileId) {
        setString(prefPod, R.string.pref_key__podprofile_id, profileId);
    }

    public boolean isLoadImages() {
        return getBoolean(prefApp, R.string.pref_key__load_images, true);
    }

    public int getMinimumFontSize() {
        switch (getString(prefApp, R.string.pref_key__font_size, "")) {
            case "huge":
                return 20;
            case "large":
                return 16;
            case "normal":
                return 8;
            default:
                setString(prefApp, R.string.pref_key__font_size, "normal");
                return 8;
        }
    }

    public String getAvatarUrl() {
        return getString(prefPod, R.string.pref_key__podprofile_avatar_url, "");
    }

    public void setAvatarUrl(String avatarUrl) {
        setString(prefPod, R.string.pref_key__podprofile_avatar_url, avatarUrl);
    }

    public String getName() {
        return getString(prefPod, R.string.pref_key__podprofile_name, "");
    }

    public void setName(String name) {
        setString(prefPod, R.string.pref_key__podprofile_name, name);
    }

    public String getPodDomain() {
        return getString(prefPod, R.string.pref_key__poddomain, "");
    }

    public void setPodDomain(String podDomain) {
        setString(prefPod, R.string.pref_key__poddomain, podDomain);
    }

    public boolean hasPodDomain() {
        return !getString(prefPod, R.string.pref_key__poddomain, "").equals("");
    }

    public String[] getPreviousPodlist() {
        return getStringArray(prefApp, R.string.pref_key__previous_podlist);
    }

    public void setPreviousPodlist(String[] pods) {
        setStringArray(prefApp, R.string.pref_key__previous_podlist, pods);
    }

    public void setPodAspects(PodAspect[] aspects) {
        setStringArray(prefPod, R.string.pref_key__podprofile_aspects, aspects);
    }

    public PodAspect[] getPodAspects() {
        String[] s = getStringArray(prefPod, R.string.pref_key__podprofile_aspects);
        PodAspect[] aspects = new PodAspect[s.length];
        for (int i = 0; i < aspects.length; i++) {
            aspects[i] = new PodAspect(s[i]);
        }
        return aspects;
    }

    public String[] getFollowedTags() {
        return getStringArray(prefPod, R.string.pref_key__podprofile_followed_tags);
    }

    public void setFollowedTags(String[] tags) {
        setStringArray(prefPod, R.string.pref_key__podprofile_followed_tags, tags);
    }

    public int getUnreadMessageCount() {
        return getInt(prefPod, R.string.pref_key__podprofile_unread_message_count, 0);
    }

    public void setUnreadMessageCount(int unreadMessageCount) {
        setInt(prefPod, R.string.pref_key__podprofile_unread_message_count, unreadMessageCount);
    }

    public int getNotificationCount() {
        return getInt(prefPod, R.string.pref_key__podprofile_notification_count, 0);
    }

    public void setNotificationCount(int notificationCount) {
        setInt(prefPod, R.string.pref_key__podprofile_notification_count, notificationCount);
    }

    public boolean isAppendSharedViaApp() {
        return getBoolean(prefApp, R.string.pref_key__append_shared_via_app, true);
    }

    @SuppressLint("CommitPrefEdits")
    public void setProxyEnabled(boolean enabled) {
        //commit instead of apply because the app is likely to be killed before apply is called.
        prefApp.edit().putBoolean(context.getString(R.string.pref_key__proxy_enabled), enabled).commit();
    }

    /**
     * Default return value: false
     *
     * @return whether proxy is enabled or not
     */
    public boolean isProxyEnabled() {
        return getBoolean(prefApp, R.string.pref_key__proxy_enabled, false);
    }

    public boolean wasProxyEnabled() {
        return getBoolean(prefApp, R.string.pref_key__proxy_was_enabled, false);
    }

    /**
     * Needed in order to determine, whether the proxy has just been disabled (trigger app restart)
     * or if proxy was disabled before (do not restart app)
     *
     * @param b new value
     */
    @SuppressLint("CommitPrefEdits")
    public void setProxyWasEnabled(boolean b) {
        prefApp.edit().putBoolean(context.getString(R.string.pref_key__proxy_was_enabled), b).commit();
    }

    /**
     * Default value: ""
     *
     * @return proxy host
     */
    public String getProxyHost() {
        return getString(prefApp, R.string.pref_key__proxy_host, "");
    }

    /**
     * Default value: 0
     *
     * @return proxy port
     */
    public int getProxyPort() {
        try {
            return Integer.parseInt(getString(prefApp, R.string.pref_key__proxy_port, "0"));
        } catch (Exception e) {
            setString(prefApp, R.string.pref_key__proxy_port, "0");
            return 0;
        }
    }

    public boolean isIntellihideToolbars() {
        return getBoolean(prefApp, R.string.pref_key__intellihide_toolbars, false);
    }

    public boolean isChromeCustomTabsEnabled() {
        return getBoolean(prefApp, R.string.pref_key__chrome_custom_tabs_enabled, true);
    }

    public boolean isPostContextMenuEnabled() {
        return getBoolean(prefApp, R.string.pref_key__post_link_context_menu_enabled, false);
    }

    public boolean isLoggingEnabled() {
        return getBoolean(prefApp, R.string.pref_key__logging_enabled, true);
    }

    public boolean isLoggingSpamEnabled() {
        return getBoolean(prefApp, R.string.pref_key__logging_spam_enabled, false);
    }

    public boolean isVisibleInNavExit() {
        return getBoolean(prefApp, R.string.pref_key__visibility_nav__exit, false);
    }

    public boolean isVisibleInNavHelp_license() {
        return getBoolean(prefApp, R.string.pref_key__visibility_nav__help_license, true);
    }

    public boolean isVisibleInNavPublic_activities() {
        return getBoolean(prefApp, R.string.pref_key__visibility_nav__public_activities, false);
    }

    public boolean isVisibleInNavMentions() {
        return getBoolean(prefApp, R.string.pref_key__visibility_nav__mentions, false);
    }

    public boolean isVisibleInNavCommented() {
        return getBoolean(prefApp, R.string.pref_key__visibility_nav__commented, true);
    }

    public boolean isVisibleInNavLiked() {
        return getBoolean(prefApp, R.string.pref_key__visibility_nav__liked, true);
    }

    public boolean isVisibleInNavActivities() {
        return getBoolean(prefApp, R.string.pref_key__visibility_nav__activities, true);
    }

    public boolean isVisibleInNavAspects() {
        return getBoolean(prefApp, R.string.pref_key__visibility_nav__aspects, true);
    }

    public boolean isVisibleInNavFollowed_tags() {
        return getBoolean(prefApp, R.string.pref_key__visibility_nav__followed_tags, true);
    }

    public boolean isVisibleInNavProfile() {
        return getBoolean(prefApp, R.string.pref_key__visibility_nav__profile, false);
    }
}
