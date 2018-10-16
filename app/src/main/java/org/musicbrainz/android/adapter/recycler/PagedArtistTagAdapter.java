package org.musicbrainz.android.adapter.recycler;

import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Objects;

import org.musicbrainz.android.R;
import org.musicbrainz.android.api.site.TagEntity;

/**
 * Created by Alex on 17.01.2018.
 */

public class PagedArtistTagAdapter extends BasePagedListAdapter<TagEntity> {

    public static class ViewHolder extends RecyclerView.ViewHolder {

        static final int VIEW_HOLDER_LAYOUT = R.layout.card_artist_tag;

        private TextView artistNameView;
        private TextView commentView;

        public ViewHolder(View v) {
            super(v);
            artistNameView = v.findViewById(R.id.artist_name);
            commentView = v.findViewById(R.id.comment);
        }

        public static ViewHolder create(ViewGroup parent) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View view = layoutInflater.inflate(VIEW_HOLDER_LAYOUT, parent, false);
            return new ViewHolder(view);
        }

        private void bindTo(TagEntity tagEntity) {
            artistNameView.setText(tagEntity.getName());
            commentView.setText(tagEntity.getArtistComment());
        }
    }

    public PagedArtistTagAdapter(RetryCallback retryCallback) {
        super(DIFF_CALLBACK, retryCallback);
    }

    @Override
    public int getItemViewType(int position) {
        if (hasExtraRow() && position == getItemCount() - 1) {
            return NetworkStateViewHolder.VIEW_HOLDER_LAYOUT;
        } else {
            return ViewHolder.VIEW_HOLDER_LAYOUT;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case ViewHolder.VIEW_HOLDER_LAYOUT:
                return ViewHolder.create(parent);
            case NetworkStateViewHolder.VIEW_HOLDER_LAYOUT:
                return NetworkStateViewHolder.create(parent, retryCallback);
            default:
                throw new IllegalArgumentException("unknown view type");
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case ViewHolder.VIEW_HOLDER_LAYOUT:
                TagEntity tagEntity = getItem(position);
                ((ViewHolder) holder).bindTo(tagEntity);
                if (holderClickListener != null) {
                    holder.itemView.setOnClickListener(view -> holderClickListener.onClick(tagEntity));
                }
                break;
            case NetworkStateViewHolder.VIEW_HOLDER_LAYOUT:
                ((NetworkStateViewHolder) holder).bindTo(networkState);
                break;
        }
    }

    private static DiffUtil.ItemCallback<TagEntity> DIFF_CALLBACK = new DiffUtil.ItemCallback<TagEntity>() {
        @Override
        public boolean areItemsTheSame(@NonNull TagEntity oldItem, @NonNull TagEntity newItem) {
            return oldItem.getMbid().equals(newItem.getMbid());
        }

        @Override
        public boolean areContentsTheSame(@NonNull TagEntity oldItem, @NonNull TagEntity newItem) {
            return Objects.equals(oldItem, newItem);
        }
    };

    public interface HolderClickListener {
        void onClick(TagEntity tagEntity);
    }

    private HolderClickListener holderClickListener;

    public void setHolderClickListener(HolderClickListener holderClickListener) {
        this.holderClickListener = holderClickListener;
    }
}
