package org.musicbrainz.android.adapter.recycler;

import android.support.v7.widget.CardView;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import org.musicbrainz.android.R;
import org.musicbrainz.android.api.model.Collection;

/**
 * Created by Alex on 22.03.2018.
 */

public class DialogCollectionsAdapter extends BaseRecyclerViewAdapter<DialogCollectionsAdapter.ViewHolder> {

    private List<Collection> collections;

    public static class ViewHolder extends BaseRecyclerViewAdapter.BaseViewHolder {

        private TextView collectionName;
        private TextView collectionCount;

        public ViewHolder(CardView v) {
            super(v);
            collectionName = v.findViewById(R.id.collection_name);
            collectionCount = v.findViewById(R.id.collection_count);
        }

        public void bindView(Collection collection) {
            collectionName.setText(collection.getName());
            collectionCount.setText(String.valueOf(collection.getCount()));
        }
    }

    public DialogCollectionsAdapter(List<Collection> collections) {
        this.collections = collections;
        Collections.sort(this.collections, (c1, c2) -> (c1.getName()).compareTo(c2.getName()));
    }

    @Override
    public void onBind(ViewHolder holder, final int position) {
        holder.bindView(collections.get(position));
    }

    @Override
    public int getItemCount() {
        return collections.size();
    }

    @Override
    public DialogCollectionsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(inflateCardView(parent, R.layout.card_dialog_collections));
    }

}
