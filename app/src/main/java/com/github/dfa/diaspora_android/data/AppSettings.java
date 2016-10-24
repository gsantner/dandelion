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
import android.os.Build;

import com.github.dfa.diaspora_android.R;
import com.github.dfa.diaspora_android.data.DiasporaPodList.DiasporaPod;
import com.github.dfa.diaspora_android.data.DiasporaPodList.DiasporaPod.DiasporaPodUrl;
import com.github.dfa.diaspora_android.ui.ThemedCheckBoxPreference;
import com.github.dfa.diaspora_android.ui.ThemedIntEditTextPreference;
import com.github.dfa.diaspora_android.ui.ThemedStringEditTextPreference;
import com.github.dfa.diaspora_android.util.ProxyHandler;

import org.json.JSONException;
import org.json.JSONObject;

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

    private String[] getStringArray(SharedPreferences pref, int keyResourceId) {
        String value = pref.getString(context.getString(keyResourceId), "%%%");
        if (value.equals("%%%")) {
            return new String[0];
        }
        return value.split("%%%");
    }

    private String getString(SharedPreferences pref, int resourceId, String defaultValue) {
        return pref.getString(context.getString(resourceId), defaultValue);
    }

    private boolean getBoolean(SharedPreferences pref, int resourceId, boolean defaultValue) {
        return pref.getBoolean(context.getString(resourceId), defaultValue);
    }

    private int getInt(SharedPreferences pref, int resourceId, int defaultValue) {
        return pref.getInt(context.getString(resourceId), defaultValue);
    }


    /*
    //     Setters & Getters
    */
    public String getProfileId() {
        return getString(prefPod, R.string.pref_key__podprofile_id, "");
    }

    void setProfileId(String profileId) {
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

    public String getMinimumFontSizeString() {
        String[] values = context.getResources().getStringArray(R.array.pref_entries_values__font_size);
        String[] titles = context.getResources().getStringArray(R.array.pref_entries__font_size);
        String current = getString(prefApp, R.string.pref_key__font_size, "normal");
        for(int i=0; i<values.length; i++) {
            if(values[i].equals(current)) {
                return titles[i];
            }
        }
        return titles[0];
    }

    public void setMinimumFontSize(int size) {
        switch (size) {
            case 20:
                setString(prefApp, R.string.pref_key__font_size, "huge");
                break;
            case 16:
                setString(prefApp, R.string.pref_key__font_size, "large");
                break;
            default:
                setString(prefApp, R.string.pref_key__font_size, "normal");
        }
    }

    public void setMinimumFontSizeIndex(int index) {
        if(index == 0) setMinimumFontSize(8);
        else if(index == 1) setMinimumFontSize(16);
        else setMinimumFontSize(20);
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


    // TODO: Remove legacy at some time ;)
    public void upgradeLegacyPoddomain() {
        String legacy = getString(prefPod, R.string.pref_key__poddomain_legacy, "");
        if (!legacy.equals("")) {
            DiasporaPod pod = new DiasporaPod();
            pod.setName(legacy);
            pod.getPodUrls().add(new DiasporaPodUrl().setHost(legacy));
            setPod(pod);
        }
    }

    public DiasporaPod getPod() {
        upgradeLegacyPoddomain();
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
        upgradeLegacyPoddomain();
        return !getString(prefPod, R.string.pref_key__current_pod_0, "").equals("");
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
            return getInt(prefApp, R.string.pref_key__http_proxy_port, 0);
        } catch(Exception _anything){
            //TODO: Backward Compatibility for older versions. REMOVE after App v1.7.0
            String str = getString(prefApp, R.string.pref_key__http_proxy_port, "0");
            return Integer.parseInt(str);
        }
    }

    public void setProxyHttpPort(int value) {
        setInt(prefApp, R.string.pref_key__http_proxy_port, value);
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
        return getBoolean(prefApp, R.string.pref_key__visibility_nav__profile, true);
    }

    public void setPrimaryColorPickerSettings(int base, int shade) {
        setInt(prefApp, R.string.pref_key__primary_color_base, base);
        setInt(prefApp, R.string.pref_key__primary_color_shade, shade);
    }

    public int[] getPrimaryColorPickerSettings() {
        return new int[]{
                getInt(prefApp, R.string.pref_key__primary_color_base, getColor(R.color.md_blue_500)),
                getInt(prefApp, R.string.pref_key__primary_color_shade, getColor(R.color.primary))
        };
    }

    public int getPrimaryColor() {
        return getInt(prefApp, R.string.pref_key__primary_color_shade, getColor(R.color.primary));
    }

    private int getColor(int id) {
        if(Build.VERSION.SDK_INT >= 23) {
            return context.getResources().getColor(id, context.getTheme());
        } else {
            return context.getResources().getColor(id);
        }
    }

    public void setAccentColorPickerSettings(int base, int shade) {
        setInt(prefApp, R.string.pref_key__accent_color_base, base);
        setInt(prefApp, R.string.pref_key__accent_color_shade, shade);
    }

    public int[] getAccentColorPickerSettings() {
        return new int[]{
                getInt(prefApp, R.string.pref_key__accent_color_base, getColor(R.color.md_deep_orange_500)),
                getInt(prefApp, R.string.pref_key__accent_color_shade, getColor(R.color.accent))
        };
    }

    public int getAccentColor() {
        return getInt(prefApp, R.string.pref_key__accent_color_shade, getColor(R.color.accent));
    }

    public boolean isExtendedNotifications() {
        return getBoolean(prefApp, R.string.pref_key__extended_notifications, false);
    }

    public boolean getThemedCheckboxPreferenceValue(ThemedCheckBoxPreference t) {
        return prefApp.getBoolean(t.getPrefKey(), t.getDefaultValue());
    }

    public void setThemedCheckboxPreferenceValue(ThemedCheckBoxPreference t, boolean b) {
        prefApp.edit().putBoolean(t.getPrefKey(), b).apply();
    }

    public String getThemedStringEditTextPreferenceValue(ThemedStringEditTextPreference t) {
        return prefApp.getString(t.getPrefKey(), t.getDefaultValue());
    }

    public void setThemedStringEditTextPreferenceValue(ThemedStringEditTextPreference t, String value) {
        prefApp.edit().putString(t.getPrefKey(), value).apply();
    }

    public int getThemedIntEditTextPreferenceValue(ThemedIntEditTextPreference t) {
        return prefApp.getInt(t.getPrefKey(), t.getDefaultValue());
    }

    public void setThemedIntEditTextPreferenceValue(ThemedIntEditTextPreference t, int value) {
        prefApp.edit().putInt(t.getPrefKey(), value).apply();
    }
}
