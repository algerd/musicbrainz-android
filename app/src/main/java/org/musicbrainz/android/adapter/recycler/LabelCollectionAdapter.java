package org.musicbrainz.android.adapter.recycler;

import android.support.v7.widget.CardView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import org.musicbrainz.android.R;
import org.musicbrainz.android.api.model.Label;

/**
 * Created by Alex on 17.01.2018.
 */

public class LabelCollectionAdapter extends BaseRecyclerViewAdapter<LabelCollectionAdapter.ViewHolder> {

    private List<Label> labels;

    public static class ViewHolder extends BaseRecyclerViewAdapter.BaseViewHolder {

        private TextView labelNameTextView;
        private ImageView deleteButton;

        public ViewHolder(CardView v) {
            super(v);
            labelNameTextView = v.findViewById(R.id.label_name);
            deleteButton = v.findViewById(R.id.delete);
        }

        public void bindView(Label label, boolean isPrivate) {
            deleteButton.setVisibility(isPrivate ? View.VISIBLE : View.GONE);
            labelNameTextView.setText(label.getName());
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

    public LabelCollectionAdapter(List<Label> labels, boolean isPrivate) {
        this.labels = labels;
        this.isPrivate = isPrivate;
        Collections.sort(this.labels, (a1, a2) -> (a1.getName()).compareTo(a2.getName()));
    }

    @Override
    public void onBind(ViewHolder holder, final int position) {
        holder.setOnDeleteListener(onDeleteListener);
        holder.bindView(labels.get(position), isPrivate);
    }

    @Override
    public int getItemCount() {
        return labels.size();
    }

    @Override
    public LabelCollectionAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(inflateCardView(parent, R.layout.card_label_collection));
    }

    public interface OnDeleteListener {
        void onDelete(int position);
    }

    private OnDeleteListener onDeleteListener;

    public void setOnDeleteListener(OnDeleteListener onDeleteListener) {
        this.onDeleteListener = onDeleteListener;
    }
}
