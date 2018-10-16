package org.musicbrainz.android.dialog;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.musicbrainz.android.R;
import org.musicbrainz.android.api.model.Release;

public class ConfirmBarcodeDialog extends DialogFragment {

    public static final String TAG = "ConfirmBarcodeDialog";

    private TextView title;
    private TextView artist;
    private TextView numberOfTracks;
    private TextView formats;
    private TextView labels;
    private TextView releaseDate;
    private TextView country;
    private Button confirm;

    public interface DialogFragmentListener {
        Release getRelease();

        void confirmSubmission();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().setTitle(R.string.barcode_add_header);
        getDialog().setCanceledOnTouchOutside(false);
        View layout = inflater.inflate(R.layout.dialog_confirm_barcode, container, false);
        findViews(layout);
        return layout;
    }

    private void findViews(View layout) {
        title = layout.findViewById(R.id.list_release_title);
        artist = layout.findViewById(R.id.list_release_artist);
        numberOfTracks = layout.findViewById(R.id.list_release_tracksnum);
        formats = layout.findViewById(R.id.list_release_formats);
        labels = layout.findViewById(R.id.list_release_labels);
        releaseDate = layout.findViewById(R.id.list_release_date);
        country = layout.findViewById(R.id.list_release_country);
        confirm = layout.findViewById(R.id.barcode_confirm);
    }

    @Override
    public void onStart() {
        super.onStart();
        Release release = ((DialogFragmentListener) getContext()).getRelease();
        if (release != null) {
            setViews(release);
            confirm.setOnClickListener(v -> {
                ((DialogFragmentListener) getContext()).confirmSubmission();
                dismiss();
            });
        }
    }

    private void setViews(Release release) {
        title.setText(release.getTitle());
        releaseDate.setText(release.getDate());

        /*
        artist.setText(StringFormat.commaSeparateArtists(release.getArtists()));
        numberOfTracks.setText(release.getTracksNum() + " " + getString(R.string.label_tracks));
        formats.setText(StringMapper.buildReleaseFormatsString(App.getContext(), release.getFormats()));
        labels.setText(StringFormat.commaSeparate(release.getLabels()));
        country.setText(release.getCountryCode());
        */
    }

}
