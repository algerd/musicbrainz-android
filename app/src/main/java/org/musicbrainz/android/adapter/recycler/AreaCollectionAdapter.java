package org.musicbrainz.android.adapter.recycler;

import android.support.v7.widget.CardView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import org.musicbrainz.android.R;
import org.musicbrainz.android.api.model.Area;

/**
 * Created by Alex on 17.01.2018.
 */

public class AreaCollectionAdapter extends BaseRecyclerViewAdapter<AreaCollectionAdapter.ViewHolder> {

    private List<Area> areas;

    public static class ViewHolder extends BaseRecyclerViewAdapter.BaseViewHolder {

        private TextView areaNameTextView;
        private ImageView deleteButton;

        public ViewHolder(CardView v) {
            super(v);
            areaNameTextView = v.findViewById(R.id.area_name);
            deleteButton = v.findViewById(R.id.delete);
        }

        public void bindView(Area area, boolean isPrivate) {
            deleteButton.setVisibility(isPrivate ? View.VISIBLE : View.GONE);
            areaNameTextView.setText(area.getName());
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

    public AreaCollectionAdapter(List<Area> areas, boolean isPrivate) {
        this.areas = areas;
        this.isPrivate = isPrivate;
        Collections.sort(this.areas, (a1, a2) -> (a1.getName()).compareTo(a2.getName()));
    }

    @Override
    public void onBind(ViewHolder holder, final int position) {
        holder.setOnDeleteListener(onDeleteListener);
        holder.bindView(areas.get(position), isPrivate);
    }

    @Override
    public int getItemCount() {
        return areas.size();
    }

    @Override
    public AreaCollectionAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(inflateCardView(parent, R.layout.card_area_collection));
    }

    public interface OnDeleteListener {
        void onDelete(int position);
    }

    private OnDeleteListener onDeleteListener;

    public void setOnDeleteListener(OnDeleteListener onDeleteListener) {
        this.onDeleteListener = onDeleteListener;
    }
}
