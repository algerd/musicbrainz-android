package org.musicbrainz.android.activity;

import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import org.musicbrainz.android.MusicBrainzApp;
import org.musicbrainz.android.R;
import org.musicbrainz.android.adapter.recycler.ReleaseGroupSearchAdapter;
import org.musicbrainz.android.api.model.Release;
import org.musicbrainz.android.api.model.ReleaseGroup;
import org.musicbrainz.android.communicator.GetReleasesCommunicator;
import org.musicbrainz.android.communicator.OnReleaseCommunicator;
import org.musicbrainz.android.dialog.PagedReleaseDialogFragment;
import org.musicbrainz.android.intent.ActivityFactory;
import org.musicbrainz.android.suggestion.SuggestionProvider;
import org.musicbrainz.android.util.ShowUtil;

import static org.musicbrainz.android.MusicBrainzApp.api;


public class AlbumSearchActivity extends BaseOptionsMenuActivity implements
        OnReleaseCommunicator,
        GetReleasesCommunicator {

    public static final String ARTIST_SEARCH = "ARTIST_SEARCH";
    public static final String ALBUM_SEARCH = "ALBUM_SEARCH";

    private String artistSearch;
    private String albumSearch;
    private List<Release> releases;

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
            artistSearch = savedInstanceState.getString(ARTIST_SEARCH);
            albumSearch = savedInstanceState.getString(ALBUM_SEARCH);
        } else {
            artistSearch = getIntent().getStringExtra(ARTIST_SEARCH);
            albumSearch = getIntent().getStringExtra(ALBUM_SEARCH);
        }

        TextView topTitle = findViewById(R.id.toolbar_title_top);
        TextView bottomTitle = findViewById(R.id.toolbar_title_bottom);
        topTitle.setText(R.string.search_album_title);
        bottomTitle.setText(!TextUtils.isEmpty(artistSearch) ? artistSearch + " / " + albumSearch : albumSearch);

        configSearchRecycler();
        search();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ARTIST_SEARCH, artistSearch);
        outState.putString(ALBUM_SEARCH, albumSearch);
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
        api.searchAlbum(
                artistSearch, albumSearch,
                result -> {
                    viewProgressLoading(false);
                    if (result.getCount() == 0) {
                        noresults.setVisibility(View.VISIBLE);
                    } else {
                        List<ReleaseGroup> releaseGroups = result.getReleaseGroups();
                        ReleaseGroupSearchAdapter adapter = new ReleaseGroupSearchAdapter(releaseGroups);
                        searchRecycler.setAdapter(adapter);
                        adapter.setHolderClickListener(position -> showReleases(releaseGroups.get(position).getId()));
                        saveQueryAsSuggestion();
                    }
                },
                this::showConnectionWarning);
    }

    private void showReleases(String releaseGroupMbid) {
        viewProgressLoading(true);
        api.getReleasesByAlbum(
                releaseGroupMbid,
                releaseBrowse -> {
                    viewProgressLoading(false);
                    if (releaseBrowse.getCount() > 1) {
                        PagedReleaseDialogFragment.newInstance(releaseGroupMbid).show(getSupportFragmentManager(), PagedReleaseDialogFragment.TAG);
                    } else if (releaseBrowse.getCount() == 1) {
                        onRelease(releaseBrowse.getReleases().get(0).getId());
                    }
                },
                t -> {
                    viewProgressLoading(false);
                    ShowUtil.showError(this, t);
                },
                2, 0);
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
                    .saveRecentQuery(albumSearch, null);
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

    @Override
    public void onRelease(String releaseMbid) {
        ActivityFactory.startReleaseActivity(this, releaseMbid);
    }

    @Override
    public List<Release> getReleases() {
        return releases;
    }

}
