package org.musicbrainz.android.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import org.musicbrainz.android.R;
import org.musicbrainz.android.adapter.recycler.ReleaseAdapter;
import org.musicbrainz.android.api.model.Release;
import org.musicbrainz.android.util.ShowUtil;

import static org.musicbrainz.android.MusicBrainzApp.api;


public class BarcodeSearchFragment extends Fragment implements
        TextWatcher {

    public interface FragmentListener {
        void onRelease(Release release);
    }

    private static final String BARCODE = "barcode";

    private RecyclerView releaseRecycler;
    private EditText barcodeText;
    private EditText searchBox;
    private EditText searchArtist;
    private ImageButton searchButton;
    private TextView instructions;
    private TextView noResults;
    private View loading;
    private View error;

    public static BarcodeSearchFragment newInstance(String barcode) {
        Bundle args = new Bundle();
        args.putString(BARCODE, barcode);

        BarcodeSearchFragment fragment = new BarcodeSearchFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_barcode_search, container, false);
        searchBox = layout.findViewById(R.id.barcode_search);
        searchArtist = layout.findViewById(R.id.artist_search);
        barcodeText = layout.findViewById(R.id.barcode);
        searchButton = layout.findViewById(R.id.barcode_search_btn);
        instructions = layout.findViewById(R.id.barcode_instructions);
        noResults = layout.findViewById(R.id.noresults);
        loading = layout.findViewById(R.id.loading);
        error = layout.findViewById(R.id.error);
        releaseRecycler = layout.findViewById(R.id.release_recycler);

        barcodeText.setText(getArguments().getString(BARCODE));

        setListeners();
        configReleaseRecycler();
        return layout;
    }

    private void configReleaseRecycler() {
        releaseRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        releaseRecycler.setItemViewCacheSize(50);
        releaseRecycler.setDrawingCacheEnabled(true);
        releaseRecycler.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        releaseRecycler.setHasFixedSize(true);
    }

    private void setListeners() {
        searchBox.setOnEditorActionListener((v, actionId, event) -> {
            if (v.getId() == R.id.barcode_search && actionId == EditorInfo.IME_NULL) {
                search();
            }
            return false;
        });
        searchButton.setOnClickListener(v -> search());
        barcodeText.addTextChangedListener(this);
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchBox.getWindowToken(), 0);
    }

    private void search() {
        error.setVisibility(View.GONE);
        instructions.setVisibility(View.INVISIBLE);
        noResults.setVisibility(View.GONE);
        loading.setVisibility(View.GONE);
        releaseRecycler.setAdapter(null);

        String term = searchBox.getText().toString().trim();
        if (!TextUtils.isEmpty(term)) {
            hideKeyboard();

            loading.setVisibility(View.VISIBLE);
            api.searchRelease(
                    searchArtist.getText().toString().trim(), term,
                    releaseSearch -> {
                        loading.setVisibility(View.GONE);
                        if (releaseSearch.getCount() > 0) {
                            List<Release> releases = releaseSearch.getReleases();
                            ReleaseAdapter adapter = new ReleaseAdapter(releases, "");
                            releaseRecycler.setAdapter(adapter);
                            adapter.setHolderClickListener(position ->
                                    ((FragmentListener) getContext()).onRelease(releases.get(position)));
                        } else {
                            noResults.setVisibility(View.VISIBLE);
                        }
                    },
                    this::showConnectionWarning,
                    100, 0
            );
        } else {
            Toast.makeText(getContext(), R.string.toast_search_err, Toast.LENGTH_SHORT).show();
        }
    }

    private void showConnectionWarning(Throwable t) {
        ShowUtil.showError(getActivity(), t);
        loading.setVisibility(View.GONE);
        error.setVisibility(View.VISIBLE);
        error.findViewById(R.id.retry_button).setOnClickListener(v -> search());
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (!isDigits(s)) {
            barcodeText.setError(getString(R.string.barcode_invalid_chars));
        } else if (!isBarcodeLengthValid(s)) {
            barcodeText.setError(getString(R.string.barcode_invalid_length));
        } else {
            barcodeText.setError(null);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
    }

    private boolean isBarcodeLengthValid(CharSequence s) {
        return s.length() == 12 || s.length() == 13;
    }

    private boolean isDigits(CharSequence s) {
        String barcode = s.toString();
        char[] chars = barcode.toCharArray();
        for (char c : chars) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }
        return true;
    }

}
