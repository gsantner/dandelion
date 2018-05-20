package com.github.dfa.diaspora_android.listener;

import android.support.design.widget.AppBarLayout;

/**
 * interface that adds options to control intellihide of toolbars to the Activity
 * Created by vanitas on 08.10.16.
 */

public interface IntellihideToolbarActivityListener {
    int toolbarDefaultScrollFlags = AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL | AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS | AppBarLayout.LayoutParams.SCROLL_FLAG_SNAP;

    void setToolbarIntellihide(boolean enable);
}
