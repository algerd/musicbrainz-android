package org.musicbrainz.android.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;

import org.musicbrainz.android.MusicBrainzApp;
import org.musicbrainz.android.R;
import org.musicbrainz.android.activity.SearchType;
import org.musicbrainz.android.suggestion.SuggestionHelper;

import java.util.ArrayList;
import java.util.List;


public class OtherSearchFragment extends Fragment {

    public interface OtherSearchFragmentListener {
        void searchType(SearchType searchType, String query);
    }

    private SuggestionHelper suggestionHelper;
    private AutoCompleteTextView searchField;
    private Spinner searchTypeSpinner;

    public static SearchFragment newInstance() {
        Bundle args = new Bundle();
        SearchFragment fragment = new SearchFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_other_search, container, false);

        searchTypeSpinner = layout.findViewById(R.id.search_spin);
        searchField = layout.findViewById(R.id.query_input);
        searchField.setOnEditorActionListener((view, actionId, event) -> search());
        layout.findViewById(R.id.search_btn).setOnClickListener(view -> search());

        return layout;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupSearchTypeSpinner();
        suggestionHelper = new SuggestionHelper(getActivity());
    }

    private void setupSearchTypeSpinner() {
        List<CharSequence> types = new ArrayList<>();
        for (SearchType searchType : SearchType.values()) {
            types.add(getResources().getText(searchType.getRes()));
        }
        ArrayAdapter<CharSequence> typeAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, types);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        searchTypeSpinner.setAdapter(typeAdapter);
    }

    private boolean search() {
        String query = searchField.getText().toString().trim();
        if (!TextUtils.isEmpty(query)) {
            hideKeyboard();
            SearchType searchType = SearchType.values()[searchTypeSpinner.getSelectedItemPosition()];
            ((OtherSearchFragmentListener) getContext()).searchType(searchType, query);
            searchField.setText("");
        }
        return false;
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchField.getWindowToken(), 0);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (MusicBrainzApp.getPreferences().isSearchSuggestionsEnabled()) {
            searchField.setAdapter(suggestionHelper.getAdapter());
        } else {
            searchField.setAdapter(suggestionHelper.getEmptyAdapter());
        }
    }

}
