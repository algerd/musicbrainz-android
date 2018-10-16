package org.musicbrainz.android.activity;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.Toast;

import java.util.List;

import org.musicbrainz.android.R;
import org.musicbrainz.android.api.model.Release;
import org.musicbrainz.android.communicator.GetReleasesCommunicator;
import org.musicbrainz.android.communicator.LoadingCommunicator;
import org.musicbrainz.android.communicator.OnReleaseCommunicator;
import org.musicbrainz.android.dialog.BarcodeNotFoundDialog;
import org.musicbrainz.android.dialog.ConfirmBarcodeDialog;
import org.musicbrainz.android.dialog.ReleaseDialogFragment;
import org.musicbrainz.android.fragment.BarcodeSearchFragment;
import org.musicbrainz.android.intent.ActivityFactory;
import org.musicbrainz.android.util.ShowUtil;

import static org.musicbrainz.android.MusicBrainzApp.api;

public class BarcodeSearchActivity extends BaseOptionsMenuActivity implements
        BarcodeSearchFragment.FragmentListener,
        BarcodeNotFoundDialog.DialogFragmentListener,
        OnReleaseCommunicator,
        GetReleasesCommunicator,
        ConfirmBarcodeDialog.DialogFragmentListener,
        LoadingCommunicator {

    public static final String BARCODE = "barcode";

    private List<Release> releases;
    private Release release;

    private boolean isLoading;
    private boolean isError;
    private String barcode = "";

    private View content;
    private View error;
    private View loading;

    @Override
    protected int getContentLayout() {
        return R.layout.activity_barcode_search;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        error = findViewById(R.id.error);
        loading = findViewById(R.id.loading);
        content = findViewById(R.id.content);

        if (savedInstanceState != null) {
            barcode = savedInstanceState.getString(BARCODE);
        } else {
            barcode = getIntent().getStringExtra(BARCODE);
        }
        searchReleasesWithBarcode();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(BARCODE, barcode);
    }

    private void searchReleasesWithBarcode() {
        viewError(false);

        viewProgressLoading(true);
        api.searchReleasesByBarcode(barcode,
                releaseSearch -> {
                    viewProgressLoading(false);
                    releases = releaseSearch.getReleases();
                    if (releaseSearch.getCount() > 0) {
                        new ReleaseDialogFragment().show(getSupportFragmentManager(), ReleaseDialogFragment.TAG);
                    } else if (releaseSearch.getCount() == 1) {
                        onRelease(releases.get(0).getId());
                    } else {
                        DialogFragment barcodeNotFound = BarcodeNotFoundDialog.newInstance(barcode);
                        barcodeNotFound.show(getSupportFragmentManager(), BarcodeNotFoundDialog.TAG);
                    }
                },
                this::showConnectionWarning);
    }

    @Override
    public void viewProgressLoading(boolean isView) {
        if (isView) {
            isLoading = true;
            content.setAlpha(0.3F);
            loading.setVisibility(View.VISIBLE);
        } else {
            isLoading = false;
            content.setAlpha(1.0F);
            loading.setVisibility(View.GONE);
        }
    }

    private void viewError(boolean isView) {
        if (isView) {
            isError = true;
            content.setVisibility(View.INVISIBLE);
            error.setVisibility(View.VISIBLE);
        } else {
            isError = false;
            error.setVisibility(View.GONE);
            content.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void showConnectionWarning(Throwable t) {
        ShowUtil.showError(this, t);
        viewProgressLoading(false);
        viewError(true);
        error.findViewById(R.id.retry_button).setOnClickListener(v -> searchReleasesWithBarcode());
    }

    @Override
    public void addBarcode() {
        getSupportFragmentManager().beginTransaction()
                .add(R.id.content, BarcodeSearchFragment.newInstance(barcode)).commit();
    }

    @Override
    public List<Release> getReleases() {
        return releases;
    }

    @Override
    public void onRelease(String releaseMbid) {
        ActivityFactory.startReleaseActivity(this, releaseMbid);
    }

    @Override
    public void onRelease(Release release) {
        this.release = release;
        DialogFragment submitDialog = new ConfirmBarcodeDialog();
        submitDialog.show(getSupportFragmentManager(), ConfirmBarcodeDialog.TAG);
    }


    @Override
    public Release getRelease() {
        return release;
    }

    @Override
    public void confirmSubmission() {
        viewError(false);
        viewProgressLoading(true);
        api.postBarcode(
                release.getId(), barcode,
                metadata -> {
                    viewProgressLoading(false);
                    if (metadata.getMessage().getText().equals("OK")) {
                        Toast.makeText(this, getString(R.string.barcode_added), Toast.LENGTH_SHORT).show();
                    } else {
                        ShowUtil.showMessage(this, "Error ");
                    }
                },
                t -> {
                    ShowUtil.showError(this, t);
                    viewProgressLoading(false);
                    viewError(true);
                    error.findViewById(R.id.retry_button).setOnClickListener(v -> confirmSubmission());
                }
        );
    }

}
