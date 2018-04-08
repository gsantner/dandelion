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
package com.github.dfa.diaspora_android.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Environment;

import com.github.dfa.diaspora_android.App;
import com.github.dfa.diaspora_android.BuildConfig;
import com.github.dfa.diaspora_android.R;
import com.github.dfa.diaspora_android.data.DiasporaAspect;
import com.github.dfa.diaspora_android.data.DiasporaPodList.DiasporaPod;
import com.github.dfa.diaspora_android.web.ProxyHandler;

import net.gsantner.opoc.preference.SharedPreferencesPropertyBackend;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.List;

/**
 * Settings
 * Created by gsantner (http://gsantner.net/) on 20.03.16. Part of dandelion*.
 */
@SuppressWarnings("ConstantConditions")
public class AppSettings extends SharedPreferencesPropertyBackend {
    private final SharedPreferences _prefPod;
    private DiasporaPod currentPod0Cached;

    public static AppSettings get() {
        return new AppSettings(App.get());
    }

    public AppSettings(Context context) {
        super(context);
        _prefPod = _context.getSharedPreferences("pod0", Context.MODE_PRIVATE);
    }

    /**
     * Clear all settings in _prefPod (Settings related to the configured pod)
     * This uses commit instead of apply, since
     * SettingsActivity.SettingsFragmentDebugging.showWipeSettingsDialog()
     * kills the app after the calling this, so we have to block until we are finished.
     */
    @SuppressLint("CommitPrefEdits")
    public void resetPodSettings() {
        super.resetSettings(_prefPod);
    }

    /**
     * Clear all settings in _prefApp (related to the App itself)
     * This uses commit instead of apply, since
     * SettingsActivity.SettingsFragmentDebugging.showWipeSettingsDialog()
     * kills the app after the calling this, so we have to block until we are finished.
     */
    @SuppressLint("CommitPrefEdits")
    public void resetAppSettings() {
        super.resetSettings(_prefApp);
    }

    //#################################
    //## Getter & Setter for settings
    //#################################
    public String getProfileId() {
        return getString(R.string.pref_key__podprofile_id, "", _prefPod);
    }

    public void setProfileId(String profileId) {
        setString(R.string.pref_key__podprofile_id, profileId, _prefPod);
    }

    public boolean isLoadImages() {
        return getBool(R.string.pref_key__load_images, true);
    }

    public int getMinimumFontSize() {
        switch (getString(R.string.pref_key__font_size, "")) {
            case "huge":
                return 20;
            case "large":
                return 16;
            case "normal":
                return 8;
            default:
                setString(R.string.pref_key__font_size, "normal");
                return 8;
        }
    }

    public String getAvatarUrl() {
        return getString(R.string.pref_key__podprofile_avatar_url, "", _prefPod);
    }

    public void setAvatarUrl(String avatarUrl) {
        setString(R.string.pref_key__podprofile_avatar_url, avatarUrl, _prefPod);
    }

    public String getName() {
        return getString(R.string.pref_key__podprofile_name, "", _prefPod);
    }

    public void setName(String name) {
        setString(R.string.pref_key__podprofile_name, name, _prefPod);
    }

    public DiasporaPod getPod() {
        if (currentPod0Cached == null) {
            String pref = getString(R.string.pref_key__current_pod_0, "", _prefPod);

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
            setString(R.string.pref_key__current_pod_0,
                    pod == null ? null : pod.toJson().toString(), _prefPod);
            currentPod0Cached = pod;
        } catch (JSONException ignored) {
        }
    }

    public boolean hasPod() {
        return !getString(R.string.pref_key__current_pod_0, "", _prefPod).equals("");
    }

    public void setPodAspects(DiasporaAspect[] aspects) {
        String[] strs = new String[aspects.length];
        for (int i = 0; i < strs.length; i++) {
            strs[i] = aspects[i].toShareAbleText();
        }
        setStringArray(R.string.pref_key__podprofile_aspects, strs, _prefPod);
    }

    public DiasporaAspect[] getAspects() {
        String[] s = getStringArray(R.string.pref_key__podprofile_aspects, _prefPod);
        DiasporaAspect[] aspects = new DiasporaAspect[s.length];
        for (int i = 0; i < aspects.length; i++) {
            aspects[i] = new DiasporaAspect(s[i]);
        }
        return aspects;
    }

    public String[] getFollowedTags() {
        return getStringArray(R.string.pref_key__podprofile_followed_tags, _prefPod);
    }

    public void setFollowedTags(String[] values) {
        setStringArray(R.string.pref_key__podprofile_followed_tags, values, _prefPod);
    }

    public String[] getFollowedTagsFavs() {
        return getStringArray(R.string.pref_key__podprofile_followed_tags_favs, _prefPod);
    }

    public void setFollowedTagsFavs(List<String> values) {
        setStringList(R.string.pref_key__podprofile_followed_tags_favs, values, _prefPod);
    }

    public String[] getAspectFavs() {
        return getStringArray(R.string.pref_key__podprofile_aspects_favs, _prefPod);
    }

    public void setAspectFavs(List<String> values) {
        setStringList(R.string.pref_key__podprofile_aspects_favs, values, _prefPod);
    }

    public int getUnreadMessageCount() {
        return getInt(R.string.pref_key__podprofile_unread_message_count, 0, _prefPod);
    }

    public void setUnreadMessageCount(int unreadMessageCount) {
        setInt(R.string.pref_key__podprofile_unread_message_count, unreadMessageCount, _prefPod);
    }

    public int getNotificationCount() {
        return getInt(R.string.pref_key__podprofile_notification_count, 0, _prefPod);
    }

    public void setNotificationCount(int notificationCount) {
        setInt(R.string.pref_key__podprofile_notification_count, notificationCount, _prefPod);
    }

    public boolean isAppendSharedViaApp() {
        return getBool(R.string.pref_key__append_shared_via_app, true);
    }

    @SuppressLint("CommitPrefEdits")
    public void setProxyHttpEnabled(boolean enabled) {
        //commit instead of apply because the app is likely to be killed before apply is called.
        _prefApp.edit().putBoolean(rstr(R.string.pref_key__http_proxy_enabled), enabled).commit();
    }

    /**
     * Default return value: false
     *
     * @return whether proxy is enabled or not
     */
    public boolean isProxyHttpEnabled() {
        try {
            return getBool(R.string.pref_key__http_proxy_enabled, false);
        } catch (ClassCastException e) {
            setProxyHttpEnabled(false);
            return false;
        }
    }

    public boolean wasProxyEnabled() {
        return getBool(R.string.pref_key__proxy_was_enabled, false);
    }

    /**
     * Needed in order to determine, whether the proxy has just been disabled (trigger app restart)
     * or if proxy was disabled before (do not restart app)
     *
     * @param b new value
     */
    @SuppressLint("CommitPrefEdits")
    public void setProxyWasEnabled(boolean b) {
        _prefApp.edit().putBoolean(rstr(R.string.pref_key__proxy_was_enabled), b).commit();
    }

    /**
     * Default value: ""
     *
     * @return proxy host
     */
    public String getProxyHttpHost() {
        return getString(R.string.pref_key__http_proxy_host, "");
    }

    public void setProxyHttpHost(String value) {
        setString(R.string.pref_key__http_proxy_host, value);
    }

    /**
     * Default value: 0
     *
     * @return proxy port
     */
    public int getProxyHttpPort() {
        try {
            String str = getString(R.string.pref_key__http_proxy_port, "0");
            return Integer.parseInt(str);
        } catch (ClassCastException e) {
            int port = getInt(R.string.pref_key__http_proxy_port, 0);
            setProxyHttpPort(port);
            return port;
        }
    }

    public void setProxyHttpPort(int value) {
        setString(R.string.pref_key__http_proxy_port, Integer.toString(value));
    }

    public ProxyHandler.ProxySettings getProxySettings() {
        return new ProxyHandler.ProxySettings(isProxyHttpEnabled(), getProxyHttpHost(), getProxyHttpPort());
    }

    public boolean isIntellihideToolbars() {
        return getBool(R.string.pref_key__intellihide_toolbars, false);
    }

    public boolean isChromeCustomTabsEnabled() {
        return getBool(R.string.pref_key__chrome_custom_tabs_enabled, true);
    }

    public boolean isLoggingEnabled() {
        return getBool(R.string.pref_key__logging_enabled, false);
    }

    public boolean isLoggingSpamEnabled() {
        return getBool(R.string.pref_key__logging_spam_enabled, false);
    }

    public boolean isVisibleInNavExit() {
        return getBool(R.string.pref_key__visibility_nav__exit, true);
    }

    public boolean isVisibleInNavHelp_license() {
        return getBool(R.string.pref_key__visibility_nav__help_license, true);
    }

    public boolean isVisibleInNavPublic_activities() {
        return getBool(R.string.pref_key__visibility_nav__public_activities, false);
    }

    public boolean isVisibleInNavMentions() {
        return getBool(R.string.pref_key__visibility_nav__mentions, false);
    }

    public boolean isVisibleInNavCommented() {
        return getBool(R.string.pref_key__visibility_nav__commented, true);
    }

    public boolean isVisibleInNavLiked() {
        return getBool(R.string.pref_key__visibility_nav__liked, true);
    }

    public boolean isVisibleInNavActivities() {
        return getBool(R.string.pref_key__visibility_nav__activities, true);
    }

    public boolean isVisibleInNavAspects() {
        return getBool(R.string.pref_key__visibility_nav__aspects, true);
    }

    public boolean isVisibleInNavFollowed_tags() {
        return getBool(R.string.pref_key__visibility_nav__followed_tags, true);
    }

    public boolean isVisibleInNavProfile() {
        return getBool(R.string.pref_key__visibility_nav__profile, true);
    }

    public boolean isVisibleInNavContacts() {
        return getBool(R.string.pref_key__visibility_nav__contacts, false);
    }

    public boolean isVisibleInNavStatistics() {
        return getBool(R.string.pref_key__visibility_nav__statistics, false);
    }

    public boolean isVisibleInNavReports() {
        return getBool(R.string.pref_key__visibility_nav__reports, false);
    }

    public boolean isVisibleInNavDandelionAccount() {
        return getBool(R.string.pref_key__visibility_nav__dandelion_account, false);
    }

    public boolean isVisibleInNavToggleMobileDesktop() {
        return getBool(R.string.pref_key__visibility_nav__toggle_mobile_desktop, false);
    }

    public boolean isTopbarStreamShortcutEnabled() {
        return getBool(R.string.pref_key__topbar_stream_shortcut, false);
    }

    public String getScreenRotation() {
        return getString(R.string.pref_key__screen_rotation, R.string.rotation_val_system);
    }

    public boolean isAppFirstStart() {
        boolean value = getBool(R.string.pref_key__app_first_start, true);
        setBool(R.string.pref_key__app_first_start, false);
        return value;
    }

    public boolean isAppCurrentVersionFirstStart(boolean doSet) {
        int value = getInt(R.string.pref_key__app_first_start_current_version, -1);
        if (doSet) {
            setInt(R.string.pref_key__app_first_start_current_version, BuildConfig.VERSION_CODE);
        }
        return value != BuildConfig.VERSION_CODE && !BuildConfig.IS_TEST_BUILD;
    }

    public File getAppSaveDirectory() {
        return new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/dandelion");

    }

    public long getLastVisitedPositionInStream() {
        return getLong(R.string.pref_key__podprofile_last_stream_position, -1, _prefPod);
    }

    public void setLastVisitedPositionInStream(long timestamp) {
        setLong(R.string.pref_key__podprofile_last_stream_position, timestamp, _prefPod);
    }

    public void setLanguage(String value) {
        setString(R.string.pref_key__language, value);
    }

    public String getLanguage() {
        return getString(R.string.pref_key__language, "");
    }

    public void setPrimaryColorSettings(int base, int shade) {
        setInt(R.string.pref_key__primary_color_base, base);
        setInt(R.string.pref_key__primary_color_shade, shade);
    }

    public int[] getPrimaryColorSettings() {
        return new int[]{
                getInt(R.string.pref_key__primary_color_base, rcolor(R.color.md_blue_650)),
                getInt(R.string.pref_key__primary_color_shade, rcolor(R.color.primary))
        };
    }

    @SuppressWarnings("ConstantConditions")
    public int getPrimaryColor() {
        if (isAmoledColorMode()) {
            return Color.BLACK;
        } else {
            return getInt(R.string.pref_key__primary_color_shade, rcolor(
                    BuildConfig.IS_TEST_BUILD ? R.color.md_brown_800 : R.color.primary));
        }
    }

    public void setAccentColorSettings(int base, int shade) {
        setInt(R.string.pref_key__accent_color_base, base);
        setInt(R.string.pref_key__accent_color_shade, shade);
    }

    public int[] getAccentColorSettings() {
        return new int[]{
                getInt(R.string.pref_key__accent_color_base, rcolor(R.color.md_green_400)),
                getInt(R.string.pref_key__accent_color_shade, rcolor(R.color.accent))
        };
    }

    public int getAccentColor() {
        return getInt(R.string.pref_key__accent_color_shade, rcolor(R.color.accent));
    }

    public boolean isExtendedNotificationsActivated() {
        return getBool(R.string.pref_key__extended_notifications, false);
    }

    public boolean isAmoledColorMode() {
        return getBool(R.string.pref_key__primary_color__amoled_mode, false);
    }

    public boolean isAdBlockEnabled() {
        return getBool(R.string.pref_key__adblock_enable, true);
    }

    public boolean isEditorStatusBarHidden() {
        return getBool(R.string.pref_key__is_overview_statusbar_hidden, false);
    }

    public void setRecreateMainActivity(boolean value) {
        setBool(R.string.pref_key__recreate_main_activity, value);
    }

    public boolean isRecreateMainActivity() {
        boolean value = getBool(R.string.pref_key__recreate_main_activity, false);
        setRecreateMainActivity(false);
        return value;
    }

    public boolean isShowTitleInMainView() {
        return getBool(R.string.pref_key__show_title, false);
    }
}
