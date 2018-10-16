package org.musicbrainz.android.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.musicbrainz.android.R;


public class ReleaseInfoTabFragment extends Fragment {

    private ReleaseInformationFragment releaseInformationFragment;
    private WikipediaWebViewTabFragment wikiFragment;

    public static ReleaseInfoTabFragment newInstance() {
        Bundle args = new Bundle();

        ReleaseInfoTabFragment fragment = new ReleaseInfoTabFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_release_info_tab, container, false);
        return layout;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        insertNestedFragments();
    }

    private void insertNestedFragments() {
        releaseInformationFragment = new ReleaseInformationFragment();
        wikiFragment = new WikipediaWebViewTabFragment();

        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_release_information, releaseInformationFragment)
                .replace(R.id.fragment_wiki, wikiFragment)
                .commit();
    }

}
