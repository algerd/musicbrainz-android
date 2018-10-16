package org.musicbrainz.android.activity;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import org.musicbrainz.android.MusicBrainzApp;
import org.musicbrainz.android.R;
import org.musicbrainz.android.intent.ActivityFactory;
import org.musicbrainz.android.intent.zxing.IntentIntegrator;
import org.musicbrainz.android.suggestion.SuggestionHelper;

import static org.musicbrainz.android.MusicBrainzApp.oauth;

public abstract class BaseActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        SearchView.OnQueryTextListener {

    protected DrawerLayout drawer;
    protected NavigationView navigationView;
    protected Toolbar toolbar;

    abstract protected int getContentLayout();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentLayout());

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                drawer,
                toolbar,
                R.string.nav_open_drawer,
                R.string.nav_close_drawer);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_home:
                ActivityFactory.startMainActivity(this);
                break;
            case R.id.nav_about:
                ActivityFactory.startAboutActivity(this);
                break;
            case R.id.nav_scan_barcode:
                IntentIntegrator.initiateScan(this, getString(R.string.zx_title), getString(R.string.zx_message),
                        getString(R.string.zx_pos), getString(R.string.zx_neg), IntentIntegrator.PRODUCT_CODE_TYPES);
                break;
            case R.id.nav_settings:
                ActivityFactory.startSettingsActivity(this);
                break;
            case R.id.nav_feedback:
                ActivityFactory.startFeedbackActivity(this);
                break;

            // User nav:
            case R.id.nav_user_profile:
                startUserActivity(R.id.user_navigation_profile);
                break;
            case R.id.nav_user_collections:
                startUserActivity(R.id.user_navigation_collections);
                break;
            case R.id.nav_user_ratings:
                startUserActivity(R.id.user_navigation_ratings);
                break;
            case R.id.nav_user_tags:
                startUserActivity(R.id.user_navigation_tags);
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void startUserActivity(int userNavigationView) {
        if (oauth.hasAccount()) {
            ActivityFactory.startUserActivity(this, oauth.getName(), userNavigationView);
        } else {
            ActivityFactory.startLoginActivity(this);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == IntentIntegrator.BARCODE_REQUEST) {
            String barcode = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent).getContents();
            if (barcode != null) {
                ActivityFactory.startBarcodeSearchActivity(this, barcode);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setOnQueryTextListener(this);

        searchView.setSearchableInfo(searchManager.getSearchableInfo(new ComponentName(this, ArtistSearchActivity.class)));
        searchView.setIconifiedByDefault(false);

        if (MusicBrainzApp.getPreferences().isSearchSuggestionsEnabled()) {
            searchView.setSuggestionsAdapter(new SuggestionHelper(this).getAdapter());
            searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
                @Override
                public boolean onSuggestionClick(int position) {
                    Cursor cursor = (Cursor) searchView.getSuggestionsAdapter().getItem(position);
                    searchView.setQuery(cursor.getString(cursor.getColumnIndex(SuggestionHelper.COLUMN)), true);
                    searchView.clearFocus();
                    return true;
                }

                @Override
                public boolean onSuggestionSelect(int position) {
                    return true;
                }
            });

        }
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        ActivityFactory.startArtistSearchActivity(this, query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

}
