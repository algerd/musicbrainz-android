package org.musicbrainz.android.fragment;


import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.musicbrainz.android.R;
import org.musicbrainz.android.adapter.recycler.PagedEntityTagAdapter;
import org.musicbrainz.android.adapter.recycler.RetryCallback;
import org.musicbrainz.android.communicator.GetTagCommunicator;
import org.musicbrainz.android.communicator.OnRecordingCommunicator;
import org.musicbrainz.android.data.Status;
import org.musicbrainz.android.ui.TagViewModel;

import static org.musicbrainz.android.api.site.TagServiceInterface.TagType.RECORDING;


public class TagRecordingsFragment extends Fragment implements
        RetryCallback {

    private TagViewModel tagViewModel;

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView tagRecycler;
    private PagedEntityTagAdapter adapter;

    private TextView errorMessageTextView;
    private Button retryLoadingButton;
    private ProgressBar loadingProgressBar;
    private View itemNetworkState;

    public static TagRecordingsFragment newInstance() {
        Bundle args = new Bundle();
        TagRecordingsFragment fragment = new TagRecordingsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_tag, container, false);

        tagRecycler = layout.findViewById(R.id.tag_recycler);
        swipeRefreshLayout = layout.findViewById(R.id.swipe_refresh_layout);
        errorMessageTextView = layout.findViewById(R.id.errorMessageTextView);
        loadingProgressBar = layout.findViewById(R.id.loadingProgressBar);
        itemNetworkState = layout.findViewById(R.id.item_network_state);

        retryLoadingButton = layout.findViewById(R.id.retryLoadingButton);
        retryLoadingButton.setOnClickListener(view -> retry());

        load();
        return layout;
    }

    public void load() {
        String tag = ((GetTagCommunicator) getContext()).getTag();
        if (tag != null) {
            adapter = new PagedEntityTagAdapter(this);
            adapter.setHolderClickListener(tagEntity -> ((OnRecordingCommunicator) getContext()).onRecording(tagEntity.getMbid()));

            tagViewModel = ViewModelProviders.of(this).get(TagViewModel.class);
            tagViewModel.load(RECORDING, tag);
            tagViewModel.tagLiveData.observe(this, adapter::submitList);
            tagViewModel.getNetworkState().observe(this, adapter::setNetworkState);

            tagRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
            tagRecycler.setNestedScrollingEnabled(true);
            tagRecycler.setAdapter(adapter);

            initSwipeToRefresh();
        }
    }

    /**
     * Init swipe to refresh and enable pull to refresh only when there are items in the adapter
     */
    private void initSwipeToRefresh() {
        tagViewModel.getRefreshState().observe(this, networkState -> {
            if (networkState != null) {

                //Show the current network state for the first getWikidataQ when the rating list
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
                }
                tagRecycler.scrollToPosition(0);
            }
        });

        swipeRefreshLayout.setOnRefreshListener(() -> {
            tagViewModel.refresh();
            swipeRefreshLayout.setRefreshing(false);
        });
    }

    @Override
    public void retry() {
        if (tagViewModel != null) {
            tagViewModel.retry();
        }
    }

}
