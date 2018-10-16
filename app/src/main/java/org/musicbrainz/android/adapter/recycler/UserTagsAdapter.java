package org.musicbrainz.android.adapter.recycler;

import android.support.v7.widget.CardView;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import org.musicbrainz.android.R;
import org.musicbrainz.android.api.model.Tag;

/**
 * Created by Alex on 19.02.2018.
 */

public class UserTagsAdapter extends BaseRecyclerViewAdapter<UserTagsAdapter.ViewHolder> {

    private List<Tag> tags;

    public static class ViewHolder extends BaseRecyclerViewAdapter.BaseViewHolder {

        private TextView tagName;
        private TextView tagCount;

        public ViewHolder(CardView v) {
            super(v);
            tagName = v.findViewById(R.id.tag_name);
            tagCount = v.findViewById(R.id.tag_count);
        }

        public void bindView(Tag tag) {
            tagName.setText(tag.getName());
            tagCount.setText(String.valueOf(tag.getCount()));
        }
    }

    public UserTagsAdapter(List<Tag> tags) {
        this.tags = tags;
    }

    @Override
    public void onBind(ViewHolder holder, final int position) {
        holder.bindView(tags.get(position));
    }

    @Override
    public int getItemCount() {
        return tags.size();
    }

    @Override
    public UserTagsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(inflateCardView(parent, R.layout.card_user_tag));
    }

}
