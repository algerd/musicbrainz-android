package org.musicbrainz.android.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import org.musicbrainz.android.R;
import org.musicbrainz.android.adapter.recycler.CoverArtAdapter;
import org.musicbrainz.android.api.coverart.CoverArtImage;
import org.musicbrainz.android.api.coverart.ReleaseCoverArt;
import org.musicbrainz.android.communicator.GetReleaseCommunicator;
import org.musicbrainz.android.intent.ActivityFactory;

import static org.musicbrainz.android.MusicBrainzApp.api;


public class ReleaseCoverArtFragment extends Fragment {

    private RecyclerView coverartRecycler;
    private View loading;
    private View noresults;

    public static ReleaseCoverArtFragment newInstance() {
        Bundle args = new Bundle();

        ReleaseCoverArtFragment fragment = new ReleaseCoverArtFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_cover_art, container, false);

        loading = layout.findViewById(R.id.loading);
        noresults = layout.findViewById(R.id.noresults);
        coverartRecycler = layout.findViewById(R.id.coverart_recycler);

        configCoverartRecycler();
        load();
        return layout;
    }

    public void load() {
        noresults.setVisibility(View.GONE);

        String releaseMbid = ((GetReleaseCommunicator) getContext()).getReleaseMbid();
        if (!TextUtils.isEmpty(releaseMbid)) {
            loading.setVisibility(View.VISIBLE);
            api.getReleaseCoverArt(
                    releaseMbid,
                    this::displayResult,
                    t -> {
                        loading.setVisibility(View.GONE);
                        noresults.setVisibility(View.VISIBLE);
                    }
            );
        }
    }

    private void configCoverartRecycler() {
        coverartRecycler.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        coverartRecycler.setItemViewCacheSize(50);
        coverartRecycler.setDrawingCacheEnabled(false);
        coverartRecycler.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        coverartRecycler.setHasFixedSize(true);
    }

    private void displayResult(ReleaseCoverArt coverArt) {
        loading.setVisibility(View.GONE);
        List<CoverArtImage> images = coverArt.getImages();
        List<CoverArtImage> coverArts = new ArrayList<>();
        if (images != null && !images.isEmpty()) {
            for (CoverArtImage image : images) {
                CoverArtImage.Thumbnails thumbnails = image.getThumbnails();
                if (thumbnails != null && !TextUtils.isEmpty(thumbnails.getLarge())) {
                    coverArts.add(image);
                }
            }
        }
        if (!coverArts.isEmpty()) {
            CoverArtAdapter adapter = new CoverArtAdapter(coverArts);
            coverartRecycler.setAdapter(adapter);
            adapter.setHolderClickListener(position -> {
                String imageUrl = coverArts.get(position).getImage();
                if (!TextUtils.isEmpty(imageUrl)) {
                    ActivityFactory.startImageActivity(getActivity(), imageUrl);
                }
            });
        }
    }

}
