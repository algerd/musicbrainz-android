package org.musicbrainz.android.dialog;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.List;

import org.musicbrainz.android.R;
import org.musicbrainz.android.adapter.recycler.ReleaseAdapter;
import org.musicbrainz.android.api.model.Release;
import org.musicbrainz.android.communicator.GetReleasesCommunicator;
import org.musicbrainz.android.communicator.OnReleaseCommunicator;


/**
 * Dialog that allows the user to choose a specific release when a release group contains more than one release.
 */
public class ReleaseDialogFragment extends DialogFragment {

    public static final String TAG = "ReleaseDialogFragment";

    private RecyclerView releaseRecycler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setStyle(DialogFragment.STYLE_NO_TITLE, 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.dialog_fragment_release, container, false);
        releaseRecycler = layout.findViewById(R.id.release_recycler);
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

    @Override
    public void onStart() {
        super.onStart();
        List<Release> releases = ((GetReleasesCommunicator) getContext()).getReleases();
        if (releases != null) {
            ReleaseAdapter adapter = new ReleaseAdapter(releases, "");
            releaseRecycler.setAdapter(adapter);
            adapter.setHolderClickListener(position -> {
                ((OnReleaseCommunicator) getContext()).onRelease(releases.get(position).getId());
                dismiss();
            });
        }
    }

    @Override
    public void onResume() {
        // Sets the height and the width of the DialogFragment
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        getDialog().getWindow().setLayout(width, height);
        super.onResume();
    }

}
