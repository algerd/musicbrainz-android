package org.musicbrainz.android.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;

import org.musicbrainz.android.R;
import org.musicbrainz.android.fragment.OtherSearchFragment;
import org.musicbrainz.android.fragment.SearchFragment;
import org.musicbrainz.android.intent.ActivityFactory;
import org.musicbrainz.android.util.ShowUtil;

import java.util.ArrayList;
import java.util.List;

import static org.musicbrainz.android.MusicBrainzApp.api;
import static org.musicbrainz.android.MusicBrainzApp.oauth;

public class MainActivity extends BaseActivity implements
        SearchFragment.SearchFragmentListener,
        OtherSearchFragment.OtherSearchFragmentListener {

    private List<String> genres = new ArrayList<>();

    private View errorView;
    private View logInBtn;

    @Override
    protected int initContentLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        logInBtn = findViewById(R.id.log_in_btn);
        errorView = findViewById(R.id.error);

        if (!oauth.hasAccount()) {
            logInBtn.setVisibility(View.VISIBLE);
            logInBtn.setOnClickListener(v -> ActivityFactory.startLoginActivity(this));
        }
        load();
    }

    private void load() {
        errorView.setVisibility(View.GONE);
        api.getGenres(g -> genres = g, this::showConnectionWarning);
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

    protected void showConnectionWarning(Throwable t) {
        ShowUtil.showError(this, t);
        errorView.setVisibility(View.VISIBLE);
        errorView.findViewById(R.id.retry_button).setOnClickListener(v -> load());
    }

    @Override
    public List<String> getGenres() {
        return genres;
    }

}
