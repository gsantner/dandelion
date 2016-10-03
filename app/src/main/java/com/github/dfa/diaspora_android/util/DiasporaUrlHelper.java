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

import com.github.dfa.diaspora_android.App;
import com.github.dfa.diaspora_android.R;
import com.github.dfa.diaspora_android.data.AppSettings;
import com.github.dfa.diaspora_android.data.PodAspect;

import java.util.regex.Pattern;

/**
 * Helper class that provides easy access to specific urls related to diaspora
 * Created by vanitasvitae on 10.08.16.
 */
@SuppressWarnings("unused")
public class DiasporaUrlHelper {
    private final AppSettings settings;

    public static final String HTTPS = "https://";
    public static final String SUBURL_NOTIFICATIONS = "/notifications";
    public static final String SUBURL_POSTS = "/posts/";
    public static final String SUBURL_STREAM = "/stream";
    public static final String SUBURL_CONVERSATIONS = "/conversations";
    public static final String SUBURL_NEW_POST = "/status_messages/new";
    public static final String SUBURL_PEOPLE = "/people/";
    public static final String SUBURL_ACTIVITY = "/activity";
    public static final String SUBURL_LIKED = "/liked";
    public static final String SUBURL_COMMENTED = "/commented";
    public static final String SUBURL_MENTIONS = "/mentions";
    public static final String SUBURL_PUBLIC = "/public";
    public static final String SUBURL_TOGGLE_MOBILE = "/mobile/toggle";
    public static final String SUBURL_SEARCH_TAGS = "/tags/";
    public static final String SUBURL_SEARCH_PEOPLE = "/people.mobile?q=";
    public static final String SUBURL_FOLOWED_TAGS = "/followed_tags";
    public static final String SUBURL_ASPECTS = "/aspects";
    public static final String SUBURL_STATISTICS = "/statistics";
    public static final String URL_BLANK = "about:blank";

    public static final Pattern NUMBERS_ONLY_PATTERN = Pattern.compile("^\\d+$");

    public DiasporaUrlHelper(AppSettings settings) {
        this.settings = settings;
    }

    /**
     * Return a https url of the pod set in AppSettings.
     * Eg. https://pod.geraspora.de
     *
     * @return https://(pod-domain.tld)
     */
    public String getPodUrl() {
        return HTTPS + settings.getPodDomain();
    }

    /**
     * Return a https url that points to the stream of the configured diaspora account
     *
     * @return https://(pod-domain.tld)/stream
     */
    public String getStreamUrl() {
        return getPodUrl() + SUBURL_STREAM;
    }

    /**
     * Return a https url that points to the notifications feed of the configured diaspora account
     *
     * @return https://(pod-domain.tld)/notifications
     */
    public String getNotificationsUrl() {
        return getPodUrl() + SUBURL_NOTIFICATIONS;
    }

    /**
     * Returns a https url that points to the post with the id postId
     *
     * @return https://(pod-domain.tld)/posts/(postId)
     */
    public String getPostsUrl(long postId) {
        return getPodUrl() + SUBURL_POSTS + postId;
    }

    /**
     * Return a https url that points to the conversations overview of the registered diaspora account
     *
     * @return https://(pod-domain.tld)/conversations
     */
    public String getConversationsUrl() {
        return getPodUrl() + SUBURL_CONVERSATIONS;
    }

    /**
     * Return a https url that points to the new-post form that lets the user create a new post
     *
     * @return https://(pod-domain.tld)/status_messages/new
     */
    public String getNewPostUrl() {
        return getPodUrl() + SUBURL_NEW_POST;
    }

    /**
     * Return a https url that shows the profile of the currently registered diaspora account
     *
     * @return https://(pod-domain.tld)/people/(profileId)
     */
    public String getProfileUrl() {
        return getPodUrl() + SUBURL_PEOPLE + settings.getProfileId();
    }

    /**
     * Return a https url that shows the profile of the user with user id profileId
     *
     * @param profileId Id of the profile to be shown
     * @return https://(pod-domain.tld)/people/(profileId)
     */
    public String getProfileUrl(long profileId) {
        return getPodUrl() + SUBURL_PEOPLE + profileId;
    }

    /**
     * Return a https url that points to the activities feed of the currently registered diaspora account
     *
     * @return https://(pod-domain.tld)/activity
     */
    public String getActivityUrl() {
        return getPodUrl() + SUBURL_ACTIVITY;
    }

    /**
     * Return a https url that points to the feed of posts that were liked by the currently registered diaspora account
     *
     * @return https://(pod-domain.tld)/liked
     */
    public String getLikedPostsUrl() {
        return getPodUrl() + SUBURL_LIKED;
    }

    /**
     * Return a https url that points to the stream of posts that were commented by the currently registered diaspora account
     *
     * @return https://(pod-domain.tld)/commented
     */
    public String getCommentedUrl() {
        return getPodUrl() + SUBURL_COMMENTED;
    }

    /**
     * Return a https url that points to the stream of posts in which the currently registered diaspora account has been mentioned in
     *
     * @return https://(pod-domain.tld)/mentions
     */
    public String getMentionsUrl() {
        return getPodUrl() + SUBURL_MENTIONS;
    }

    /**
     * Return a https url that points to the stream of public posts
     *
     * @return https://(pod-domain.tld)/public
     */
    public String getPublicUrl() {
        return getPodUrl() + SUBURL_PUBLIC;
    }

    /**
     * Return a https url that toggles between mobile and desktop view when opened
     *
     * @return https://(pod-domain.tld)/mobile/toggle
     */
    public String getToggleMobileUrl() {
        return getPodUrl() + SUBURL_TOGGLE_MOBILE;
    }

    /**
     * Return a https url that queries posts for the given hashtag query
     *
     * @param query hashtag to be searched
     * @return https://(pod-domain.tld)/tags/query
     */
    public String getSearchTagsUrl(String query) {
        return getPodUrl() + SUBURL_SEARCH_TAGS + query;
    }

    /**
     * Return a https url that queries user accounts for query
     *
     * @param query search term
     * @return https://(pod-domain.tld)/people.mobile?q=(query)
     */
    public String getSearchPeopleUrl(String query) {
        return getPodUrl() + SUBURL_SEARCH_PEOPLE + query;
    }

    /**
     * Return a https url that points to the statistics page of the pod.
     * @return https://(pod-domain.tld)/statistics
     */
    public String getStatisticsUrl() {
        return getPodUrl() + SUBURL_STATISTICS;
    }

    /**
     * Returns the url of the blank WebView
     *
     * @return about:blank
     */
    public String getBlankUrl() {
        return URL_BLANK;
    }

    public boolean isAspectUrl(String url) {
        return url.startsWith(getPodUrl() + "/aspects?a_ids[]=");
    }

    public String getAspectNameFromUrl(String url, App app) {
        url = url.replace(getPodUrl() + "/aspects?a_ids[]=", "").split(",")[0];
        try {
            int id = Integer.parseInt(url);
            for (PodAspect aspect : app.getPodUserProfile().getAspects()) {
                if (aspect.id == id) {
                    return aspect.name;
                }
            }
        } catch (Exception ignored) {
        }
        return app.getString(R.string.aspects);
    }

    public boolean isPostUrl(String url) {
        if (url.startsWith(getPodUrl() + SUBURL_POSTS)) {
            String path = url.substring((getPodUrl() + SUBURL_POSTS).length());
            if (NUMBERS_ONLY_PATTERN.matcher(path).matches()) {
                return true;
            }
        }

        return false;
    }
}
