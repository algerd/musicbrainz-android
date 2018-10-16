package org.musicbrainz.android.adapter.recycler;

import android.support.v7.widget.CardView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import org.musicbrainz.android.R;
import org.musicbrainz.android.api.model.Work;

/**
 * Created by Alex on 17.01.2018.
 */

public class WorkCollectionAdapter extends BaseRecyclerViewAdapter<WorkCollectionAdapter.ViewHolder> {

    private List<Work> works;

    public static class ViewHolder extends BaseRecyclerViewAdapter.BaseViewHolder {

        private TextView workNameTextView;
        private ImageView deleteButton;

        public ViewHolder(CardView v) {
            super(v);
            workNameTextView = v.findViewById(R.id.work_name);
            deleteButton = v.findViewById(R.id.delete);
        }

        public void bindView(Work work, boolean isPrivate) {
            deleteButton.setVisibility(isPrivate ? View.VISIBLE : View.GONE);
            workNameTextView.setText(work.getTitle());
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

    public WorkCollectionAdapter(List<Work> works, boolean isPrivate) {
        this.works = works;
        this.isPrivate = isPrivate;
        Collections.sort(this.works, (a1, a2) -> (a1.getTitle()).compareTo(a2.getTitle()));
    }

    @Override
    public void onBind(ViewHolder holder, final int position) {
        holder.setOnDeleteListener(onDeleteListener);
        holder.bindView(works.get(position), isPrivate);
    }

    @Override
    public int getItemCount() {
        return works.size();
    }

    @Override
    public WorkCollectionAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(inflateCardView(parent, R.layout.card_work_collection));
    }

    public interface OnDeleteListener {
        void onDelete(int position);
    }

    private OnDeleteListener onDeleteListener;

    public void setOnDeleteListener(OnDeleteListener onDeleteListener) {
        this.onDeleteListener = onDeleteListener;
    }
}
