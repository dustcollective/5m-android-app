package com.m5.android.avicola;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.ShareActionProvider;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.hanacek.android.utilLib.calendar.CalendarUtils;
import com.hanacek.android.utilLib.tasks.AbstractAsyncTask;
import com.hanacek.android.utilLib.ui.view.PresetSizeImageView;
import com.hanacek.android.utilLib.util.Log;
import com.m5.android.avicola.app.AppContext;
import com.m5.android.avicola.app.Constants;
import com.m5.android.avicola.app.UiComponentContext;
import com.m5.android.avicola.model.Content;
import com.m5.android.avicola.tracking.GoogleAnalytics;
import com.m5.android.avicola.util.Cfg;
import com.m5.android.avicola.util.IntentUtil;

import java.util.Calendar;
import java.util.TimeZone;

public class DetailActivity extends ActionBarActivity {

    private Content item;
    private ShareActionProvider shareActionProvider;
    private UiComponentContext uiComponentContext;
    private PresetSizeImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_detail);
        item = Content.fromBundle(getIntent().getBundleExtra(Constants.EXTRAS_CONTENT));

        ((TextView)findViewById(R.id.headline)).setText(Html.fromHtml(item.headline));

        final TextView body = (TextView) findViewById(R.id.body);
        body.setText(Html.fromHtml(item.body));
        body.setMovementMethod(LinkMovementMethod.getInstance());

        imageView = ((PresetSizeImageView)findViewById(R.id.image));
        imageView.presetDimensions(AppContext.getDisplayWidth(), (int)(Cfg.IMAGE_HEIGHT_RATIO*AppContext.getDisplayWidth()));
        imageView.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        showData();

        uiComponentContext = new UiComponentContext(this);
        uiComponentContext.setProgressBar(findViewById(R.id.progress));

        getSupportActionBar().setDisplayShowTitleEnabled(true);
        doShare();
    }

    @Override
    protected void onStart() {
        super.onStart();
        AppContext.ga().sendView(GoogleAnalytics.ScreenName.DETAIL);
    }

    private void showData() {
        switch (item.type) {
            case EVENT:
                showEvent();
                break;
            default:
                showStandard();
        }
    }

    private void showEvent() {
        imageView.setImageResource(R.drawable.event_calendar);
        ((TextView)findViewById(R.id.date)).setText(item.getFormattedStart() + " - " + item.getFormattedEnd());
    }

    private void showStandard() {
        if (item.getImageUrl() == null) {
            imageView.setImageResource(R.drawable.news_calendar);
        }
        else {
            AppContext.imageCache().displayImage(item.getImageUrl(), imageView);
        }
        ((TextView)findViewById(R.id.date)).setText(item.getFormattedDate());
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.detail, menu);
        MenuItem menuItem = menu.findItem(R.id.action_share);
        shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);
        doShare();

        new AbstractAsyncTask<Content>() {
            @Override
            protected Content extendedDoInBackground() {
                return AppContext.favoriteDao().find(DetailActivity.this.item.id);
            }

            @Override
            public void onSuccess(Content content) {
                final MenuItem menuItem = menu.findItem(R.id.action_favorites);
                if (menuItem != null) {
                    if (content == null) {
                        menuItem.setIcon(R.drawable.ic_action_favorite);
                    }
                    else {
                        menuItem.setIcon(R.drawable.ic_action_favorite_selected);
                    }
                }
            }
        }.setIsNullResponseSuccess().execute();

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_favorites) {
            new AbstractAsyncTask<Content>() {
                @Override
                protected Content extendedDoInBackground() {
                    return AppContext.favoriteDao().find(DetailActivity.this.item.id);
                }

                @Override
                public void onSuccess(Content content) {
                    final Content c = DetailActivity.this.item;
                    int messageResId;
                    if (content == null) {
                        AppContext.ga().sendHit(GoogleAnalytics.Category.BUTTON, GoogleAnalytics.Action.ADD, GoogleAnalytics.Label.FAVORITES_IN_DETAIL);
                        if (c.getType() == Content.Type.EVENT) {
                            final Calendar cb = Calendar.getInstance();
                            cb.setTimeInMillis(c.start*1000);
                            final Calendar ce = Calendar.getInstance();
                            ce.setTimeInMillis(c.end*1000);
                            c.calendarId = CalendarUtils.addEvent(DetailActivity.this, cb, ce, c.getHeadline(), c.getTeaser(),
                                    TimeZone.getDefault().getDisplayName(), c.location);
                        }
                        AppContext.favoriteDao().insert(c);
                        messageResId = R.string.fav_added;
                        if (item != null) {
                            item.setIcon(R.drawable.ic_action_favorite_selected);
                        }
                    }
                    else {
                        AppContext.ga().sendHit(GoogleAnalytics.Category.BUTTON, GoogleAnalytics.Action.REMOVE, GoogleAnalytics.Label.FAVORITES_IN_DETAIL);
                        if (c.calendarId != -1) {
                            CalendarUtils.removeEvent(DetailActivity.this, c.calendarId);
                        }

                        AppContext.favoriteDao().delete(c.id);
                        messageResId = R.string.fav_removed;
                        if (item != null) {
                            item.setIcon(R.drawable.ic_action_favorite);
                        }
                    }
                    Toast.makeText(DetailActivity.this, messageResId, Toast.LENGTH_LONG).show();
                }

                @Override
                public void onFailed(FailHolder failHolder, Content content) {
                    Log.error("Could not add/remove from/to favorites.");
                }
            }.setShowProgressBar(uiComponentContext).setIsNullResponseSuccess().execute();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void doShare() {
        if (shareActionProvider != null && item != null) {
            shareActionProvider.setShareIntent(IntentUtil.getShareIntent(item.link));
            shareActionProvider.setOnShareTargetSelectedListener(new ShareActionProvider.OnShareTargetSelectedListener() {
                @Override
                public boolean onShareTargetSelected(ShareActionProvider shareActionProvider, Intent intent) {
                    AppContext.ga().sendHit(GoogleAnalytics.Category.BUTTON, GoogleAnalytics.Label.SHARE);
                    return false;
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        uiComponentContext.unbind();
        super.onDestroy();
    }
}
