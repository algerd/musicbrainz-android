package org.musicbrainz.android.adapter.recycler;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import org.musicbrainz.android.R;
import org.musicbrainz.android.api.site.TagEntity;

/**
 * Created by Alex on 17.01.2018.
 */

public class ArtistTagAdapter extends BaseRecyclerViewAdapter<ArtistTagAdapter.ViewHolder> {

    private List<TagEntity> tags;

    public static class ViewHolder extends BaseRecyclerViewAdapter.BaseViewHolder {

        private TextView artistNameView;
        private TextView commentView;

        public ViewHolder(View v) {
            super(v);
            artistNameView = v.findViewById(R.id.artist_name);
            commentView = v.findViewById(R.id.comment);
        }

        public void bindView(TagEntity tag) {
            artistNameView.setText(tag.getName());
            commentView.setText(tag.getArtistComment());
        }
    }

    public ArtistTagAdapter(List<TagEntity> tags) {
        this.tags = tags;
    }

    @Override
    public void onBind(ViewHolder holder, final int position) {
        holder.bindView(tags.get(position));
    }

    @Override
    public int getItemCount() {
        return tags.size();
    }

    @Override
    public ArtistTagAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(inflateCardView(parent, R.layout.card_artist_tag));
    }
}
