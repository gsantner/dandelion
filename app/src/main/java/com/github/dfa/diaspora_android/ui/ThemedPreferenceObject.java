package com.github.dfa.diaspora_android.ui;

/**
 * Basic methods a ThemedPreference class should implement
 * Created by vanitas on 23.10.16.
 */

public interface ThemedPreferenceObject<T> {
    String getPrefKey();
    void setTitleText(String titleText);
    void setSummaryText(String summaryText);
    T getDefaultValue();
    T getValue();
    void setValue(T value);
}
