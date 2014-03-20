package com.m5.android.avicola;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.hanacek.android.utilLib.tasks.AbstractAsyncTask;
import com.hanacek.android.utilLib.tasks.AbstractHttpAsyncTask;
import com.hanacek.android.utilLib.util.Log;
import com.m5.android.avicola.app.AppContext;
import com.m5.android.avicola.app.Constants;
import com.m5.android.avicola.app.UiComponentContext;
import com.m5.android.avicola.app.modConfig.AbstractModConfig;
import com.m5.android.avicola.model.Advert;
import com.m5.android.avicola.model.Content;
import com.m5.android.avicola.model.Feed;
import com.m5.android.avicola.ui.view.InterstitialView;
import com.m5.android.avicola.util.Cfg;
import com.m5.android.avicola.util.IntentUtil;

import java.util.List;

public class MainActivity extends ActionBarActivity implements ListFragment.ListFragmentInterface, NavigationDrawerFragment.NavigationDrawerCallbacks {

    private static final int REQUEST_CODE_SETTINGS = 1;

    /**
     * Keep complete data exactly as they came from the feed locally cached
     */
    private Feed feed;

    /**
     * What tab is currently selected
     */
    private Content.Type currentType = Content.Type.ALL;

    /**
     * In case user is searching, keep the searched expression
     */
    private String currentSearchExpression;

    /**
     * Filter by territory
     */
    private Content.Territory currentTerritory = Content.Territory.ALL;

    private UiComponentContext uiComponentContext;

    private InterstitialView interstitialView;

    private int detailInterstitialsShown;

    private boolean isFavoritesShown;
    private AlertDialog helpDialog;

    private NavigationDrawerFragment mNavigationDrawerFragment;
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.container, new ListFragment(), ListFragment.TAG).commit();
        }

        final ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        // Set up the drawer.
        mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavigationDrawerFragment.setUp(R.id.navigation_drawer, drawerLayout);

        //add tabs
        final TabListener tabListener = new TabListener();
        ActionBar.Tab tab = actionBar.newTab().setText(R.string.tab_all).setTag(Content.Type.ALL).setTabListener(tabListener);
        actionBar.addTab(tab);
        tab = actionBar.newTab().setText(R.string.tab_news).setTag(Content.Type.NEWS).setTabListener(tabListener);
        actionBar.addTab(tab);
        tab = actionBar.newTab().setText(R.string.tab_events).setTag(Content.Type.EVENT).setTabListener(tabListener);
        actionBar.addTab(tab);

        uiComponentContext = new UiComponentContext(this);

        new AbstractHttpAsyncTask<Feed>(AppContext.modConfig().getString(AbstractModConfig.Strings.FEED_URL), AppContext.httpAsyncTaskConfiguration()){
            @Override
            protected Feed extendedPostDoInBackground(byte[] result) {
                try {
                    ObjectMapper mapper = new ObjectMapper();
                    mapper.setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
                    return mapper.readValue(result, Feed.class);
                }
                catch (Exception e) {
                    Log.error("Exception while parsing json", e);
                    return null;
                }
            }

            @Override
            public void onSuccess(Feed feed) {
                MainActivity.this.feed = feed;
                showData();

                if (feed.adverts.launch != null && feed.adverts.launch.length > 0) {
                    interstitialView.displayImage(feed.adverts.interDisplay*1000, feed.adverts.launch[0].fullscreen, new InterstitialListener(),
                            feed.adverts.launch[0].link);
                }
            }

            @Override
            protected void extendedOnFailed(FailHolder failHolder) {
                Toast.makeText(MainActivity.this, R.string.err_fetch_data, Toast.LENGTH_LONG).show();

            }
        }.setShowProgressBar(uiComponentContext).execute();

        interstitialView = (InterstitialView) findViewById(R.id.interstitial);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                doSearch(s);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (s.length() == 0 && !TextUtils.isEmpty(currentSearchExpression)) {
                    doSearch(s);
                }
                else if (s.length() >= 3) {
                    doSearch(s);
                }

                return true;
            }

            private void doSearch(String s) {
                currentSearchExpression = s;
                showData();
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                currentSearchExpression = null;
                showData();
                return true;
            }
        });

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case REQUEST_CODE_SETTINGS:
                currentTerritory = (Content.Territory) data.getSerializableExtra(Constants.EXTRAS_TERRITORY);
                if (currentTerritory == null) {
                    currentTerritory = Content.Territory.ALL;
                }
                showData();
                return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    protected void showData() {
        final ListFragment listFragment = (ListFragment) getSupportFragmentManager().findFragmentByTag(ListFragment.TAG);
        if (listFragment != null && feed != null) {
            listFragment.showData(currentType, currentTerritory, currentSearchExpression, feed.contents, feed.adverts);
        }
    }

    @Override
    public void onContentItemSelected(final Content item) {
        final Advert[] ads = feed.adverts.inline;
        int clickFrequency = feed.adverts.clickFrequency;
        if (ads != null && ++detailInterstitialsShown%clickFrequency == 0 && detailInterstitialsShown/clickFrequency < ads.length) {
            interstitialView.displayImage(feed.adverts.interDisplay*1000, feed.adverts.inter[detailInterstitialsShown/clickFrequency].fullscreen,
                    new InterstitialListener(){
                        @Override
                        public void additionalOnHide() {
                            IntentUtil.startDetailActivity(MainActivity.this, item);
                        }
                    });
        }
        else {
            IntentUtil.startDetailActivity(this, item);
        }
    }

    @Override
    public void onNavigationDrawerItemSelected(NavigationDrawerFragment.DrawerItem drawerItem) {
        switch (drawerItem) {
            case FAVORITES:
                new AbstractAsyncTask<List<Content>>() {
                    @Override
                    protected List<Content> extendedDoInBackground() {
                        return AppContext.favoriteDao().getAll();
                    }

                    @Override
                    public void onSuccess(List<Content> contents) {
                        isFavoritesShown = true;
                        final ListFragment listFragment = (ListFragment) getSupportFragmentManager().findFragmentByTag(ListFragment.TAG);
                        listFragment.showData(contents);
                    }
                }.setShowProgressBar(uiComponentContext).execute();
                break;
            case SETTINGS:
                IntentUtil.startSettingsForResult(this, REQUEST_CODE_SETTINGS);
                break;
            case HOME:

                break;
            case APPS:
                IntentUtil.startWebActivity(this, Cfg.URL_APPS);
                break;
            case HELP:
                if (helpDialog == null) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setPositiveButton(R.string.help_text_dismiss_button, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.setMessage(R.string.help_text);
                    helpDialog = builder.create();
                }
                helpDialog.show();
                break;
        }
    }

    @Override
    public void onAdvertItemSelected(Advert item) {

    }

    @Override
    public void onBackPressed() {
        if (isFavoritesShown) {
            showData();
            isFavoritesShown = false;
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (helpDialog != null) {
            helpDialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        uiComponentContext.unbind();
        super.onDestroy();
    }

    private class InterstitialListener implements InterstitialView.InterstitialStateListener {
        @Override
        final public void onShow() {
            interstitialView.setVisibility(View.VISIBLE);
            mNavigationDrawerFragment.setCanOpen(false);
            getSupportActionBar().hide();
        }

        @Override
        final public void onHide() {
            getSupportActionBar().show();
            mNavigationDrawerFragment.setCanOpen(true);
            additionalOnHide();
            interstitialView.setVisibility(View.GONE);
        }

        public void additionalOnHide() {}
    }

    public class TabListener implements ActionBar.TabListener {
        @Override
        public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
            currentType = (Content.Type) tab.getTag();
            showData();
        }

        public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

        }

        public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
            // User selected the already selected tab. Usually do nothing.
        }
    }
}
