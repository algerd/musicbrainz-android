package org.musicbrainz.android.adapter.recycler;

import android.support.v7.widget.CardView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import org.musicbrainz.android.R;
import org.musicbrainz.android.api.model.Event;

/**
 * Created by Alex on 17.01.2018.
 */

public class EventCollectionAdapter extends BaseRecyclerViewAdapter<EventCollectionAdapter.ViewHolder> {

    private List<Event> events;

    public static class ViewHolder extends BaseRecyclerViewAdapter.BaseViewHolder {

        private TextView eventNameTextView;
        private ImageView deleteButton;

        public ViewHolder(CardView v) {
            super(v);
            eventNameTextView = v.findViewById(R.id.event_name);
            deleteButton = v.findViewById(R.id.delete);
        }

        public void bindView(Event event, boolean isPrivate) {
            deleteButton.setVisibility(isPrivate ? View.VISIBLE : View.GONE);
            eventNameTextView.setText(event.getName());
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

    public EventCollectionAdapter(List<Event> events, boolean isPrivate) {
        this.events = events;
        this.isPrivate = isPrivate;
        Collections.sort(this.events, (a1, a2) -> (a1.getName()).compareTo(a2.getName()));
    }

    @Override
    public void onBind(ViewHolder holder, final int position) {
        holder.setOnDeleteListener(onDeleteListener);
        holder.bindView(events.get(position), isPrivate);
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    @Override
    public EventCollectionAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(inflateCardView(parent, R.layout.card_event_collection));
    }

    public interface OnDeleteListener {
        void onDelete(int position);
    }

    private OnDeleteListener onDeleteListener;

    public void setOnDeleteListener(OnDeleteListener onDeleteListener) {
        this.onDeleteListener = onDeleteListener;
    }
}
