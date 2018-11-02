package org.musicbrainz.android.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.musicbrainz.android.R;
import org.musicbrainz.android.adapter.pager.UserTagsPagerAdapter;
import org.musicbrainz.android.api.model.Tag;
import org.musicbrainz.android.communicator.GetGenresCommunicator;
import org.musicbrainz.android.communicator.GetTagCommunicator;
import org.musicbrainz.android.communicator.GetTagsCommunicator;
import org.musicbrainz.android.communicator.GetUsernameCommunicator;
import org.musicbrainz.android.util.ShowUtil;

import java.util.ArrayList;
import java.util.List;

import static org.musicbrainz.android.MusicBrainzApp.api;


public class UserTagsPagerFragment extends LazyFragment implements
        GetGenresCommunicator,
        GetTagsCommunicator {

    private List<Tag> genres = new ArrayList<>();
    private List<Tag> tags = new ArrayList<>();
    private boolean isLoading;
    private boolean isError;

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private View error;
    private View loading;
    private View noresults;

    public static UserTagsPagerFragment newInstance() {
        Bundle args = new Bundle();
        UserTagsPagerFragment fragment = new UserTagsPagerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_pager_without_icons, container, false);

        error = layout.findViewById(R.id.error);
        loading = layout.findViewById(R.id.loading);
        noresults = layout.findViewById(R.id.noresults);
        viewPager = layout.findViewById(R.id.pager);
        tabLayout = layout.findViewById(R.id.tabs);

        loadView();
        return layout;
    }

    @Override
    protected void lazyLoad() {
        viewError(false);
        noresults.setVisibility(View.GONE);

        String username = ((GetUsernameCommunicator) getContext()).getUsername();
        if (username != null) {
            viewProgressLoading(true);
            api.getTags(username,
                    tagSearch -> {
                        viewProgressLoading(false);
                        if (tagSearch.getTags().isEmpty()) {
                            noresults.setVisibility(View.VISIBLE);
                        } else {
                            genres.clear();
                            tags.clear();
                            for (Tag tag : tagSearch.getTags()) {
                                if (tag.getTagType().equals(Tag.TagType.GENRE)) {
                                    genres.add(tag);
                                } else if (tag.getTagType().equals(Tag.TagType.TAG)) {
                                    tags.add(tag);
                                }
                            }
                            UserTagsPagerAdapter pagerAdapter = new UserTagsPagerAdapter(getChildFragmentManager(), getResources());
                            viewPager.setAdapter(pagerAdapter);
                            viewPager.setOffscreenPageLimit(pagerAdapter.getCount());
                            tabLayout.setupWithViewPager(viewPager);
                            tabLayout.setTabMode(TabLayout.MODE_FIXED);
                            pagerAdapter.setupTabViews(tabLayout);
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
        error.findViewById(R.id.retry_button).setOnClickListener(v -> lazyLoad());
    }

    @Override
    public List<Tag> getGenres() {
        return genres;
    }

    @Override
    public List<Tag> getTags() {
        return tags;
    }
}