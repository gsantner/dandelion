package com.github.dfa.diaspora_android.fragment;

import android.os.Bundle;
import android.view.View;

/**
 * Created by vanitas on 16.10.16.
 */

public abstract class ThemedSettingsFragment extends ThemedFragment implements View.OnClickListener {

    @Override
    public void onViewCreated(View layout, Bundle savedInstanceState) {
        super.onViewCreated(layout, savedInstanceState);
        applyColorToViews();
        applySettingsToViews();
        setOnClickListenersOnViews();
    }

    protected abstract void applyColorToViews();
    protected abstract void applySettingsToViews();
    protected abstract void setOnClickListenersOnViews();
}
