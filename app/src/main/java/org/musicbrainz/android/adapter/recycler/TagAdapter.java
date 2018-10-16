package org.musicbrainz.android.adapter.recycler;

import android.content.res.ColorStateList;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import org.musicbrainz.android.R;
import org.musicbrainz.android.api.model.Tag;

/**
 * Created by Alex on 19.02.2018.
 */

public class TagAdapter extends BaseRecyclerViewAdapter<TagAdapter.ViewHolder> {

    private List<Tag> tags;
    private List<Tag> userTags;

    public static class ViewHolder extends BaseRecyclerViewAdapter.BaseViewHolder {

        private TextView tagName;
        private TextView votesCount;
        private ImageView voteBtn;

        public ViewHolder(CardView v) {
            super(v);
            tagName = v.findViewById(R.id.tag_name);
            votesCount = v.findViewById(R.id.votes_count);
            voteBtn = v.findViewById(R.id.vote_btn);
        }

        public void bindView(Tag tag, boolean votted) {
            tagName.setText(tag.getName());
            votesCount.setText(String.valueOf(tag.getCount()));
            if (votted) {
                voteBtn.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(itemView.getContext(), R.color.colorAccent)));
            }
        }

        public void setOnVoteTagListener(OnVoteTagListener listener) {
            voteBtn.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onVote(getAdapterPosition());
                }
            });
        }
    }

    public TagAdapter(List<Tag> tags, List<Tag> userTags) {
        this.tags = tags;
        this.userTags = userTags;
        Collections.sort(this.tags, (t1, t2) -> t2.getCount() - t1.getCount());
    }

    @Override
    public void onBind(ViewHolder holder, final int position) {
        holder.setOnVoteTagListener(onVoteTagListener);
        boolean votted = false;
        if (userTags != null && !userTags.isEmpty()) {
            for (Tag userTag : userTags) {
                if (userTag.getName().equalsIgnoreCase(tags.get(position).getName())) {
                    votted = true;
                    break;
                }
            }
        }
        holder.bindView(tags.get(position), votted);
    }

    @Override
    public int getItemCount() {
        return tags.size();
    }

    @Override
    public TagAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(inflateCardView(parent, R.layout.card_tag));
    }

    public interface OnVoteTagListener {
        void onVote(int position);
    }

    private OnVoteTagListener onVoteTagListener;

    public void setOnVoteTagListener(OnVoteTagListener onVoteTagListener) {
        this.onVoteTagListener = onVoteTagListener;
    }
}
