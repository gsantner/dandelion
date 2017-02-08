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
package com.github.dfa.diaspora_android.listener;

import com.github.dfa.diaspora_android.data.DiasporaUserProfile;

/**
 * Created by gsantner (https://gsantner.github.io/) on 26.03.16.
 * Interface that needs to be implemented by classes that listen for Profile related changes
 */
public interface DiasporaUserProfileChangedListener {
    /**
     * Called when the DiasporaUserProfile name changed
     *
     * @param diasporaUserProfile The profile
     * @param name                The new name
     */
    void onUserProfileNameChanged(DiasporaUserProfile diasporaUserProfile, String name);

    /**
     * Called when the DiasporaUserProfile avatarUrl changed
     *
     * @param diasporaUserProfile The profile
     * @param avatarUrl           The new name
     */
    void onUserProfileAvatarChanged(DiasporaUserProfile diasporaUserProfile, String avatarUrl);

    /**
     * Called when the DiasporaUserProfile notificationCount changed
     *
     * @param diasporaUserProfile The profile
     * @param notificationCount   The new notificationCount
     */
    void onNotificationCountChanged(DiasporaUserProfile diasporaUserProfile, int notificationCount);

    /**
     * Called when the DiasporaUserProfile unreadMessageCount changed
     *
     * @param diasporaUserProfile The profile
     * @param unreadMessageCount  The new unreadMessageCount
     */
    void onUnreadMessageCountChanged(DiasporaUserProfile diasporaUserProfile, int unreadMessageCount);
}
