package org.musicbrainz.android.activity;

import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import org.musicbrainz.android.MusicBrainzApp;
import org.musicbrainz.android.R;
import org.musicbrainz.android.adapter.recycler.ArtistSearchAdapter;
import org.musicbrainz.android.api.model.Artist;
import org.musicbrainz.android.intent.ActivityFactory;
import org.musicbrainz.android.suggestion.SuggestionProvider;
import org.musicbrainz.android.util.ShowUtil;

import static org.musicbrainz.android.MusicBrainzApp.api;


public class ArtistSearchActivity extends BaseOptionsMenuActivity {

    public static final String QUERY = "query"; // !!! for SearchView.OnQueryTextListener (BaseActivity)

    private String artistSearch;

    private boolean isLoading;
    private boolean isError;

    private RecyclerView searchRecycler;
    private View error;
    private View loading;
    private View noresults;

    @Override
    protected int getContentLayout() {
        return R.layout.activity_search;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        error = findViewById(R.id.error);
        loading = findViewById(R.id.loading);
        noresults = findViewById(R.id.noresults);

        if (savedInstanceState != null) {
            artistSearch = savedInstanceState.getString(QUERY);
        } else {
            artistSearch = getIntent().getStringExtra(QUERY);
        }

        TextView topTitle = findViewById(R.id.toolbar_title_top);
        TextView bottomTitle = findViewById(R.id.toolbar_title_bottom);
        topTitle.setText(R.string.search_artist_title);
        bottomTitle.setText(artistSearch);

        configSearchRecycler();
        search();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(QUERY, artistSearch);
    }

    private void configSearchRecycler() {
        searchRecycler = findViewById(R.id.search_recycler);
        searchRecycler.setLayoutManager(new LinearLayoutManager(this));
        searchRecycler.setItemViewCacheSize(50);
        searchRecycler.setDrawingCacheEnabled(true);
        searchRecycler.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        searchRecycler.setHasFixedSize(true);
    }

    private void search() {
        noresults.setVisibility(View.GONE);
        viewError(false);

        viewProgressLoading(true);
        api.searchArtist(
                artistSearch,
                result -> {
                    viewProgressLoading(false);
                    if (result.getCount() == 0) {
                        noresults.setVisibility(View.VISIBLE);
                    } else {
                        List<Artist> artists = result.getArtists();
                        ArtistSearchAdapter adapter = new ArtistSearchAdapter(artists);
                        searchRecycler.setAdapter(adapter);
                        adapter.setHolderClickListener(position ->
                                ActivityFactory.startArtistActivity(this, artists.get(position).getId()));
                        saveQueryAsSuggestion();
                    }
                },
                this::showConnectionWarning);
    }

    private void showConnectionWarning(Throwable t) {
        ShowUtil.showError(this, t);
        viewProgressLoading(false);
        viewError(true);
        error.findViewById(R.id.retry_button).setOnClickListener(v -> search());
    }

    private void saveQueryAsSuggestion() {
        if (MusicBrainzApp.getPreferences().isSearchSuggestionsEnabled()) {
            new SearchRecentSuggestions(this, SuggestionProvider.AUTHORITY, SuggestionProvider.MODE)
                    .saveRecentQuery(artistSearch, null);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        searchRecycler.getRecycledViewPool().clear();
        searchRecycler.setAdapter(null);
    }

    private void viewProgressLoading(boolean isView) {
        if (isView) {
            isLoading = true;
            searchRecycler.setAlpha(0.3F);
            loading.setVisibility(View.VISIBLE);
        } else {
            isLoading = false;
            searchRecycler.setAlpha(1.0F);
            loading.setVisibility(View.GONE);
        }
    }

    private void viewError(boolean isView) {
        if (isView) {
            isError = true;
            searchRecycler.setVisibility(View.INVISIBLE);
            error.setVisibility(View.VISIBLE);
        } else {
            isError = false;
            error.setVisibility(View.GONE);
            searchRecycler.setVisibility(View.VISIBLE);
        }
    }

}
