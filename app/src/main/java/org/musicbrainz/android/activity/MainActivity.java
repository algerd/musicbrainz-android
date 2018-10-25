package org.musicbrainz.android.activity;

import android.os.Bundle;

import org.musicbrainz.android.R;
import org.musicbrainz.android.fragment.SearchFragment;
import org.musicbrainz.android.fragment.UserSearchFragment;
import org.musicbrainz.android.intent.ActivityFactory;

public class MainActivity extends BaseActivity implements
        SearchFragment.SearchFragmentListener,
        UserSearchFragment.FragmentListener {

    @Override
    protected int getContentLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void searchTrack(String artist, String album, String track) {
        ActivityFactory.startSearchActivity(this, artist, album, track);
    }

    @Override
    public void searchUser(String user) {
        ActivityFactory.startUserActivity(this, user);
    }
}
