package org.musicbrainz.android.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.musicbrainz.android.R;
import org.musicbrainz.android.adapter.recycler.UserTagsAdapter;
import org.musicbrainz.android.communicator.GetUsernameCommunicator;
import org.musicbrainz.android.communicator.OnUserTagCommunicator;
import org.musicbrainz.android.util.ShowUtil;

import static org.musicbrainz.android.MusicBrainzApp.api;


public class UserTagsFragment extends Fragment {

    private String username;
    private boolean isLoading;
    private boolean isError;

    private View error;
    private View loading;
    private View noresults;
    private RecyclerView tagsRecycler;

    public static UserTagsFragment newInstance() {
        Bundle args = new Bundle();

        UserTagsFragment fragment = new UserTagsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_recycler_view, container, false);

        error = layout.findViewById(R.id.error);
        loading = layout.findViewById(R.id.loading);
        noresults = layout.findViewById(R.id.noresults);
        tagsRecycler = layout.findViewById(R.id.recycler);

        load();
        return layout;
    }

    private void configRecycler() {
        tagsRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        tagsRecycler.setItemViewCacheSize(100);
        tagsRecycler.setHasFixedSize(true);
    }

    private void load() {
        viewError(false);
        noresults.setVisibility(View.GONE);

        username = ((GetUsernameCommunicator) getContext()).getUsername();
        if (username != null) {
            viewProgressLoading(true);
            api.getTags(username,
                    tagSearch -> {
                        viewProgressLoading(false);
                        if (tagSearch.getTags().isEmpty()) {
                            noresults.setVisibility(View.VISIBLE);
                        } else {
                            configRecycler();
                            UserTagsAdapter adapter = new UserTagsAdapter(tagSearch.getTags());
                            adapter.setHolderClickListener(position ->
                                    ((OnUserTagCommunicator) getContext()).onUserTag(username, tagSearch.getTags().get(position).getName()));
                            tagsRecycler.setAdapter(adapter);
                        }
                    },
                    this::showConnectionWarning);
        }
    }

    private void viewProgressLoading(boolean isView) {
        if (isView) {
            isLoading = true;
            loading.setVisibility(View.VISIBLE);
        } else {
            isLoading = false;
            loading.setVisibility(View.GONE);
        }
    }

    private void viewError(boolean isView) {
        if (isView) {
            isError = true;
            error.setVisibility(View.VISIBLE);
        } else {
            isError = false;
            error.setVisibility(View.GONE);
        }
    }

    private void showConnectionWarning(Throwable t) {
        ShowUtil.showError(getContext(), t);
        viewProgressLoading(false);
        viewError(true);
        error.findViewById(R.id.retry_button).setOnClickListener(v -> load());
    }

}
