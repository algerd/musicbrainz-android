package org.musicbrainz.android.adapter.recycler;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.musicbrainz.android.R;
import org.musicbrainz.android.api.site.TagEntity;

import java.util.List;

/**
 * Created by Alex on 17.01.2018.
 */

public class ArtistTagAdapter extends BaseRecyclerViewAdapter<ArtistTagAdapter.ArtistTagViewHolder> {

    private List<TagEntity> tags;

    public static class ArtistTagViewHolder extends BaseRecyclerViewAdapter.BaseViewHolder {

        static final int VIEW_HOLDER_LAYOUT = R.layout.card_artist_tag;

        private TextView artistNameView;
        private TextView commentView;

        public static ArtistTagViewHolder create(ViewGroup parent) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View view = layoutInflater.inflate(VIEW_HOLDER_LAYOUT, parent, false);
            return new ArtistTagViewHolder(view);
        }

        private ArtistTagViewHolder(View v) {
            super(v);
            artistNameView = v.findViewById(R.id.artist_name);
            commentView = v.findViewById(R.id.comment);
        }

        public void bindTo(TagEntity tag) {
            artistNameView.setText(tag.getName());
            commentView.setText(tag.getArtistComment());
        }
    }

    public ArtistTagAdapter(List<TagEntity> tags) {
        this.tags = tags;
    }

    @Override
    public void onBind(ArtistTagViewHolder holder, final int position) {
        holder.bindTo(tags.get(position));
    }

    @Override
    public int getItemCount() {
        return tags.size();
    }

    @NonNull
    @Override
    public ArtistTagViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return ArtistTagViewHolder.create(parent);
    }
}
