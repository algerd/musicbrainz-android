package org.musicbrainz.android.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.musicbrainz.android.R;
import org.musicbrainz.android.adapter.pager.ArtistInfoPagerAdapter;


public class ArtistInfoPagerFragment extends LazyFragment {

    private ViewPager viewPager;
    private TabLayout tabLayout;

    public static ArtistInfoPagerFragment newInstance() {
        Bundle args = new Bundle();
        ArtistInfoPagerFragment fragment = new ArtistInfoPagerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_pager_with_icons, container, false);

        viewPager = layout.findViewById(R.id.pager);
        tabLayout = layout.findViewById(R.id.tabs);

        loadView();
        return layout;
    }

    @Override
    protected void lazyLoad() {
        ArtistInfoPagerAdapter pagerAdapter = new ArtistInfoPagerAdapter(getChildFragmentManager(), getResources());
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOffscreenPageLimit(pagerAdapter.getCount());
        tabLayout.setupWithViewPager(viewPager);
        pagerAdapter.setupTabViews(tabLayout);
    }
}
