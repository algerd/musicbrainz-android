package org.musicbrainz.android.adapter.recycler;

import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.List;

import org.musicbrainz.android.R;
import org.musicbrainz.android.api.coverart.CoverArtImage;
import org.musicbrainz.android.api.model.Release;

import static org.musicbrainz.android.MusicBrainzApp.api;

/**
 * Created by Alex on 17.01.2018.
 */

public class ReleaseCollectionAdapter extends BaseRecyclerViewAdapter<ReleaseCollectionAdapter.ViewHolder> {

    private List<Release> releases;

    public static class ViewHolder extends BaseRecyclerViewAdapter.BaseViewHolder {

        private Release release;

        private ImageView coverart;
        private ProgressBar progressLoading;
        private TextView releaseName;
        private ImageView deleteBtn;

        public ViewHolder(CardView v) {
            super(v);
            coverart = v.findViewById(R.id.release_image);
            progressLoading = v.findViewById(R.id.image_loading);
            releaseName = v.findViewById(R.id.release_name);
            deleteBtn = v.findViewById(R.id.delete);
        }

        public void bindView(Release release, boolean isPrivate) {
            deleteBtn.setVisibility(isPrivate ? View.VISIBLE : View.GONE);
            this.release = release;
            releaseName.setText(release.getTitle());
            loadReleaseImage();
        }

        private void loadReleaseImage() {
            if (release.getCoverArt() != null &&
                    release.getCoverArt().getFront() != null && release.getCoverArt().getFront()) {

                coverart.setVisibility(View.INVISIBLE);
                progressLoading.setVisibility(View.VISIBLE);
                api.getReleaseCoverArt(
                        release.getId(),
                        coverArt -> {
                            CoverArtImage.Thumbnails thumbnails = coverArt.getFrontThumbnails();
                            if (thumbnails != null && !TextUtils.isEmpty(thumbnails.getSmall())) {
                                Picasso.with(itemView.getContext()).load(thumbnails.getSmall()).fit().into(coverart);
                            }
                            coverart.setVisibility(View.VISIBLE);
                            progressLoading.setVisibility(View.GONE);
                        },
                        t -> {
                            coverart.setVisibility(View.VISIBLE);
                            progressLoading.setVisibility(View.GONE);
                        }
                );
            } else {
                coverart.setVisibility(View.VISIBLE);
            }
        }

        public void setOnDeleteListener(OnDeleteListener listener) {
            deleteBtn.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDelete(getAdapterPosition());
                }
            });
        }

    }

    private boolean isPrivate;

    public ReleaseCollectionAdapter(List<Release> releases, boolean isPrivate) {
        this.releases = releases;
        this.isPrivate = isPrivate;
        Collections.sort(this.releases, (a1, a2) -> (a1.getTitle()).compareTo(a2.getTitle()));
    }

    @Override
    public void onBind(ViewHolder holder, final int position) {
        holder.setOnDeleteListener(onDeleteListener);
        holder.bindView(releases.get(position), isPrivate);
    }

    @Override
    public int getItemCount() {
        return releases.size();
    }

    @Override
    public ReleaseCollectionAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(inflateCardView(parent, R.layout.card_release_collection));
    }

    public interface OnDeleteListener {
        void onDelete(int position);
    }

    private OnDeleteListener onDeleteListener;

    public void setOnDeleteListener(OnDeleteListener onDeleteListener) {
        this.onDeleteListener = onDeleteListener;
    }
}
