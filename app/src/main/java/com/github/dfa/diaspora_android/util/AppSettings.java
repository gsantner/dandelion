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
package com.github.dfa.diaspora_android.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import com.github.dfa.diaspora_android.R;
import com.github.dfa.diaspora_android.data.DiasporaAspect;
import com.github.dfa.diaspora_android.data.DiasporaPodList.DiasporaPod;
import com.github.dfa.diaspora_android.data.DiasporaPodList.DiasporaPod.DiasporaPodUrl;
import com.github.dfa.diaspora_android.web.ProxyHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Settings
 * Created by gsantner (https://gsantner.github.io/) on 20.03.16. Part of Diaspora for Android.
 */
public class AppSettings {
    private final SharedPreferences prefApp;
    private final SharedPreferences prefPod;
    private final Context context;
    private DiasporaPod currentPod0Cached;

    public AppSettings(Context context) {
        this.context = context.getApplicationContext();
        prefApp = this.context.getSharedPreferences("app", Context.MODE_PRIVATE);
        prefPod = this.context.getSharedPreferences("pod0", Context.MODE_PRIVATE);
    }

    public Context getApplicationContext() {
        return context;
    }

    /**
     * Clear all settings in prefPod (Settings related to the configured pod)
     * This uses commit instead of apply, since
     * SettingsActivity.SettingsFragmentDebugging.showWipeSettingsDialog()
     * kills the app after the calling this, so we have to block until we are finished.
     */
    @SuppressLint("CommitPrefEdits")
    public void clearPodSettings() {
        prefPod.edit().clear().commit();
    }

    /**
     * Clear all settings in prefApp (related to the App itself)
     * This uses commit instead of apply, since
     * SettingsActivity.SettingsFragmentDebugging.showWipeSettingsDialog()
     * kills the app after the calling this, so we have to block until we are finished.
     */
    @SuppressLint("CommitPrefEdits")
    public void clearAppSettings() {
        prefApp.edit().clear().commit();
    }

    public String getKey(int stringKeyResourceId) {
        return context.getString(stringKeyResourceId);
    }

    public boolean isKeyEqual(String key, int stringKeyRessourceId) {
        return key.equals(getKey(stringKeyRessourceId));
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

    private String getString(SharedPreferences pref, int ressourceId, int ressourceIdDefaultValue) {
        return pref.getString(context.getString(ressourceId), context.getString(ressourceIdDefaultValue));
    }

    private boolean getBoolean(SharedPreferences pref, int ressourceId, boolean defaultValue) {
        return pref.getBoolean(context.getString(ressourceId), defaultValue);
    }

    private int getInt(SharedPreferences pref, int ressourceId, int defaultValue) {
        return pref.getInt(context.getString(ressourceId), defaultValue);
    }

    public int getColor(SharedPreferences pref, String key, int defaultColor) {
        return pref.getInt(key, defaultColor);
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

    public DiasporaPod getPod() {
        if (currentPod0Cached == null) {
            String pref = getString(prefPod, R.string.pref_key__current_pod_0, "");

            try {
                currentPod0Cached = new DiasporaPod().fromJson(new JSONObject(pref));
            } catch (JSONException e) {
                currentPod0Cached = null;
            }
        }
        return currentPod0Cached;
    }

    public void setPod(DiasporaPod pod) {
        try {
            setString(prefPod, R.string.pref_key__current_pod_0,
                    pod == null ? null : pod.toJson().toString());
            currentPod0Cached = pod;
        } catch (JSONException ignored) {
        }
    }

    public boolean hasPod() {
        return !getString(prefPod, R.string.pref_key__current_pod_0, "").equals("");
    }

    public void setPodAspects(DiasporaAspect[] aspects) {
        setStringArray(prefPod, R.string.pref_key__podprofile_aspects, aspects);
    }

    public DiasporaAspect[] getAspects() {
        String[] s = getStringArray(prefPod, R.string.pref_key__podprofile_aspects);
        DiasporaAspect[] aspects = new DiasporaAspect[s.length];
        for (int i = 0; i < aspects.length; i++) {
            aspects[i] = new DiasporaAspect(s[i]);
        }
        return aspects;
    }

    public String[] getFollowedTags() {
        return getStringArray(prefPod, R.string.pref_key__podprofile_followed_tags);
    }

    public void setFollowedTags(String[] values) {
        setStringArray(prefPod, R.string.pref_key__podprofile_followed_tags, values);
    }

    public String[] getFollowedTagsFavs() {
        return getStringArray(prefPod, R.string.pref_key__podprofile_followed_tags_favs);
    }

    public void setFollowedTagsFavs(List<String> values) {
        setStringArray(prefPod, R.string.pref_key__podprofile_followed_tags_favs, values.toArray(new String[values.size()]));
    }

    public String[] getAspectFavs() {
        return getStringArray(prefPod, R.string.pref_key__podprofile_aspects_favs);
    }

    public void setAspectFavs(List<String> values) {
        setStringArray(prefPod, R.string.pref_key__podprofile_aspects_favs, values.toArray(new String[values.size()]));
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
    public void setProxyHttpEnabled(boolean enabled) {
        //commit instead of apply because the app is likely to be killed before apply is called.
        prefApp.edit().putBoolean(context.getString(R.string.pref_key__http_proxy_enabled), enabled).commit();
    }

    /**
     * Default return value: false
     *
     * @return whether proxy is enabled or not
     */
    public boolean isProxyHttpEnabled() {
        return getBoolean(prefApp, R.string.pref_key__http_proxy_enabled, false);
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
    public String getProxyHttpHost() {
        return getString(prefApp, R.string.pref_key__http_proxy_host, "");
    }

    public void setProxyHttpHost(String value) {
        setString(prefApp, R.string.pref_key__http_proxy_host, value);
    }

    /**
     * Default value: 0
     *
     * @return proxy port
     */
    public int getProxyHttpPort() {
        try {
            String str = getString(prefApp, R.string.pref_key__http_proxy_port, "0");
            return Integer.parseInt(str);
        } catch (ClassCastException e) {
            int port = getInt(prefApp, R.string.pref_key__http_proxy_port, 0);
            setProxyHttpPort(port);
            return port;
        }
    }

    public void setProxyHttpPort(int value) {
        setString(prefApp, R.string.pref_key__http_proxy_port, Integer.toString(value));
    }

    public ProxyHandler.ProxySettings getProxySettings() {
        return new ProxyHandler.ProxySettings(isProxyHttpEnabled(), getProxyHttpHost(), getProxyHttpPort());
    }

    public boolean isIntellihideToolbars() {
        return getBoolean(prefApp, R.string.pref_key__intellihide_toolbars, true);
    }

    public boolean isChromeCustomTabsEnabled() {
        return getBoolean(prefApp, R.string.pref_key__chrome_custom_tabs_enabled, true);
    }

    public boolean isLoggingEnabled() {
        return getBoolean(prefApp, R.string.pref_key__logging_enabled, false);
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
        return getBoolean(prefApp, R.string.pref_key__visibility_nav__profile, true);
    }

    public boolean isVisibleInNavContacts() {
        return getBoolean(prefApp, R.string.pref_key__visibility_nav__contacts, false);
    }

    public boolean isVisibleInNavReports() {
        return getBoolean(prefApp, R.string.pref_key__visibility_nav__reports, false);
    }

    public boolean isTopbarStreamShortcutEnabled() {
        return getBoolean(prefApp, R.string.pref_key__topbar_stream_shortcut, false);
    }

    public String getScreenRotation() {
        return getString(prefApp, R.string.pref_key__screen_rotation, R.string.rotation_val_system);
    }

    public void setPrimaryColorSettings(int base, int shade) {
        setInt(prefApp, R.string.pref_key__primary_color_base, base);
        setInt(prefApp, R.string.pref_key__primary_color_shade, shade);
    }

    public int[] getPrimaryColorSettings() {
        return new int[]{
                getInt(prefApp, R.string.pref_key__primary_color_base, context.getResources().getColor(R.color.md_blue_500)),
                getInt(prefApp, R.string.pref_key__primary_color_shade, context.getResources().getColor(R.color.primary))
        };
    }

    public int getPrimaryColor() {
        return getInt(prefApp, R.string.pref_key__primary_color_shade, context.getResources().getColor(R.color.primary));
    }

    public void setAccentColorSettings(int base, int shade) {
        setInt(prefApp, R.string.pref_key__accent_color_base, base);
        setInt(prefApp, R.string.pref_key__accent_color_shade, shade);
    }

    public int[] getAccentColorSettings() {
        return new int[]{
                getInt(prefApp, R.string.pref_key__accent_color_base, context.getResources().getColor(R.color.md_deep_orange_500)),
                getInt(prefApp, R.string.pref_key__accent_color_shade, context.getResources().getColor(R.color.accent))
        };
    }

    public int getAccentColor() {
        return getInt(prefApp, R.string.pref_key__accent_color_shade, context.getResources().getColor(R.color.accent));
    }

    public boolean isExtendedNotificationsActivated() {
        return getBoolean(prefApp, R.string.pref_key__extended_notifications, false);
    }

    public void registerPrefAppPreferenceChangedListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        prefApp.registerOnSharedPreferenceChangeListener(listener);
    }

    public void unregisterPrefAppPreferenceChangedListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        prefApp.unregisterOnSharedPreferenceChangeListener(listener);
    }
}