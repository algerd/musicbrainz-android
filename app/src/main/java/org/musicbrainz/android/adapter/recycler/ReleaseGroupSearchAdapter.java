package org.musicbrainz.android.adapter.recycler;

import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import org.musicbrainz.android.R;
import org.musicbrainz.android.api.StringMapper;
import org.musicbrainz.android.api.core.ApiUtils;
import org.musicbrainz.android.api.coverart.CoverArtImage;
import org.musicbrainz.android.api.model.Artist;
import org.musicbrainz.android.api.model.ReleaseGroup;

import static org.musicbrainz.android.MusicBrainzApp.api;

public class ReleaseGroupSearchAdapter extends BaseRecyclerViewAdapter<ReleaseGroupSearchAdapter.ViewHolder> {

    private List<ReleaseGroup> releaseGroups;

    public static class ViewHolder extends BaseRecyclerViewAdapter.BaseViewHolder {

        private ImageView coverart;
        private ProgressBar coverartLoading;
        private TextView releaseName;
        private TextView releaseType;
        private TextView artistName;
        private TextView tags;

        public ViewHolder(CardView v) {
            super(v);
            coverart = v.findViewById(R.id.coverart);
            coverartLoading = v.findViewById(R.id.coverart_loading);
            releaseName = v.findViewById(R.id.release_name);
            releaseType = v.findViewById(R.id.release_type);
            artistName = v.findViewById(R.id.artist_name);
            tags = v.findViewById(R.id.tags);
        }

        public void bindView(ReleaseGroup releaseGroup) {
            releaseName.setText(releaseGroup.getTitle());
            List<Artist.ArtistCredit> artists = releaseGroup.getArtistCredit();
            Artist artist = null;
            if (artists != null && !artists.isEmpty()) {
                artist = artists.get(0).getArtist();
                artistName.setText(artist.getName());
                if (releaseGroup.getTags() != null && !releaseGroup.getTags().isEmpty()) {
                    tags.setText(ApiUtils.getStringFromList(releaseGroup.getTags(), ", "));
                } else {
                    tags.setText(artist.getDisambiguation());
                }
            }
            releaseType.setText(StringMapper.mapReleaseGroupOneType(releaseGroup));

            loadImage(releaseGroup.getId());
        }

        private void loadImage(String mbid) {
            coverart.setVisibility(View.INVISIBLE);
            coverartLoading.setVisibility(View.VISIBLE);
            api.getReleaseGroupCoverArt(
                    mbid,
                    coverArt -> {
                        CoverArtImage.Thumbnails thumbnails = coverArt.getFrontThumbnails();
                        if (thumbnails != null && !TextUtils.isEmpty(thumbnails.getSmall())) {
                            Picasso.with(itemView.getContext()).load(thumbnails.getSmall()).fit().into(coverart);
                        }
                        coverart.setVisibility(View.VISIBLE);
                        coverartLoading.setVisibility(View.GONE);
                    },
                    t -> {
                        coverart.setVisibility(View.VISIBLE);
                        coverartLoading.setVisibility(View.GONE);
                    }
            );
        }
    }

    public ReleaseGroupSearchAdapter(List<ReleaseGroup> releaseGroups) {
        this.releaseGroups = releaseGroups;
    }

    @Override
    public void onBind(ViewHolder holder, final int position) {
        holder.bindView(releaseGroups.get(position));
    }

    @Override
    public int getItemCount() {
        return releaseGroups.size();
    }

    @Override
    public ReleaseGroupSearchAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(inflateCardView(parent, R.layout.card_search_release_group));
    }
}
