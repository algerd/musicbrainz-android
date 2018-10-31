package org.musicbrainz.android.activity;

import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import org.musicbrainz.android.MusicBrainzApp;
import org.musicbrainz.android.R;
import org.musicbrainz.android.adapter.recycler.ArtistSearchAdapter;
import org.musicbrainz.android.adapter.recycler.ReleaseGroupSearchAdapter;
import org.musicbrainz.android.adapter.recycler.SearchListAdapter;
import org.musicbrainz.android.adapter.recycler.TrackSearchAdapter;
import org.musicbrainz.android.api.model.Artist;
import org.musicbrainz.android.api.model.Recording;
import org.musicbrainz.android.api.model.ReleaseGroup;
import org.musicbrainz.android.api.model.Tag;
import org.musicbrainz.android.communicator.OnReleaseCommunicator;
import org.musicbrainz.android.dialog.PagedReleaseDialogFragment;
import org.musicbrainz.android.intent.ActivityFactory;
import org.musicbrainz.android.suggestion.SuggestionProvider;
import org.musicbrainz.android.util.ShowUtil;

import java.util.ArrayList;
import java.util.List;

import static org.musicbrainz.android.MusicBrainzApp.api;


public class SearchActivity extends BaseActivity implements
        OnReleaseCommunicator {

    // artistSearch:
    // !!! for SearchView.OnQueryTextListener (BaseActivity)
    public static final String QUERY = "query";

    public static final String ALBUM_SEARCH = "ALBUM_SEARCH";
    public static final String TRACK_SEARCH = "TRACK_SEARCH";

    public static final String SEARCH_QUERY = "SEARCH_QUERY";
    public static final String SEARCH_TYPE = "SEARCH_TYPE";

    private String artistSearch;
    private String albumSearch;
    private String trackSearch;
    private String searchQuery;
    private int searchType = -1;
    private boolean isLoading;
    private boolean isError;

    private RecyclerView searchRecycler;
    private View error;
    private View loading;
    private View noresults;
    private TextView topTitle;
    private TextView bottomTitle;

    @Override
    protected int initContentLayout() {
        return R.layout.activity_search;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        error = findViewById(R.id.error);
        loading = findViewById(R.id.loading);
        noresults = findViewById(R.id.noresults);
        topTitle = findViewById(R.id.toolbar_title_top);
        bottomTitle = findViewById(R.id.toolbar_title_bottom);

        if (savedInstanceState != null) {
            artistSearch = savedInstanceState.getString(QUERY);
            albumSearch = savedInstanceState.getString(ALBUM_SEARCH);
            trackSearch = savedInstanceState.getString(TRACK_SEARCH);
            searchQuery = savedInstanceState.getString(SEARCH_QUERY);
            searchType = savedInstanceState.getInt(SEARCH_TYPE, -1);
        } else {
            artistSearch = getIntent().getStringExtra(QUERY);
            albumSearch = getIntent().getStringExtra(ALBUM_SEARCH);
            trackSearch = getIntent().getStringExtra(TRACK_SEARCH);
            searchQuery = getIntent().getStringExtra(SEARCH_QUERY);
            searchType = getIntent().getIntExtra(SEARCH_TYPE, -1);
        }
        configSearchRecycler();
        search();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(QUERY, artistSearch);
        outState.putString(ALBUM_SEARCH, albumSearch);
        outState.putString(TRACK_SEARCH, trackSearch);
        outState.putString(SEARCH_QUERY, searchQuery);
        outState.putInt(SEARCH_TYPE, searchType);
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
        if (searchType != -1) {
            bottomTitle.setText(searchQuery);
            if (searchType == SearchType.TAG.ordinal()) {
                topTitle.setText(R.string.search_tag_title);
                searchTag();
            } else if (searchType == SearchType.USER.ordinal()) {
                topTitle.setText(R.string.search_user_title);
                searchUser();
            }

        } else if (!TextUtils.isEmpty(trackSearch)) {
            topTitle.setText(R.string.search_track_title);
            bottomTitle.setText(!TextUtils.isEmpty(artistSearch) ? artistSearch + " / " + trackSearch : trackSearch);
            searchRecording();
        } else if (!TextUtils.isEmpty(albumSearch)) {
            topTitle.setText(R.string.search_album_title);
            bottomTitle.setText(!TextUtils.isEmpty(artistSearch) ? artistSearch + " / " + albumSearch : albumSearch);
            searchAlbum();
        } else if (!TextUtils.isEmpty(artistSearch)) {
            topTitle.setText(R.string.search_artist_title);
            bottomTitle.setText(artistSearch);
            searchArtist();
        }
    }

    private void searchTag() {
        api.searchTagFromSite(
                searchQuery, 1, 100,
                strings -> {
                    viewProgressLoading(false);
                    if (strings.isEmpty()) {
                        noresults.setVisibility(View.VISIBLE);
                    } else {
                        SearchListAdapter adapter = new SearchListAdapter(strings);
                        searchRecycler.setAdapter(adapter);
                        adapter.setHolderClickListener(position ->
                                ActivityFactory.startTagActivity(this, strings.get(position)));
                        saveQueryAsSuggestion();
                        if (strings.size() == 1) {
                            ActivityFactory.startTagActivity(this, strings.get(0));
                        }
                    }
                },
                this::showConnectionWarning
        );

        /*
        //bad result
        api.searchTagFromWebservice(
                searchQuery, 1, 100,
                tagSearch -> {
                    viewProgressLoading(false);
                    if (tagSearch.getCount() == 0) {
                        noresults.setVisibility(View.VISIBLE);
                    } else {
                        List<Tag> tags = tagSearch.getTags();
                        List<String> strings = new ArrayList<>();
                        for (Tag tag : tags) {
                            strings.add(tag.getName());
                        }
                        if (strings.isEmpty()) {
                            noresults.setVisibility(View.VISIBLE);
                        } else {
                            SearchListAdapter adapter = new SearchListAdapter(strings);
                            searchRecycler.setAdapter(adapter);
                            adapter.setHolderClickListener(position ->
                                    ActivityFactory.startTagActivity(this, strings.get(position)));
                            saveQueryAsSuggestion();
                            if (strings.size() == 1) {
                                ActivityFactory.startTagActivity(this, strings.get(0));
                            }
                        }
                    }
                },
                this::showConnectionWarning
        );
        */
    }

    private void searchUser() {
        api.searchUserFromSite(
                searchQuery, 1, 100,
                strings -> {
                    viewProgressLoading(false);
                    if (strings.isEmpty()) {
                        noresults.setVisibility(View.VISIBLE);
                    } else {
                        SearchListAdapter adapter = new SearchListAdapter(strings);
                        searchRecycler.setAdapter(adapter);
                        adapter.setHolderClickListener(position ->
                                ActivityFactory.startUserActivity(this, strings.get(position)));
                        saveQueryAsSuggestion();
                        if (strings.size() == 1) {
                            ActivityFactory.startUserActivity(this, strings.get(0));
                        }
                    }
                },
                this::showConnectionWarning
        );
    }

    private void searchRecording() {
        api.searchRecording(
                artistSearch, albumSearch, trackSearch,
                result -> {
                    viewProgressLoading(false);
                    if (result.getCount() == 0) {
                        noresults.setVisibility(View.VISIBLE);
                    } else {
                        List<Recording> recordings = result.getRecordings();
                        TrackSearchAdapter adapter = new TrackSearchAdapter(recordings);
                        searchRecycler.setAdapter(adapter);
                        adapter.setHolderClickListener(position ->
                                ActivityFactory.startRecordingActivity(this, recordings.get(position).getId()));
                        saveQueryAsSuggestion();
                        if (recordings.size() == 1) {
                            ActivityFactory.startRecordingActivity(this, recordings.get(0).getId());
                        }
                    }
                },
                this::showConnectionWarning
        );
    }

    private void searchAlbum() {
        api.searchAlbum(
                artistSearch, albumSearch,
                result -> {
                    viewProgressLoading(false);
                    if (result.getCount() == 0) {
                        noresults.setVisibility(View.VISIBLE);
                    }  else {
                        List<ReleaseGroup> releaseGroups = result.getReleaseGroups();
                        ReleaseGroupSearchAdapter adapter = new ReleaseGroupSearchAdapter(releaseGroups);
                        searchRecycler.setAdapter(adapter);
                        adapter.setHolderClickListener(position -> showReleases(releaseGroups.get(position).getId()));
                        saveQueryAsSuggestion();
                        if (releaseGroups.size() == 1) {
                            showReleases(releaseGroups.get(0).getId());
                        }
                    }
                },
                this::showConnectionWarning);
    }

    private void searchArtist() {
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
                        if (artists.size() == 1) {
                            ActivityFactory.startArtistActivity(this, artists.get(0).getId());
                        }
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

    @Override
    public void onRelease(String releaseMbid) {
        ActivityFactory.startReleaseActivity(this, releaseMbid);
    }

    private void showConnectionWarning(Throwable t) {
        ShowUtil.showError(this, t);
        viewProgressLoading(false);
        viewError(true);
        error.findViewById(R.id.retry_button).setOnClickListener(v -> search());
    }

    private void saveQueryAsSuggestion() {
        if (MusicBrainzApp.getPreferences().isSearchSuggestionsEnabled()) {
            SearchRecentSuggestions searchRecentSuggestions = new SearchRecentSuggestions(this, SuggestionProvider.AUTHORITY, SuggestionProvider.MODE);
            searchRecentSuggestions.saveRecentQuery(artistSearch, null);
            searchRecentSuggestions.saveRecentQuery(albumSearch, null);
            searchRecentSuggestions.saveRecentQuery(trackSearch, null);
            searchRecentSuggestions.saveRecentQuery(searchQuery, null);
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
