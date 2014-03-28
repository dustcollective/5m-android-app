package com.m5.android.avicola.tracking;

import android.content.Context;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.hanacek.android.utilLib.util.Log;
import com.m5.android.avicola.app.AppContext;
import com.m5.android.avicola.app.modConfig.AbstractModConfig;

public class GoogleAnalytics {

    private Context context;
    private Tracker tracker;

    public enum ScreenName {
        DETAIL, OVERVIEW, SETTINGS, WEB
    }

    public enum Category {
        BUTTON, TAB, LIST_ITEM
    }

    public enum Action {
        OPEN, CLOSE, ADD, REMOVE
    }

    public enum Label {
        FAVORITES_IN_DETAIL, SHARE, HELP, APPS, NAVIGATION_DRAWER, SETTINGS, FAVORITES_IN_DRAWER, SEARCH, //buttons
        NEWS, ALL, EVENTS, //tabs
        SHOW_CONTENT, SHOW_AD //listItems
    }

    public GoogleAnalytics(Context context) {
        this.context = context;
        com.google.android.gms.analytics.GoogleAnalytics.getInstance(context).setDryRun(true);
    }

    synchronized Tracker getTracker() {
        if (tracker == null) {
            tracker = com.google.android.gms.analytics.GoogleAnalytics.getInstance(context)
                    .newTracker(AppContext.modConfig().getString(AbstractModConfig.Strings.GOOGLE_ANALYTICS_PROPERTY_ID));
        }
        return tracker;
    }

    public void sendView(ScreenName screenName) {
        final Tracker t = getTracker();
        t.setScreenName(screenName.toString());
        t.send(new HitBuilders.AppViewBuilder().build());

        Log.debug("GA - sendView() - screenName: " + screenName.toString());
    }

    public void sendHit(Category category, Label label) {
        sendHit(category, null, label);
    }

    public void sendHit(Category category, Action action, Label label) {
        final Tracker t = getTracker();
        final HitBuilders.EventBuilder builder = new HitBuilders.EventBuilder().setCategory(category.toString()).setLabel(label.toString());
        if (action != null) {
            builder.setAction(action.toString());
        }

        t.send(builder.build());

        Log.debug("GA - sendHit() - category: " + category.toString() + ", action: " + ((action == null) ? "null" : action.toString()) + ", label: " + label.toString());
    }

    //TODO opt-out
}
