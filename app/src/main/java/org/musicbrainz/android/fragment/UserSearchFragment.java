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
import org.musicbrainz.android.api.site.UserProfileServiceInterface;
import org.musicbrainz.android.suggestion.SuggestionHelper;
import org.musicbrainz.android.util.ShowUtil;

import static org.musicbrainz.android.MusicBrainzApp.api;

public class UserSearchFragment extends Fragment {

    public interface FragmentListener {
        void searchUser(String user);
    }

    private SuggestionHelper suggestionHelper;

    private AutoCompleteTextView userField;
    private View loading;

    public static UserSearchFragment newInstance() {
        Bundle args = new Bundle();
        UserSearchFragment fragment = new UserSearchFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_user_search, container, false);

        userField = layout.findViewById(R.id.user_field);
        loading = layout.findViewById(R.id.loading);

        layout.findViewById(R.id.search_btn).setOnClickListener(view -> search());
        return layout;
    }

    private boolean search() {
        String user = userField.getText().toString().trim();
        if (!TextUtils.isEmpty(user)) {
            hideKeyboard();

            viewProgressLoading(true);
            api.searchUser(user,
                    responseBody -> {
                        viewProgressLoading(false);
                        ((FragmentListener) getContext()).searchUser(user);
                        userField.setText("");
                    },
                    t -> {
                        viewProgressLoading(false);
                        if (t instanceof UserProfileServiceInterface.EditorNotFoundException) {
                            userField.setError(getResources().getString(R.string.editor_not_found));
                        } else {
                            ShowUtil.showError(getContext(), t);
                        }
                    });
        }
        return false;
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(userField.getWindowToken(), 0);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        suggestionHelper = new SuggestionHelper(getActivity());
    }

    private void viewProgressLoading(boolean isView) {
        if (isView) {
            userField.setAlpha(0.2f);
            loading.setVisibility(View.VISIBLE);
        } else {
            userField.setAlpha(1.0f);
            loading.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (MusicBrainzApp.getPreferences().isSearchSuggestionsEnabled()) {
            userField.setAdapter(suggestionHelper.getAdapter());
        } else {
            userField.setAdapter(suggestionHelper.getEmptyAdapter());
        }
    }

}
