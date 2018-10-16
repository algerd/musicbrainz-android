package org.musicbrainz.android.adapter.recycler;

import android.support.v7.widget.CardView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import org.musicbrainz.android.R;
import org.musicbrainz.android.api.model.Place;

/**
 * Created by Alex on 17.01.2018.
 */

public class PlaceCollectionAdapter extends BaseRecyclerViewAdapter<PlaceCollectionAdapter.ViewHolder> {

    private List<Place> places;

    public static class ViewHolder extends BaseRecyclerViewAdapter.BaseViewHolder {

        private TextView placeNameTextView;
        private ImageView deleteButton;

        public ViewHolder(CardView v) {
            super(v);
            placeNameTextView = v.findViewById(R.id.place_name);
            deleteButton = v.findViewById(R.id.delete);
        }

        public void bindView(Place place, boolean isPrivate) {
            deleteButton.setVisibility(isPrivate ? View.VISIBLE : View.GONE);
            placeNameTextView.setText(place.getName());
        }

        public void setOnDeleteListener(OnDeleteListener listener) {
            deleteButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDelete(getAdapterPosition());
                }
            });
        }
    }

    private boolean isPrivate;

    public PlaceCollectionAdapter(List<Place> places, boolean isPrivate) {
        this.places = places;
        this.isPrivate = isPrivate;
        Collections.sort(this.places, (a1, a2) -> (a1.getName()).compareTo(a2.getName()));
    }

    @Override
    public void onBind(ViewHolder holder, final int position) {
        holder.setOnDeleteListener(onDeleteListener);
        holder.bindView(places.get(position), isPrivate);
    }

    @Override
    public int getItemCount() {
        return places.size();
    }

    @Override
    public PlaceCollectionAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(inflateCardView(parent, R.layout.card_place_collection));
    }

    public interface OnDeleteListener {
        void onDelete(int position);
    }

    private OnDeleteListener onDeleteListener;

    public void setOnDeleteListener(OnDeleteListener onDeleteListener) {
        this.onDeleteListener = onDeleteListener;
    }
}
