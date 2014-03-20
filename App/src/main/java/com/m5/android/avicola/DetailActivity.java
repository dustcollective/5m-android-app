package com.m5.android.avicola;

import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.ShareActionProvider;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.hanacek.android.utilLib.tasks.AbstractAsyncTask;
import com.hanacek.android.utilLib.ui.view.PresetSizeImageView;
import com.hanacek.android.utilLib.util.Log;
import com.m5.android.avicola.app.AppContext;
import com.m5.android.avicola.app.Constants;
import com.m5.android.avicola.app.UiComponentContext;
import com.m5.android.avicola.model.Content;
import com.m5.android.avicola.util.Cfg;
import com.m5.android.avicola.util.IntentUtil;

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
        ((TextView)findViewById(R.id.body)).setText(Html.fromHtml(item.body));
        imageView = ((PresetSizeImageView)findViewById(R.id.image));
        imageView.presetDimensions(AppContext.getDisplayWidth(), (int)(Cfg.IMAGE_HEIGHT_RATIO*AppContext.getDisplayWidth()));
        imageView.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        showData();

        uiComponentContext = new UiComponentContext(this);
        uiComponentContext.setProgressBar(findViewById(R.id.progress));

        getSupportActionBar().setDisplayShowTitleEnabled(true);
        doShare();
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
            //TODO whatever is needed.
        }
        else {
            AppContext.imageCache().displayImage(item.getImageUrl(), imageView);
        }
        ((TextView)findViewById(R.id.date)).setText(item.getFormattedDate());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail, menu);
        MenuItem menuItem = menu.findItem(R.id.action_share);
        shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);
        doShare();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_favorites) {
            new AbstractAsyncTask<Content>() {
                @Override
                protected Content extendedDoInBackground() {
                    return AppContext.favoriteDao().find(DetailActivity.this.item.id);
                }

                @Override
                public void onSuccess(Content content) {
                    int messageResId;
                    if (content == null) {
                        AppContext.favoriteDao().insert(DetailActivity.this.item);
                        messageResId = R.string.fav_added;
                    }
                    else {
                        AppContext.favoriteDao().delete(DetailActivity.this.item.id);
                        messageResId = R.string.fav_removed;
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
        }
    }

    @Override
    protected void onDestroy() {
        uiComponentContext.unbind();
        super.onDestroy();
    }
}
