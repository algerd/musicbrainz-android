package org.musicbrainz.android.activity;

import android.os.Bundle;
import android.view.View;

import org.musicbrainz.android.R;
import org.musicbrainz.android.fragment.OtherSearchFragment;
import org.musicbrainz.android.fragment.SearchFragment;
import org.musicbrainz.android.intent.ActivityFactory;

import static org.musicbrainz.android.MusicBrainzApp.oauth;

public class MainActivity extends BaseActivity implements
        SearchFragment.SearchFragmentListener,
        OtherSearchFragment.OtherSearchFragmentListener {

    private View logInBtn;

    @Override
    protected int initContentLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        logInBtn = findViewById(R.id.log_in_btn);
        if (!oauth.hasAccount()) {
            logInBtn.setVisibility(View.VISIBLE);
            logInBtn.setOnClickListener(v -> ActivityFactory.startLoginActivity(this));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (oauth.hasAccount()) {
            logInBtn.setVisibility(View.GONE);
        }
    }

    @Override
    public void searchTrack(String artist, String album, String track) {
        ActivityFactory.startSearchActivity(this, artist, album, track);
    }

    @Override
    public void searchType(SearchType searchType, String query) {
        ActivityFactory.startSearchActivity(this, query, searchType);
    }

}
