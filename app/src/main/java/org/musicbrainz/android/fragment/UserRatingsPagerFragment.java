package org.musicbrainz.android.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.musicbrainz.android.R;
import org.musicbrainz.android.adapter.pager.UserRatingsPagerAdapter;


public class UserRatingsPagerFragment extends Fragment {

    public static UserRatingsPagerFragment newInstance() {
        Bundle args = new Bundle();
        UserRatingsPagerFragment fragment = new UserRatingsPagerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_pager_with_icons, container, false);

        ViewPager viewPager = layout.findViewById(R.id.pager);
        TabLayout tabLayout = layout.findViewById(R.id.tabs);

        UserRatingsPagerAdapter pagerAdapter = new UserRatingsPagerAdapter(getChildFragmentManager(), getResources());
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOffscreenPageLimit(pagerAdapter.getCount());
        tabLayout.setupWithViewPager(viewPager);
        pagerAdapter.setupTabViews(tabLayout);

        return layout;
    }

}
