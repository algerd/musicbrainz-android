package org.musicbrainz.android.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.musicbrainz.android.R;


public class RecordingInfoTabFragment extends Fragment {

    private RecordingInformationFragment recordingInformationFragment;
    private WikipediaWebViewTabFragment wikiFragment;

    public static RecordingInfoTabFragment newInstance() {
        Bundle args = new Bundle();
        RecordingInfoTabFragment fragment = new RecordingInfoTabFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_recording_info, container, false);
        return layout;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        insertNestedFragments();
    }

    private void insertNestedFragments() {
        recordingInformationFragment = new RecordingInformationFragment();
        wikiFragment = new WikipediaWebViewTabFragment();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction
                .replace(R.id.fragment_recording_information, recordingInformationFragment)
                .replace(R.id.fragment_wiki, wikiFragment)
                .commit();
    }

}
