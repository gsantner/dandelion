package com.github.dfa.diaspora_android.listener;

/**
 * Listener for different types of click events
 */
public interface OnSomethingClickListener<T> {
    /**
     * Triggered when something was clicked
     *
     * @param o Some object, or null
     * @param i Some index, int value or null
     * @param s Some String, or null
     */
    void onSomethingClicked(T o, Integer i, String s);
}
