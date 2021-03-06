package org.musicbrainz.android.adapter.recycler.artistRelations;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import org.musicbrainz.android.R;
import org.musicbrainz.android.adapter.recycler.expandedRecycler.BaseItemViewHolder;
import org.musicbrainz.android.api.core.ApiUtils;
import org.musicbrainz.android.api.model.Artist;
import org.musicbrainz.android.api.model.relations.Relation;

public class ItemViewHolder extends BaseItemViewHolder {

    public interface OnItemClickListener {
        void onClick(Artist artist);
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    private TextView artistView;
    private TextView typeView;
    private TextView beginView;
    private TextView endView;

    public ItemViewHolder(View itemView, boolean visible) {
        super(itemView, visible);
        artistView = itemView.findViewById(R.id.artist);
        typeView = itemView.findViewById(R.id.type);
        beginView = itemView.findViewById(R.id.begin);
        endView = itemView.findViewById(R.id.end);
    }

    public void bindView(Relation relation) {
        Artist artist = relation.getArtist();
        if (artist != null) {
            itemView.setOnClickListener(v -> {
                if (onItemClickListener != null && relation.getArtist() != null) {
                    onItemClickListener.onClick(relation.getArtist());
                }
            });
            artistView.setText(artist.getName());
            String attributes = ApiUtils.getStringFromList(relation.getAttributes(), ", ");
            typeView.setText(attributes);

            String begin = TextUtils.isEmpty(relation.getBegin()) ? "..." : relation.getBegin();
            String end = TextUtils.isEmpty(relation.getEnd()) ? "..." : relation.getEnd();
            if (begin.equals("...") && end.equals("...")) {
                begin = "";
                end = "";
            }
            beginView.setText(begin);
            endView.setText(end);

        } else {
            artistView.setText(R.string.unknown);
        }
    }

}
