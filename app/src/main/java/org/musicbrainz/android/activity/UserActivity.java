package org.musicbrainz.android.activity;

import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.List;

import org.musicbrainz.android.R;
import org.musicbrainz.android.adapter.pager.BaseFragmentPagerAdapter;
import org.musicbrainz.android.adapter.pager.CollectionsPagerAdapter;
import org.musicbrainz.android.adapter.pager.UserNavigationPagerAdapter;
import org.musicbrainz.android.api.model.Collection;
import org.musicbrainz.android.api.model.Release;
import org.musicbrainz.android.api.site.SiteService;
import org.musicbrainz.android.communicator.GetCollectionCommunicator;
import org.musicbrainz.android.communicator.GetReleasesCommunicator;
import org.musicbrainz.android.communicator.GetUsernameCommunicator;
import org.musicbrainz.android.communicator.OnArtistCommunicator;
import org.musicbrainz.android.communicator.OnCollectionCommunicator;
import org.musicbrainz.android.communicator.OnCreateCollectionCommunicator;
import org.musicbrainz.android.communicator.OnEditCollectionCommunicator;
import org.musicbrainz.android.communicator.OnRecordingCommunicator;
import org.musicbrainz.android.communicator.OnReleaseCommunicator;
import org.musicbrainz.android.communicator.OnReleaseGroupCommunicator;
import org.musicbrainz.android.communicator.OnUserTagCommunicator;
import org.musicbrainz.android.communicator.ShowFloatingActionButtonCommunicator;
import org.musicbrainz.android.communicator.ShowTitleCommunicator;
import org.musicbrainz.android.dialog.PagedReleaseDialogFragment;
import org.musicbrainz.android.fragment.CollectionCreateFragment;
import org.musicbrainz.android.fragment.CollectionEditFragment;
import org.musicbrainz.android.fragment.CollectionFragment;
import org.musicbrainz.android.fragment.CollectionsPagerFragment;
import org.musicbrainz.android.fragment.UserTagPagerFragment;
import org.musicbrainz.android.intent.ActivityFactory;
import org.musicbrainz.android.ui.CustomViewPager;
import org.musicbrainz.android.util.BottomNavigationBehavior;
import org.musicbrainz.android.util.FloatingActionButtonBehavior;
import org.musicbrainz.android.util.ShowUtil;

import static org.musicbrainz.android.MusicBrainzApp.api;
import static org.musicbrainz.android.MusicBrainzApp.oauth;
import static org.musicbrainz.android.adapter.pager.UserNavigationPagerAdapter.TAB_COLLECTIONS_POS;
import static org.musicbrainz.android.adapter.pager.UserNavigationPagerAdapter.TAB_PROFILE_POS;
import static org.musicbrainz.android.adapter.pager.UserNavigationPagerAdapter.TAB_RATINGS_POS;
import static org.musicbrainz.android.adapter.pager.UserNavigationPagerAdapter.TAB_TAGS_POS;

public class UserActivity extends BaseOptionsMenuActivity implements
        GetUsernameCommunicator,
        OnArtistCommunicator,
        OnReleaseGroupCommunicator,
        OnReleaseCommunicator,
        OnRecordingCommunicator,
        GetReleasesCommunicator,
        OnUserTagCommunicator,
        OnCollectionCommunicator,
        GetCollectionCommunicator,
        OnCreateCollectionCommunicator,
        ShowFloatingActionButtonCommunicator,
        OnEditCollectionCommunicator,
        ShowTitleCommunicator,
        CollectionsPagerFragment.CollectionTabOrdinalCommunicator {

    public static final String USERNAME = "USERNAME";
    public static final String USER_NAV_VIEW = "USER_NAV_VIEW";
    public static final int DEFAULT_USER_NAV_VIEW = R.id.user_navigation_profile;

    private int collectionTabOrdinal = -1;
    private String username;
    private int userNavigationView;
    private boolean isLoading;
    private boolean isError;
    private List<Release> releases;
    private Collection collection;
    private boolean isPrivate;

    private BottomNavigationView bottomNavigationView;
    private TextView topTitle;
    private TextView bottomTitle;
    private FrameLayout frameContainer;
    private View error;
    private View loading;
    private FloatingActionButton floatingActionButton;
    private CustomViewPager viewPager;
    private UserNavigationPagerAdapter pagerAdapter;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = item -> {
        frameContainer.setVisibility(View.GONE);
        viewPager.setVisibility(View.VISIBLE);
        floatingActionButton.setVisibility(View.GONE);
        switch (item.getItemId()) {
            case R.id.user_navigation_profile:
                viewPager.setCurrentItem(TAB_PROFILE_POS);
                topTitle.setText(R.string.title_user_profile);
                break;

            case R.id.user_navigation_collections:
                viewPager.setCurrentItem(TAB_COLLECTIONS_POS);
                topTitle.setText(R.string.title_user_collections);
                if (isPrivate) {
                    showFloatingActionButton(true, FloatingButtonType.ADD_TO_COLLECTION);
                }
                break;

            case R.id.user_navigation_ratings:
                viewPager.setCurrentItem(TAB_RATINGS_POS);
                topTitle.setText(R.string.title_user_ratings);
                break;

            case R.id.user_navigation_tags:
                viewPager.setCurrentItem(TAB_TAGS_POS);
                topTitle.setText(R.string.title_user_tags);
                break;
        }
        return true;
    };

    @Override
    protected int getContentLayout() {
        return R.layout.activity_bottom_nav;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        error = findViewById(R.id.error);
        loading = findViewById(R.id.loading);
        frameContainer = findViewById(R.id.frame_container);
        viewPager = findViewById(R.id.viewpager);
        floatingActionButton = findViewById(R.id.floatin_action_btn);
        topTitle = findViewById(R.id.toolbar_title_top);
        bottomTitle = findViewById(R.id.toolbar_title_bottom);

        if (savedInstanceState != null) {
            username = savedInstanceState.getString(USERNAME);
            userNavigationView = savedInstanceState.getInt(USER_NAV_VIEW, DEFAULT_USER_NAV_VIEW);
        } else {
            username = getIntent().getStringExtra(USERNAME);
            userNavigationView = getIntent().getIntExtra(USER_NAV_VIEW, DEFAULT_USER_NAV_VIEW);
        }

        isPrivate = oauth.hasAccount() && username.equals(oauth.getName());

        bottomTitle.setText(username);

        bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.inflateMenu(R.menu.user_nav);
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        // attaching behaviours - hide / showFloatingActionButton on scroll
        ((CoordinatorLayout.LayoutParams) bottomNavigationView.getLayoutParams()).setBehavior(new BottomNavigationBehavior());
        ((CoordinatorLayout.LayoutParams) floatingActionButton.getLayoutParams()).setBehavior(new FloatingActionButtonBehavior());

        refreshTokenAndLoad();
    }

    private void refreshTokenAndLoad() {
        viewError(false);

        if (isPrivate) {
            viewProgressLoading(true);
            oauth.refreshToken(
                    () -> {
                        viewProgressLoading(false);
                        configPager();
                        return null;
                    },
                    this::showConnectionWarning
            );
        } else {
            configPager();
        }
    }

    private void configPager() {
        pagerAdapter = new UserNavigationPagerAdapter(getSupportFragmentManager(), getResources());
        viewPager.setAdapter(pagerAdapter);
        viewPager.setPagingEnabled(false);
        viewPager.setOffscreenPageLimit(pagerAdapter.getCount());
        bottomNavigationView.setSelectedItemId(userNavigationView);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(USERNAME, username);
        outState.putInt(USER_NAV_VIEW, userNavigationView);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        username = savedInstanceState.getString(USERNAME);
        userNavigationView = savedInstanceState.getInt(USER_NAV_VIEW, DEFAULT_USER_NAV_VIEW);
    }

    private void loadFragment(Fragment fragment) {
        viewPager.setVisibility(View.INVISIBLE);
        frameContainer.setVisibility(View.VISIBLE);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public void onArtist(String artistMbid) {
        ActivityFactory.startArtistActivity(this, artistMbid);
    }

    @Override
    public void onRecording(String recordingMbid) {
        ActivityFactory.startRecordingActivity(this, recordingMbid);
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

    @Override
    public void onUserTag(String username, String tag) {
        loadFragment(UserTagPagerFragment.newInstance(username, tag));
    }

    @Override
    public List<Release> getReleases() {
        return releases;
    }

    private void viewProgressLoading(boolean isView) {
        if (isView) {
            isLoading = true;
            frameContainer.setAlpha(0.3F);
            loading.setVisibility(View.VISIBLE);
        } else {
            isLoading = false;
            frameContainer.setAlpha(1.0F);
            loading.setVisibility(View.GONE);
        }
    }

    private void viewError(boolean isView) {
        if (isView) {
            isError = true;
            viewPager.setVisibility(View.INVISIBLE);
            error.setVisibility(View.VISIBLE);
        } else {
            isError = false;
            error.setVisibility(View.GONE);
            viewPager.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onCollection(Collection collection) {
        if (!isLoading) {
            this.collection = collection;
            loadFragment(CollectionFragment.newInstance());
        }
    }

    @Override
    public Collection getCollection() {
        return collection;
    }

    @Override
    public String getCollectionMbid() {
        return null;
    }

    @Override
    public void showFloatingActionButton(boolean visible, FloatingButtonType floatingButtonType) {
        floatingActionButton.setVisibility(visible ? View.VISIBLE : View.GONE);

        if (floatingButtonType != null) {
            floatingActionButton.setImageResource(floatingButtonType.getImgResource());

            switch (floatingButtonType) {
                case ADD_TO_COLLECTION:
                    floatingActionButton.setOnClickListener(v -> loadFragment(CollectionCreateFragment.newInstance()));
                    break;
                case EDIT_COLLECTION:
                    floatingActionButton.setOnClickListener(v -> loadFragment(CollectionEditFragment.newInstance()));
                    break;
                default:
                    floatingActionButton.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onCreateCollection(String name, int type, String description, int publ, EditText editText) {
        if (!isLoading) {
            viewProgressLoading(true);
            api.getCollections(
                    collectionBrowse -> {
                        boolean existName = false;
                        if (collectionBrowse.getCount() > 0) {
                            List<Collection> collections = collectionBrowse.getCollections();
                            for (Collection collection : collections) {
                                if (collection.getName().equalsIgnoreCase(name) &&
                                        collection.getType().equalsIgnoreCase(SiteService.getCollectionTypeFromSpinner(type - 1))) {
                                    viewProgressLoading(false);
                                    String errorString = getString(R.string.collection_create_exist_name);
                                    if (editText != null) {
                                        editText.setError(errorString);
                                    } else {
                                        ShowUtil.showToast(this, errorString);
                                    }
                                    existName = true;
                                    break;
                                }
                            }
                        }
                        if (!existName) {
                            api.createCollection(name, type, description, publ,
                                    responseBody -> {
                                        viewProgressLoading(false);
                                        collectionTabOrdinal = CollectionsPagerAdapter.collectionTabTypeSpinner[type - 1].ordinal();
                                        ((BaseFragmentPagerAdapter.Updatable) pagerAdapter.getFragment(TAB_COLLECTIONS_POS)).update();
                                        bottomNavigationView.setSelectedItemId(R.id.user_navigation_collections);
                                    },
                                    t -> {
                                        viewProgressLoading(false);
                                        ShowUtil.showError(this, t);
                                    });
                        }
                    },
                    t -> {
                        viewProgressLoading(false);
                        ShowUtil.showError(this, t);
                    },
                    100, 0);
        }
    }

    @Override
    public void onEditCollection(String name, int type, String description, int isPublic) {
        if (!isLoading) {
            viewProgressLoading(true);
            api.editCollection(collection, name, type, description, isPublic,
                    responseBody -> {
                        viewProgressLoading(false);
                        collectionTabOrdinal = CollectionsPagerAdapter.collectionTabTypeSpinner[type - 1].ordinal();
                        ((BaseFragmentPagerAdapter.Updatable) pagerAdapter.getFragment(TAB_COLLECTIONS_POS)).update();
                        bottomNavigationView.setSelectedItemId(R.id.user_navigation_collections);
                    },
                    t -> {
                        viewProgressLoading(false);
                        ShowUtil.showError(this, t);
                    });
        }
    }

    private void showConnectionWarning(Throwable t) {
        ShowUtil.showError(this, t);
        viewProgressLoading(false);
        viewError(true);
        error.findViewById(R.id.retry_button).setOnClickListener(v -> refreshTokenAndLoad());
    }

    @Override
    public TextView getTopTitle() {
        return topTitle;
    }

    @Override
    public TextView getBottomTitle() {
        return bottomTitle;
    }

    @Override
    public int getCollectionTabOrdinal() {
        return collectionTabOrdinal;
    }
}
