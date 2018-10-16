package org.musicbrainz.android.adapter.recycler;

import android.support.v7.widget.CardView;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import org.musicbrainz.android.R;
import org.musicbrainz.android.api.core.ApiUtils;
import org.musicbrainz.android.api.model.Artist;
import org.musicbrainz.android.api.model.Recording;
import org.musicbrainz.android.api.model.Release;

public class TrackSearchAdapter extends BaseRecyclerViewAdapter<TrackSearchAdapter.ViewHolder> {

    private List<Recording> recordings;

    public static class ViewHolder extends BaseRecyclerViewAdapter.BaseViewHolder {

        private TextView artistName;
        private TextView albumName;
        private TextView trackName;
        private TextView tags;

        public ViewHolder(CardView v) {
            super(v);
            artistName = v.findViewById(R.id.artist_name);
            albumName = v.findViewById(R.id.album_name);
            trackName = v.findViewById(R.id.track_name);
            tags = v.findViewById(R.id.tags);
        }

        public void bindView(Recording recording) {
            trackName.setText(recording.getTitle());

            List<Artist.ArtistCredit> artists = recording.getArtistCredits();
            Artist artist = null;
            if (artists != null && !artists.isEmpty()) {
                artist = artists.get(0).getArtist();
                artistName.setText(itemView.getResources().getString(R.string.search_track_artist_name, artist.getName()));
                if (recording.getTags() != null && !recording.getTags().isEmpty()) {
                    tags.setText(ApiUtils.getStringFromList(recording.getTags(), ", "));
                } else {
                    tags.setText(artist.getDisambiguation());
                }
            }

            List<Release> releases = recording.getReleases();
            if (releases != null && !releases.isEmpty()) {
                albumName.setText(itemView.getResources().getString(R.string.search_track_album_name, releases.get(0).getTitle()));
            }
        }
    }

    public TrackSearchAdapter(List<Recording> recordings) {
        this.recordings = recordings;
    }

    @Override
    public void onBind(ViewHolder holder, final int position) {
        holder.bindView(recordings.get(position));
    }

    @Override
    public int getItemCount() {
        return recordings.size();
    }

    @Override
    public TrackSearchAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(inflateCardView(parent, R.layout.card_search_track));
    }
}
