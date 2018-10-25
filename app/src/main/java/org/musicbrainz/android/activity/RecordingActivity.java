package org.musicbrainz.android.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;

import org.musicbrainz.android.R;
import org.musicbrainz.android.adapter.pager.RecordingNavigationPagerAdapter;
import org.musicbrainz.android.adapter.pager.TagPagerAdapter;
import org.musicbrainz.android.api.model.Artist;
import org.musicbrainz.android.api.model.Collection;
import org.musicbrainz.android.api.model.Recording;
import org.musicbrainz.android.api.model.RelationExtractor;
import org.musicbrainz.android.api.model.Url;
import org.musicbrainz.android.api.model.Work;
import org.musicbrainz.android.api.model.relations.Relation;
import org.musicbrainz.android.api.site.SiteService;
import org.musicbrainz.android.communicator.GetCollectionsCommunicator;
import org.musicbrainz.android.communicator.GetRecordingCommunicator;
import org.musicbrainz.android.communicator.GetUrlsCommunicator;
import org.musicbrainz.android.communicator.GetWorkCommunicator;
import org.musicbrainz.android.communicator.OnArtistCommunicator;
import org.musicbrainz.android.communicator.OnReleaseCommunicator;
import org.musicbrainz.android.communicator.OnTagCommunicator;
import org.musicbrainz.android.communicator.SetWebViewCommunicator;
import org.musicbrainz.android.communicator.ShowFloatingActionButtonCommunicator;
import org.musicbrainz.android.data.DatabaseHelper;
import org.musicbrainz.android.dialog.CollectionsDialogFragment;
import org.musicbrainz.android.dialog.CreateCollectionDialogFragment;
import org.musicbrainz.android.intent.ActivityFactory;
import org.musicbrainz.android.util.FloatingActionButtonBehavior;
import org.musicbrainz.android.util.ShowUtil;

import java.util.ArrayList;
import java.util.List;

import static org.musicbrainz.android.MusicBrainzApp.api;
import static org.musicbrainz.android.MusicBrainzApp.oauth;
import static org.musicbrainz.android.adapter.pager.RecordingNavigationPagerAdapter.TAB_INFO_POS;
import static org.musicbrainz.android.adapter.pager.RecordingNavigationPagerAdapter.TAB_LYRICS_POS;
import static org.musicbrainz.android.adapter.pager.RecordingNavigationPagerAdapter.TAB_RATINGS_POS;
import static org.musicbrainz.android.adapter.pager.RecordingNavigationPagerAdapter.TAB_RELEASES_POS;
import static org.musicbrainz.android.adapter.pager.RecordingNavigationPagerAdapter.TAB_TAGS_POS;
import static org.musicbrainz.android.api.model.Collection.RECORDING_ENTITY_TYPE;
import static org.musicbrainz.android.api.model.Collection.RECORDING_TYPE;
import static org.musicbrainz.android.api.other.CollectionServiceInterface.CollectionType.RECORDINGS;


public class RecordingActivity extends BaseBottomNavActivity implements
        ShowFloatingActionButtonCommunicator,
        OnArtistCommunicator,
        OnReleaseCommunicator,
        OnTagCommunicator,
        GetRecordingCommunicator,
        GetCollectionsCommunicator,
        GetWorkCommunicator,
        GetUrlsCommunicator,
        CollectionsDialogFragment.DialogFragmentListener,
        CreateCollectionDialogFragment.DialogFragmentListener,
        SetWebViewCommunicator {

    public static final String RECORDING_MBID = "RECORDING_MBID";
    public static final int DEFAULT_RECORDING_NAV_VIEW = R.id.recording_nav_lyrics;

    private String mbid;
    private Recording recording;
    private Work work;
    private List<Collection> collections;

    private FloatingActionButton floatingActionButton;
    private WebView webView;

    @Override
    protected void onCreateActivity(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mbid = savedInstanceState.getString(RECORDING_MBID);
        } else {
            mbid = getIntent().getStringExtra(RECORDING_MBID);
        }

        floatingActionButton = findViewById(R.id.floatin_action_btn);
        ((CoordinatorLayout.LayoutParams) floatingActionButton.getLayoutParams()).setBehavior(new FloatingActionButtonBehavior());
        showFloatingActionButton(true, ShowFloatingActionButtonCommunicator.FloatingButtonType.ADD_TO_COLLECTION);
    }

    @Override
    protected int getBottomMenuId() {
        return R.menu.recording_bottom_nav;
    }

    @Override
    protected int getDefaultNavViewId() {
        return DEFAULT_RECORDING_NAV_VIEW;
    }

    @Override
    protected BottomNavigationView.OnNavigationItemSelectedListener getOnNavigationItemSelectedListener() {
        return item -> {
            frameContainer.setVisibility(View.GONE);
            viewPager.setVisibility(View.VISIBLE);
            switch (item.getItemId()) {
                case R.id.recording_nav_lyrics:
                    viewPager.setCurrentItem(TAB_LYRICS_POS);
                    break;

                case R.id.recording_nav_info:
                    viewPager.setCurrentItem(TAB_INFO_POS);
                    break;

                case R.id.recording_nav_releases:
                    viewPager.setCurrentItem(TAB_RELEASES_POS);
                    break;

                case R.id.recording_nav_ratings:
                    viewPager.setCurrentItem(TAB_RATINGS_POS);
                    break;

                case R.id.recording_nav_tags:
                    viewPager.setCurrentItem(TAB_TAGS_POS);
                    break;
            }
            return true;
        };
    }

    @Override
    protected void refreshTokenAndLoad() {
        viewError(false);

        viewProgressLoading(true);
        // refresh token and configure pager fragments
        api.getRecording(
                mbid,
                recording -> {
                    this.recording = recording;
                    if (!recording.getArtistCredits().isEmpty()) {
                        Artist.ArtistCredit artistCredit = recording.getArtistCredits().get(0);
                        topTitle.setText(artistCredit.getName());
                        topTitle.setOnClickListener(v -> onArtist(artistCredit.getArtist().getId()));
                    }
                    if (!TextUtils.isEmpty(recording.getTitle())) {
                        bottomTitle.setText(recording.getTitle());
                    }

                    List<Relation> workRelations = new RelationExtractor(recording).getWorkRelations();
                    if (workRelations != null && !workRelations.isEmpty()) {
                        api.getWork(
                                workRelations.get(0).getWork().getId(),
                                work -> {
                                    this.work = work;
                                    configPager();
                                    viewProgressLoading(false);
                                },
                                this::showConnectionWarning);
                    } else {
                        configPager();
                        viewProgressLoading(false);
                    }

                    //todo: сделать асинхронно
                    DatabaseHelper databaseHelper = new DatabaseHelper(this);
                    databaseHelper.setRecommends(recording.getTags());
                    databaseHelper.close();
                },
                this::showConnectionWarning
        );
    }

    private void configPager() {
        RecordingNavigationPagerAdapter pagerAdapter = new RecordingNavigationPagerAdapter(getSupportFragmentManager(), getResources());
        viewPager.setPagingEnabled(false);
        viewPager.setOffscreenPageLimit(pagerAdapter.getCount());
        viewPager.setAdapter(pagerAdapter);
        bottomNavigationView.setSelectedItemId(navViewId);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(RECORDING_MBID, mbid);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mbid = savedInstanceState.getString(RECORDING_MBID);
    }

    @Override
    public void setWebView(WebView webView) {
        this.webView = webView;
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

    @SuppressLint("RestrictedApi")
    @Override
    public void showFloatingActionButton(boolean visible, ShowFloatingActionButtonCommunicator.FloatingButtonType floatingButtonType) {
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
                            if (collection.getEntityType().equals(RECORDING_ENTITY_TYPE)) {
                                collection.setCount(collection.getRecordingCount());
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
    public void onArtist(String artistMbid) {
        ActivityFactory.startArtistActivity(this, artistMbid);
    }

    @Override
    public void onRelease(String releaseMbid) {
        ActivityFactory.startReleaseActivity(this, releaseMbid);
    }

    @Override
    public void onTag(String tag) {
        ActivityFactory.startTagActivity(this, tag, TagPagerAdapter.TagTab.RECORDING.ordinal());
    }

    @Override
    public Recording getRecording() {
        return recording;
    }

    @Override
    public String getRecordingMbid() {
        return mbid;
    }

    @Override
    public void onCollection(String collectionMbid) {
        viewProgressLoading(true);
        api.addEntityToCollection(
                collectionMbid, RECORDINGS, mbid,
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
        api.createCollection(
                name, SiteService.getCollectionTypeFromSpinner(RECORDING_TYPE), description, publ,
                responseBody -> {
                    api.getCollections(
                            collectionBrowse -> {
                                String id = "";
                                if (collectionBrowse.getCount() > 0) {
                                    List<Collection> collections = collectionBrowse.getCollections();
                                    for (Collection collection : collections) {
                                        if (collection.getEntityType().equals(RECORDING_ENTITY_TYPE) && collection.getName().equals(name)) {
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
    public List<Collection> getCollections() {
        return collections;
    }

    @Override
    public Work getWork() {
        return work;
    }

    @Override
    public String getWorkMbid() {
        return work != null ? work.getId() : null;
    }

    @Override
    public List<Url> getUrls() {
        return work != null ? new RelationExtractor(work).getUrls() : null;
    }
}
