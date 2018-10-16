package org.musicbrainz.android.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;

import org.musicbrainz.android.MusicBrainzApp;
import org.musicbrainz.android.R;
import org.musicbrainz.android.suggestion.SuggestionHelper;

public class SearchFragment extends Fragment {

    public interface FragmentListener {
        void searchArtist(String artist);
        void searchAlbum(String artist, String album);
        void searchTrack(String artist, String album, String track);
    }

    private SuggestionHelper suggestionHelper;

    private AutoCompleteTextView artistField;
    private AutoCompleteTextView albumField;
    private AutoCompleteTextView trackField;

    public static SearchFragment newInstance() {
        Bundle args = new Bundle();
        SearchFragment fragment = new SearchFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_search, container, false);
        artistField = layout.findViewById(R.id.artist_field);
        albumField = layout.findViewById(R.id.album_field);
        trackField = layout.findViewById(R.id.track_field);

        layout.findViewById(R.id.search_btn).setOnClickListener(view -> search());
        return layout;
    }

    private boolean search() {
        String artist = artistField.getText().toString().trim();
        String album = albumField.getText().toString().trim();
        String track = trackField.getText().toString().trim();

        if (!TextUtils.isEmpty(track)) {
            hideKeyboard();
            ((FragmentListener) getContext()).searchTrack(artist, album, track);
        } else if (!TextUtils.isEmpty(album)) {
            hideKeyboard();
            ((FragmentListener) getContext()).searchAlbum(artist, album);
        } else if (!TextUtils.isEmpty(artist)) {
            hideKeyboard();
            ((FragmentListener) getContext()).searchArtist(artist);
        }
        artistField.setText("");
        albumField.setText("");
        trackField.setText("");
        return false;
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(artistField.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(albumField.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(trackField.getWindowToken(), 0);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        suggestionHelper = new SuggestionHelper(getActivity());
    }

    @Override
    public void onResume() {
        super.onResume();
        if (MusicBrainzApp.getPreferences().isSearchSuggestionsEnabled()) {
            artistField.setAdapter(suggestionHelper.getAdapter());
            albumField.setAdapter(suggestionHelper.getAdapter());
            trackField.setAdapter(suggestionHelper.getAdapter());
        } else {
            //artistField.setAdapter(suggestionHelper.getEmptyAdapter());
        }
    }


}
