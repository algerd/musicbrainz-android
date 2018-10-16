package org.musicbrainz.android.adapter.recycler;

import android.support.v7.widget.CardView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import org.musicbrainz.android.R;
import org.musicbrainz.android.api.model.Collection;

/**
 * Created by Alex on 19.02.2018.
 */

public class CollectionsAdapter extends BaseRecyclerViewAdapter<CollectionsAdapter.ViewHolder> {

    private ViewHolder.OnDeleteCollectionListener onDeleteCollectionListener;
    private List<Collection> collections;
    private boolean isPrivate;

    public static class ViewHolder extends BaseRecyclerViewAdapter.BaseViewHolder {

        public interface OnDeleteCollectionListener {
            void onDelete(int position);
        }

        private OnDeleteCollectionListener onDeleteCollectionListener;

        public void setOnDeleteCollectionListener(OnDeleteCollectionListener onDeleteCollectionListener) {
            this.onDeleteCollectionListener = onDeleteCollectionListener;
        }

        private TextView collectionName;
        private TextView collectionCount;
        private ImageView collectionDelete;

        public ViewHolder(CardView v) {
            super(v);
            collectionName = v.findViewById(R.id.collection_name);
            collectionCount = v.findViewById(R.id.collection_count);
            collectionDelete = v.findViewById(R.id.collection_delete);
        }

        public void bindView(Collection collection, boolean isPrivate) {
            collectionDelete.setVisibility(isPrivate ? View.VISIBLE : View.GONE);

            collectionDelete.setOnClickListener(v -> {
                if (onDeleteCollectionListener != null) {
                    onDeleteCollectionListener.onDelete(getAdapterPosition());
                }
            });

            collectionName.setText(collection.getName());
            collectionCount.setText(String.valueOf(collection.getCount()));
        }

    }

    public CollectionsAdapter(List<Collection> collections, boolean isPrivate) {
        this.collections = collections;
        this.isPrivate = isPrivate;
        //Collections.sort(this.collections, (t1, t2) -> t2.getCount() - t1.getCount());
    }

    @Override
    public void onBind(ViewHolder holder, final int position) {
        holder.setOnDeleteCollectionListener(onDeleteCollectionListener);
        holder.bindView(collections.get(position), isPrivate);
    }

    @Override
    public int getItemCount() {
        return collections.size();
    }

    @Override
    public CollectionsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(inflateCardView(parent, R.layout.card_collections));
    }

    public void setOnDeleteCollectionListener(ViewHolder.OnDeleteCollectionListener onDeleteCollectionListener) {
        this.onDeleteCollectionListener = onDeleteCollectionListener;
    }

}
