package org.musicbrainz.android.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.PagerAdapter;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;

import java.util.ArrayList;
import java.util.List;

import org.musicbrainz.android.R;
import org.musicbrainz.android.adapter.pager.ArtistNavigationPagerAdapter;
import org.musicbrainz.android.adapter.pager.BaseFragmentPagerAdapter;
import org.musicbrainz.android.adapter.pager.TagPagerAdapter;
import org.musicbrainz.android.api.model.Artist;
import org.musicbrainz.android.api.model.Collection;
import org.musicbrainz.android.api.model.RelationExtractor;
import org.musicbrainz.android.api.model.Url;
import org.musicbrainz.android.api.site.SiteService;
import org.musicbrainz.android.communicator.GetArtistCommunicator;
import org.musicbrainz.android.communicator.GetCollectionsCommunicator;
import org.musicbrainz.android.communicator.GetUrlsCommunicator;
import org.musicbrainz.android.communicator.OnArtistCommunicator;
import org.musicbrainz.android.communicator.OnReleaseCommunicator;
import org.musicbrainz.android.communicator.OnReleaseGroupCommunicator;
import org.musicbrainz.android.communicator.OnTagCommunicator;
import org.musicbrainz.android.communicator.SetWebViewCommunicator;
import org.musicbrainz.android.communicator.ShowFloatingActionButtonCommunicator;
import org.musicbrainz.android.data.DatabaseHelper;
import org.musicbrainz.android.dialog.CollectionsDialogFragment;
import org.musicbrainz.android.dialog.CreateCollectionDialogFragment;
import org.musicbrainz.android.dialog.PagedReleaseDialogFragment;
import org.musicbrainz.android.intent.ActivityFactory;
import org.musicbrainz.android.util.FloatingActionButtonBehavior;
import org.musicbrainz.android.util.ShowUtil;

import static org.musicbrainz.android.MusicBrainzApp.api;
import static org.musicbrainz.android.MusicBrainzApp.oauth;
import static org.musicbrainz.android.adapter.pager.ArtistNavigationPagerAdapter.TAB_INFO_POS;
import static org.musicbrainz.android.adapter.pager.ArtistNavigationPagerAdapter.TAB_RATINGS_POS;
import static org.musicbrainz.android.adapter.pager.ArtistNavigationPagerAdapter.TAB_RELEASES_POS;
import static org.musicbrainz.android.adapter.pager.ArtistNavigationPagerAdapter.TAB_TAGS_POS;
import static org.musicbrainz.android.api.model.Collection.ARTIST_ENTITY_TYPE;
import static org.musicbrainz.android.api.model.Collection.ARTIST_TYPE;
import static org.musicbrainz.android.api.other.CollectionServiceInterface.CollectionType.ARTISTS;


public class ArtistActivity extends BaseBottomNavActivity implements
        GetArtistCommunicator,
        OnReleaseGroupCommunicator,
        OnReleaseCommunicator,
        ShowFloatingActionButtonCommunicator,
        CollectionsDialogFragment.DialogFragmentListener,
        CreateCollectionDialogFragment.DialogFragmentListener,
        GetCollectionsCommunicator,
        GetUrlsCommunicator,
        OnArtistCommunicator,
        OnTagCommunicator,
        SetWebViewCommunicator {

    public static final String ARTIST_MBID = "ARTIST_MBID";
    public static final int DEFAULT_ARTIST_NAV_VIEW = R.id.artist_nav_releases;

    private String mbid;
    private Artist artist;
    private List<Collection> collections;
    private FloatingActionButton floatingActionButton;
    private WebView webView;

    @Override
    protected void onCreateActivity(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mbid = savedInstanceState.getString(ARTIST_MBID);
        } else {
            mbid = getIntent().getStringExtra(ARTIST_MBID);
        }
        floatingActionButton = findViewById(R.id.floatin_action_btn);
        ((CoordinatorLayout.LayoutParams) floatingActionButton.getLayoutParams()).setBehavior(new FloatingActionButtonBehavior());
        showFloatingActionButton(true, ShowFloatingActionButtonCommunicator.FloatingButtonType.ADD_TO_COLLECTION);
    }

    @Override
    protected BottomNavigationView.OnNavigationItemSelectedListener initOnNavigationItemSelectedListener() {
        return item -> {
            frameContainer.setVisibility(View.GONE);
            viewPager.setVisibility(View.VISIBLE);
            switch (item.getItemId()) {
                case R.id.artist_nav_releases:
                    viewPager.setCurrentItem(TAB_RELEASES_POS);
                    topTitle.setText(R.string.title_artist_releases);
                    break;

                case R.id.artist_nav_info:
                    viewPager.setCurrentItem(TAB_INFO_POS);
                    topTitle.setText(R.string.title_artist_info);
                    break;

                case R.id.artist_nav_ratings:
                    viewPager.setCurrentItem(TAB_RATINGS_POS);
                    topTitle.setText(R.string.title_artist_ratings);
                    break;

                case R.id.artist_nav_tags:
                    viewPager.setCurrentItem(TAB_TAGS_POS);
                    topTitle.setText(R.string.title_tags_genres);
                    break;
            }
            return true;
        };
    }

    @Override
    protected int initBottomMenuId() {
        return R.menu.artist_bottom_nav;
    }

    @Override
    protected int initDefaultNavViewId() {
        return DEFAULT_ARTIST_NAV_VIEW;
    }

    @Override
    protected BaseFragmentPagerAdapter initBottomNavigationPagerAdapter() {
        return new ArtistNavigationPagerAdapter(getSupportFragmentManager(), getResources());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ARTIST_MBID, mbid);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mbid = savedInstanceState.getString(ARTIST_MBID);
    }

    @Override
    protected void load() {
        viewError(false);

        viewProgressLoading(true);
        // refresh token and configure pager fragments
        api.getArtist(mbid,
                artist -> {
                    viewProgressLoading(false);
                    if (!TextUtils.isEmpty(artist.getName())) {
                        bottomTitle.setText(artist.getName());
                    }
                    this.artist = artist;
                    configBottomNavigationPager();
                    //todo: сделать асинхронно
                    DatabaseHelper databaseHelper = new DatabaseHelper(this);
                    databaseHelper.setRecommends(artist.getTags());
                    databaseHelper.close();
                },
                this::showConnectionWarning);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (webView != null) {
            if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
                webView.goBack();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public Artist getArtist() {
        return artist;
    }

    @Override
    public String getArtistMbid() {
        return mbid;
    }

    @Override
    public void onReleaseGroup(String releaseGroupMbid) {
        if (!isLoading) {
            // c автоматическим переходом при 1 релизе альбома засчёт предварительной прогрузки релизов альбома
            showReleases(releaseGroupMbid);
            // без автоматического перехода при 1 релизе альбома
            //PagedReleaseDialogFragment.newInstance(releaseGroupMbid).show(getSupportFragmentManager(), PagedReleaseDialogFragment.TAG);
        }
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

    @SuppressLint("RestrictedApi")
    @Override
    public void showFloatingActionButton(boolean visible, FloatingButtonType floatingButtonType) {
        floatingActionButton.setVisibility(visible ? View.VISIBLE : View.GONE);

        if (floatingButtonType != null) {
            floatingActionButton.setImageResource(floatingButtonType.getImgResource());

            switch (floatingButtonType) {
                case ADD_TO_COLLECTION:
                    floatingActionButton.setOnClickListener(v -> {
                        if (!isLoading) {
                            if (oauth.hasAccount()) {
                                showCollections();
                            } else {
                                ActivityFactory.startLoginActivity(this);
                            }
                        }
                    });
                    break;

                default:
                    floatingActionButton.setVisibility(View.GONE);
            }
        }
    }

    private void showCollections() {
        viewProgressLoading(true);
        api.getCollections(
                collectionBrowse -> {
                    viewProgressLoading(false);
                    collections = new ArrayList<>();
                    if (collectionBrowse.getCount() > 0) {
                        for (Collection collection : collectionBrowse.getCollections()) {
                            if (collection.getEntityType().equals(ARTIST_ENTITY_TYPE)) {
                                collection.setCount(collection.getArtistCount());
                                collections.add(collection);
                            }
                        }
                        new CollectionsDialogFragment().show(getSupportFragmentManager(), CollectionsDialogFragment.TAG);
                    }
                },
                this::showConnectionWarning,
                100, 0
        );
    }

    @Override
    public List<Collection> getCollections() {
        return collections;
    }

    @Override
    public void onCollection(String collectionMbid) {
        viewProgressLoading(true);
        api.addEntityToCollection(
                collectionMbid, ARTISTS, mbid,
                metadata -> {
                    viewProgressLoading(false);
                    if (metadata.getMessage().getText().equals("OK")) {
                        //todo: snackbar or toast?
                        ShowUtil.showMessage(this, getString(R.string.collection_added));
                    } else {
                        ShowUtil.showMessage(this, "Error");
                    }
                },
                this::showConnectionWarning);
    }

    @Override
    public void showCreateCollection() {
        new CreateCollectionDialogFragment().show(getSupportFragmentManager(), CreateCollectionDialogFragment.TAG);
    }

    @Override
    public void onCreateCollection(String name, String description, int publ) {
        viewProgressLoading(true);
        // create collection and add artist to it
        api.createCollection(
                name, SiteService.getCollectionTypeFromSpinner(ARTIST_TYPE), description, publ,
                responseBody -> {
                    // get collection id and add artist to this collection
                    api.getCollections(
                            collectionBrowse -> {
                                String id = "";
                                if (collectionBrowse.getCount() > 0) {
                                    List<Collection> collections = collectionBrowse.getCollections();
                                    for (Collection collection : collections) {
                                        if (collection.getEntityType().equals(ARTIST_ENTITY_TYPE) && collection.getName().equals(name)) {
                                            id = collection.getId();
                                            break;
                                        }
                                    }
                                }
                                if (!TextUtils.isEmpty(id)) {
                                    onCollection(id);
                                } else {
                                    ShowUtil.showMessage(this, "Error");
                                    viewProgressLoading(false);
                                }
                            },
                            this::showConnectionWarning,
                            100, 0);
                },
                this::showConnectionWarning);
    }

    @Override
    public List<Url> getUrls() {
        return artist != null ? new RelationExtractor(artist).getUrls() : null;
    }

    @Override
    public void onArtist(String artistMbid) {
        ActivityFactory.startArtistActivity(this, artistMbid);
    }

    @Override
    public void onTag(String tag, boolean isGenre) {
        ActivityFactory.startTagActivity(this, tag, TagPagerAdapter.TagTab.ARTIST, isGenre);
    }

    @Override
    public void setWebView(WebView webView) {
        this.webView = webView;
    }

}
