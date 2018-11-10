package org.musicbrainz.android.fragment;


import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.musicbrainz.android.R;
import org.musicbrainz.android.adapter.pager.ReleaseGroupsPagerAdapter;
import org.musicbrainz.android.adapter.recycler.ReleaseGroupsAdapter;
import org.musicbrainz.android.adapter.recycler.RetryCallback;
import org.musicbrainz.android.communicator.GetArtistCommunicator;
import org.musicbrainz.android.communicator.OnReleaseGroupCommunicator;
import org.musicbrainz.android.data.Status;
import org.musicbrainz.android.ui.ReleaseGroupsViewModel;


public class ReleaseGroupsTabFragment extends LazyFragment implements RetryCallback {

    private static final String RELEASES_TAB = "RELEASES_TAB";

    private ReleaseGroupsPagerAdapter.ReleaseTab releaseGroupType;
    private ReleaseGroupsViewModel releaseGroupsViewModel;

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView pagedRecycler;
    private ReleaseGroupsAdapter adapter;

    private TextView errorMessageTextView;
    private Button retryLoadingButton;
    private ProgressBar loadingProgressBar;
    private View itemNetworkState;


    public static ReleaseGroupsTabFragment newInstance(int releasesTab) {
        Bundle args = new Bundle();
        args.putInt(RELEASES_TAB, releasesTab);
        ReleaseGroupsTabFragment fragment = new ReleaseGroupsTabFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_paged_recycler, container, false);

        releaseGroupType = ReleaseGroupsPagerAdapter.ReleaseTab.values()[getArguments().getInt(RELEASES_TAB)];

        pagedRecycler = layout.findViewById(R.id.paged_recycler);
        swipeRefreshLayout = layout.findViewById(R.id.swipe_refresh_layout);
        errorMessageTextView = layout.findViewById(R.id.errorMessageTextView);
        loadingProgressBar = layout.findViewById(R.id.loadingProgressBar);
        itemNetworkState = layout.findViewById(R.id.item_network_state);

        retryLoadingButton = layout.findViewById(R.id.retryLoadingButton);
        retryLoadingButton.setOnClickListener(view -> retry());

        loadView();
        return layout;
    }

    @Override
    protected void lazyLoad() {
        String artistMbid = ((GetArtistCommunicator) getContext()).getArtistMbid();
        if (!TextUtils.isEmpty(artistMbid)) {
            adapter = new ReleaseGroupsAdapter(this);
            adapter.setHolderClickListener(releaseGroup -> ((OnReleaseGroupCommunicator) getContext()).onReleaseGroup(releaseGroup.getId()));

            releaseGroupsViewModel = ViewModelProviders.of(this).get(ReleaseGroupsViewModel.class);
            releaseGroupsViewModel.load(artistMbid, releaseGroupType.getAlbumType());
            releaseGroupsViewModel.realeseGroupLiveData.observe(this, adapter::submitList);
            releaseGroupsViewModel.getNetworkState().observe(this, adapter::setNetworkState);

            pagedRecycler.setLayoutManager(new GridLayoutManager(getContext(), 2));
            pagedRecycler.setNestedScrollingEnabled(true);
            pagedRecycler.setItemViewCacheSize(100);
            pagedRecycler.setHasFixedSize(true);
            pagedRecycler.setAdapter(adapter);

            initSwipeToRefresh();
        }
    }

    private void initSwipeToRefresh() {
        releaseGroupsViewModel.getRefreshState().observe(this, networkState -> {
            if (networkState != null) {

                //Show the current network state for the first getWikidata when the rating list
                //in the adapter is empty and disable swipe to scroll at the first loading
                if (adapter.getCurrentList() == null || adapter.getCurrentList().size() == 0) {
                    itemNetworkState.setVisibility(View.VISIBLE);
                    //error message
                    errorMessageTextView.setVisibility(networkState.getMessage() != null ? View.VISIBLE : View.GONE);
                    if (networkState.getMessage() != null) {
                        errorMessageTextView.setText(networkState.getMessage());
                    }
                    //loading and retry
                    retryLoadingButton.setVisibility(networkState.getStatus() == Status.FAILED ? View.VISIBLE : View.GONE);
                    loadingProgressBar.setVisibility(networkState.getStatus() == Status.RUNNING ? View.VISIBLE : View.GONE);

                    swipeRefreshLayout.setEnabled(networkState.getStatus() == Status.SUCCESS);
                    pagedRecycler.scrollToPosition(0);
                }
            }
        });

        swipeRefreshLayout.setOnRefreshListener(() -> {
            releaseGroupsViewModel.refresh();
            swipeRefreshLayout.setRefreshing(false);
            pagedRecycler.scrollToPosition(0);
        });
    }

    @Override
    public void retry() {
        if (releaseGroupsViewModel != null) {
            releaseGroupsViewModel.retry();
        }
    }

}
