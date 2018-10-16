package org.musicbrainz.android.adapter.recycler;

import android.support.v7.widget.CardView;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import org.musicbrainz.android.R;
import org.musicbrainz.android.api.core.ApiUtils;
import org.musicbrainz.android.api.model.relations.Relation;

/**
 * Created by Alex on 17.01.2018.
 */

public class CreditsAdapter extends BaseRecyclerViewAdapter<CreditsAdapter.ViewHolder> {

    private List<Relation> artistRelations;

    public static class ViewHolder extends BaseRecyclerViewAdapter.BaseViewHolder {

        private TextView typeView;
        private TextView artistView;

        public ViewHolder(CardView v) {
            super(v);
            typeView = v.findViewById(R.id.type);
            artistView = v.findViewById(R.id.artist);
        }

        public void bindView(Relation relation) {
            String type = relation.getType();
            String attributes = ApiUtils.getStringFromList(relation.getAttributes(), ", ");
            typeView.setText(ApiUtils.getStringFromArray(new String[]{type, attributes}, " / "));
            artistView.setText(relation.getArtist().getName());
        }
    }

    public CreditsAdapter(List<Relation> artistRelations) {
        this.artistRelations = artistRelations;
    }

    @Override
    public void onBind(ViewHolder holder, final int position) {
        holder.bindView(artistRelations.get(position));
    }

    @Override
    public int getItemCount() {
        return artistRelations.size();
    }

    @Override
    public CreditsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(inflateCardView(parent, R.layout.card_credits));
    }
}
