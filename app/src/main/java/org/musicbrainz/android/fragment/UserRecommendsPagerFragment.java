package org.musicbrainz.android.fragment;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.musicbrainz.android.R;
import org.musicbrainz.android.adapter.pager.UserRecommendsPagerAdapter;


public class UserRecommendsPagerFragment extends Fragment {

    private ViewPager viewPager;
    private TabLayout tabLayout;

    public static UserRecommendsPagerFragment newInstance() {
        Bundle args = new Bundle();
        UserRecommendsPagerFragment fragment = new UserRecommendsPagerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_pager_with_icons, container, false);

        viewPager = layout.findViewById(R.id.pager);
        tabLayout = layout.findViewById(R.id.tabs);

        UserRecommendsPagerAdapter pagerAdapter = new UserRecommendsPagerAdapter(getChildFragmentManager(), getResources());
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOffscreenPageLimit(pagerAdapter.getCount());
        tabLayout.setupWithViewPager(viewPager);
        pagerAdapter.setupTabViews(tabLayout);

        return layout;
    }

}
