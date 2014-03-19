package com.m5.android.avicola;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;

abstract public class TabListener implements ActionBar.TabListener {
    private final String mTag;

    /** Constructor used each time a new tab is created.
     * @param tag  The identifier tag for the fragment
     */
    public TabListener(String tag) {
        mTag = tag;
    }

    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
        // User selected the already selected tab. Usually do nothing.
    }
}
