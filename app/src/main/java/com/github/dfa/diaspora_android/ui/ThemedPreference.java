package com.github.dfa.diaspora_android.ui;

/**
 * Created by vanitas on 23.10.16.
 */

public interface ThemedPreference<T> {
    String getPrefKey();
    void setTitleText(String titleText);
    void setSummaryText(String summaryText);
    T getDefaultValue();
    T getValue();
    void setValue(T value);
}
