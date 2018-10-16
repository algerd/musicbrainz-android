package org.musicbrainz.android.adapter.recycler;

import android.support.v7.widget.CardView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import org.musicbrainz.android.R;
import org.musicbrainz.android.api.model.Series;

/**
 * Created by Alex on 17.01.2018.
 */

public class SeriesCollectionAdapter extends BaseRecyclerViewAdapter<SeriesCollectionAdapter.ViewHolder> {

    private List<Series> serieses;

    public static class ViewHolder extends BaseRecyclerViewAdapter.BaseViewHolder {

        private TextView seriesNameTextView;
        private ImageView deleteButton;

        public ViewHolder(CardView v) {
            super(v);
            seriesNameTextView = v.findViewById(R.id.series_name);
            deleteButton = v.findViewById(R.id.delete);
        }

        public void bindView(Series series, boolean isPrivate) {
            deleteButton.setVisibility(isPrivate ? View.VISIBLE : View.GONE);
            seriesNameTextView.setText(series.getName());
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

    public SeriesCollectionAdapter(List<Series> serieses, boolean isPrivate) {
        this.serieses = serieses;
        this.isPrivate = isPrivate;
        Collections.sort(this.serieses, (a1, a2) -> (a1.getName()).compareTo(a2.getName()));
    }

    @Override
    public void onBind(ViewHolder holder, final int position) {
        holder.setOnDeleteListener(onDeleteListener);
        holder.bindView(serieses.get(position), isPrivate);
    }

    @Override
    public int getItemCount() {
        return serieses.size();
    }

    @Override
    public SeriesCollectionAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(inflateCardView(parent, R.layout.card_series_collection));
    }

    public interface OnDeleteListener {
        void onDelete(int position);
    }

    private OnDeleteListener onDeleteListener;

    public void setOnDeleteListener(OnDeleteListener onDeleteListener) {
        this.onDeleteListener = onDeleteListener;
    }
}
