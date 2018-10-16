package org.musicbrainz.android.adapter.recycler;

import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.Objects;

import org.musicbrainz.android.R;
import org.musicbrainz.android.api.StringMapper;
import org.musicbrainz.android.api.coverart.CoverArtImage;
import org.musicbrainz.android.api.model.Rating;
import org.musicbrainz.android.api.model.ReleaseGroup;
import org.musicbrainz.android.intent.ActivityFactory;
import org.musicbrainz.android.util.PicassoHelper;
import org.musicbrainz.android.util.ShowUtil;

import static org.musicbrainz.android.MusicBrainzApp.api;
import static org.musicbrainz.android.MusicBrainzApp.oauth;
import static org.musicbrainz.android.api.coverart.CoverArtImage.Thumbnails.SMALL_SIZE;

/**
 * Created by Alex on 17.01.2018.
 */

public class ReleaseGroupsAdapter extends BasePagedListAdapter<ReleaseGroup> {

    public static class ReleaseGroupsViewHolder extends RecyclerView.ViewHolder {

        static final int VIEW_HOLDER_LAYOUT = R.layout.card_release_group;

        private ImageView imageView;
        private ProgressBar progressLoading;
        private RatingBar userRatingBar;
        private TextView ratingView;
        private TextView releaseNameView;
        private TextView releaseTypeYearView;
        private LinearLayout ratingContainer;

        private ReleaseGroupsViewHolder(View v) {
            super(v);
            imageView = itemView.findViewById(R.id.img);
            progressLoading = itemView.findViewById(R.id.loading);
            releaseNameView = itemView.findViewById(R.id.name);
            releaseTypeYearView = itemView.findViewById(R.id.type_year);

            ratingContainer = itemView.findViewById(R.id.rating_container);
            userRatingBar = itemView.findViewById(R.id.user_ratingbar);
            ratingView = itemView.findViewById(R.id.rating);
        }

        public static ReleaseGroupsViewHolder create(ViewGroup parent) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View view = layoutInflater.inflate(VIEW_HOLDER_LAYOUT, parent, false);
            return new ReleaseGroupsViewHolder(view);
        }

        private void bindTo(ReleaseGroup releaseGroup) {
            releaseNameView.setText(releaseGroup.getTitle());

            setAllRating(releaseGroup);
            setUserRating(releaseGroup);

            String year = releaseGroup.getFirstReleaseDate();
            year = !TextUtils.isEmpty(year) ? year.substring(0, 4) : "";

            String type = StringMapper.mapReleaseGroupTypeString(releaseGroup);
            releaseTypeYearView.setText(year + " (" + type + ")");

            loadImage(releaseGroup.getId());
            ratingContainer.setOnClickListener(v -> showRatingBar(releaseGroup));
        }

        private void showRatingBar(ReleaseGroup releaseGroup) {
            if (oauth.hasAccount()) {
                AlertDialog alertDialog = new AlertDialog.Builder(itemView.getContext()).create();
                alertDialog.show();
                Window win = alertDialog.getWindow();
                if (win != null) {
                    win.setContentView(R.layout.dialog_rating_bar);
                    RatingBar rb = win.findViewById(R.id.rating_bar);
                    rb.setRating(userRatingBar.getRating());
                    TextView title = win.findViewById(R.id.title_text);
                    title.setText(itemView.getResources().getString(R.string.rate_entity, releaseGroup.getTitle()));

                    rb.setOnRatingBarChangeListener((RatingBar ratingBar, float rating, boolean fromUser) -> {
                        if (oauth.hasAccount()) {
                            if (fromUser) {
                                api.postAlbumRating(
                                        releaseGroup.getId(), rating,
                                        metadata -> {
                                            if (metadata.getMessage().getText().equals("OK")) {
                                                userRatingBar.setRating(rating);
                                                api.getAlbumRatings(
                                                        releaseGroup.getId(),
                                                        this::setAllRating,
                                                        t -> ShowUtil.showToast(itemView.getContext(), t.getMessage()));
                                            } else {
                                                ShowUtil.showToast(itemView.getContext(), "Error");
                                            }
                                            alertDialog.dismiss();
                                        },
                                        t -> {
                                            ShowUtil.showToast(itemView.getContext(), t.getMessage());
                                            alertDialog.dismiss();
                                        });
                            }
                        } else {
                            ActivityFactory.startLoginActivity(itemView.getContext());
                        }
                    });
                }
            } else {
                ActivityFactory.startLoginActivity(itemView.getContext());
            }
        }

        private void loadImage(String albumMbid) {
            imageView.setVisibility(View.INVISIBLE);
            progressLoading.setVisibility(View.VISIBLE);
            api.getReleaseGroupCoverArt(
                    albumMbid,
                    coverArt -> {
                        CoverArtImage.Thumbnails thumbnails = coverArt.getFrontThumbnails();
                        if (thumbnails != null && !TextUtils.isEmpty(thumbnails.getSmall())) {
                            progressLoading.setVisibility(View.VISIBLE);
                            Picasso.with(itemView.getContext()).load(thumbnails.getSmall())
                                    .resize(SMALL_SIZE, SMALL_SIZE)
                                    .into(imageView, PicassoHelper.createPicassoProgressCallback(progressLoading));
                        }
                        imageView.setVisibility(View.VISIBLE);
                    },
                    t -> {
                        imageView.setVisibility(View.VISIBLE);
                        progressLoading.setVisibility(View.GONE);
                    });
        }

        private void setAllRating(ReleaseGroup releaseGroup) {
            Rating rating = releaseGroup.getRating();
            if (rating != null) {
                Float r = rating.getValue();
                if (r != null) {
                    ratingView.setText(itemView.getContext().getString(R.string.rating_text, r, rating.getVotesCount()));
                } else {
                    ratingView.setText(itemView.getContext().getString(R.string.rating_text, 0.0, 0));
                }
            }
        }

        private void setUserRating(ReleaseGroup releaseGroup) {
            Rating rating = releaseGroup.getUserRating();
            if (rating != null) {
                Float r = rating.getValue();
                if (r == null) r = 0f;
                userRatingBar.setRating(r);
            }
        }

    }

    public ReleaseGroupsAdapter(RetryCallback retryCallback) {
        super(DIFF_CALLBACK, retryCallback);
    }

    @Override
    public int getItemViewType(int position) {
        if (hasExtraRow() && position == getItemCount() - 1) {
            return NetworkStateViewHolder.VIEW_HOLDER_LAYOUT;
        } else {
            return ReleaseGroupsViewHolder.VIEW_HOLDER_LAYOUT;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case ReleaseGroupsViewHolder.VIEW_HOLDER_LAYOUT:
                return ReleaseGroupsViewHolder.create(parent);
            case NetworkStateViewHolder.VIEW_HOLDER_LAYOUT:
                return NetworkStateViewHolder.create(parent, retryCallback);
            default:
                throw new IllegalArgumentException("unknown view type");
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case ReleaseGroupsViewHolder.VIEW_HOLDER_LAYOUT:
                ReleaseGroup releaseGroup = getItem(position);
                ((ReleaseGroupsViewHolder) holder).bindTo(releaseGroup);
                if (holderClickListener != null) {
                    holder.itemView.setOnClickListener(view -> holderClickListener.onClick(releaseGroup));
                }
                break;
            case NetworkStateViewHolder.VIEW_HOLDER_LAYOUT:
                ((NetworkStateViewHolder) holder).bindTo(networkState);
                break;
        }
    }

    private static DiffUtil.ItemCallback<ReleaseGroup> DIFF_CALLBACK = new DiffUtil.ItemCallback<ReleaseGroup>() {
        @Override
        public boolean areItemsTheSame(@NonNull ReleaseGroup oldItem, @NonNull ReleaseGroup newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull ReleaseGroup oldItem, @NonNull ReleaseGroup newItem) {
            return Objects.equals(oldItem, newItem);
        }
    };

    public interface HolderClickListener {
        void onClick(ReleaseGroup releaseGroup);
    }

    private HolderClickListener holderClickListener;

    public void setHolderClickListener(HolderClickListener holderClickListener) {
        this.holderClickListener = holderClickListener;
    }

}
