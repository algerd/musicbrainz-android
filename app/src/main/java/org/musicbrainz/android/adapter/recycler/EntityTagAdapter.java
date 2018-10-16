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

public class EntityTagAdapter extends BaseRecyclerViewAdapter<EntityTagAdapter.ViewHolder> {

    private List<TagEntity> tags;

    public static class ViewHolder extends BaseRecyclerViewAdapter.BaseViewHolder {

        private TextView entityNameView;
        private TextView artistNameView;

        public ViewHolder(View v) {
            super(v);
            entityNameView = v.findViewById(R.id.entity_name);
            artistNameView = v.findViewById(R.id.artist_name);
        }

        public void bindView(TagEntity tag) {
            entityNameView.setText(tag.getName());
            artistNameView.setText(tag.getArtistName());
        }
    }

    public EntityTagAdapter(List<TagEntity> tags) {
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
    public EntityTagAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(inflateCardView(parent, R.layout.card_entity_tag));
    }
}
