package org.musicbrainz.android.adapter.recycler;

import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.musicbrainz.android.R;
import org.musicbrainz.android.api.coverart.CoverArtImage;
import org.musicbrainz.android.api.model.Label;
import org.musicbrainz.android.api.model.Media;
import org.musicbrainz.android.api.model.Release;
import org.musicbrainz.android.util.MbUtils;
import org.musicbrainz.android.util.StringFormat;

import static org.musicbrainz.android.MusicBrainzApp.api;

/**
 * Created by Alex on 19.02.2018.
 */

public class ReleaseAdapter extends BaseRecyclerViewAdapter<ReleaseAdapter.ViewHolder> {

    private String releaseMbid;
    private List<Release> releases;

    public static class ViewHolder extends BaseRecyclerViewAdapter.BaseViewHolder {

        private CardView cardView;
        private ImageView coverart;
        private ProgressBar coverartLoading;
        private TextView date;
        private TextView releaseName;
        private TextView countryLabel;
        private TextView format;
        private TextView catalog;
        private TextView barcode;

        public ViewHolder(CardView v) {
            super(v);
            cardView = v.findViewById(R.id.release_card);
            coverart = v.findViewById(R.id.coverart);
            coverartLoading = v.findViewById(R.id.coverart_loading);
            date = v.findViewById(R.id.date);
            releaseName = v.findViewById(R.id.release_name);
            countryLabel = v.findViewById(R.id.country_label);
            format = v.findViewById(R.id.format);
            catalog = v.findViewById(R.id.catalog);
            barcode = v.findViewById(R.id.barcode);
        }

        public void bindView(Release release, String releaseMbid) {
            if (release.getId().equals(releaseMbid)) {
                cardView.setBackgroundResource(R.color.md_orange_50);
            }

            date.setText(release.getDate());
            releaseName.setText(release.getTitle());
            if (!TextUtils.isEmpty(release.getBarcode())) {
                //TODO: send text to resource
                barcode.setText(itemView.getResources().getString(R.string.r_barcode, release.getBarcode()));
            }

            List<Label.LabelInfo> labelInfos = release.getLabelInfo();
            String labelName = "";
            if (labelInfos != null && !labelInfos.isEmpty()) {
                Label label = labelInfos.get(0).getLabel();
                if (label != null) {
                    labelName = label.getName();
                }
                String labelCatalog = labelInfos.get(0).getCatalogNumber();
                if (!TextUtils.isEmpty(labelCatalog)) {
                    catalog.setText(itemView.getResources().getString(R.string.r_catalog, labelCatalog));
                }
            }
            countryLabel.setText(release.getCountry() + " " + labelName);

            int trackCount = 0;
            List<Media> medias = release.getMedia();
            for (Media media : medias) {
                trackCount += media.getTrackCount();
            }
            String f = StringFormat.buildReleaseFormatsString(itemView.getContext(), medias);
            format.setText(itemView.getResources().getString(R.string.r_tracks, f, trackCount));

            if (release.getCoverArt() != null &&
                    release.getCoverArt().getFront() != null && release.getCoverArt().getFront()) {

                coverart.setVisibility(View.INVISIBLE);
                coverartLoading.setVisibility(View.VISIBLE);
                api.getReleaseCoverArt(
                        release.getId(),
                        coverArt -> {
                            coverart.setVisibility(View.VISIBLE);
                            coverartLoading.setVisibility(View.GONE);
                            CoverArtImage.Thumbnails thumbnails = coverArt.getFrontThumbnails();
                            if (thumbnails != null && !TextUtils.isEmpty(thumbnails.getSmall())) {
                                Picasso.with(itemView.getContext()).load(thumbnails.getSmall()).fit().into(coverart);
                            }
                        },
                        t -> {
                            coverart.setVisibility(View.VISIBLE);
                            coverartLoading.setVisibility(View.GONE);
                        }
                );
            }
        }
    }

    public ReleaseAdapter(List<Release> releases, String releaseMbid) {
        this.releases = releases;
        this.releaseMbid = releaseMbid;
        Comparator<Release> sortDate = (r1, r2) -> MbUtils.getNumberDate(r1.getDate()) - MbUtils.getNumberDate(r2.getDate());
        Collections.sort(this.releases, sortDate);
    }

    @Override
    public void onBind(ViewHolder holder, final int position) {
        holder.bindView(releases.get(position), releaseMbid);
    }

    @Override
    public int getItemCount() {
        return releases.size();
    }

    @Override
    public ReleaseAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(inflateCardView(parent, R.layout.card_release));
    }
}
