package org.musicbrainz.android.adapter.recycler;

import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
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

import org.musicbrainz.android.R;
import org.musicbrainz.android.api.lastfm.model.Image;
import org.musicbrainz.android.api.model.Artist;
import org.musicbrainz.android.api.model.Rating;
import org.musicbrainz.android.intent.ActivityFactory;
import org.musicbrainz.android.util.ShowUtil;

import java.util.Collections;
import java.util.List;

import static org.musicbrainz.android.MusicBrainzApp.api;
import static org.musicbrainz.android.MusicBrainzApp.oauth;

/**
 * Created by Alex on 17.01.2018.
 */

public class ArtistCollectionAdapter extends BaseRecyclerViewAdapter<ArtistCollectionAdapter.ArtistCollectionViewHolder> {

    private List<Artist> artists;

    public static class ArtistCollectionViewHolder extends BaseRecyclerViewAdapter.BaseViewHolder {

        static final int VIEW_HOLDER_LAYOUT = R.layout.card_artist_collection;

        private ImageView image;
        private ProgressBar progressLoading;
        private TextView artistName;
        private RatingBar userRating;
        private TextView allRatingView;
        private ImageView deleteBtn;
        private LinearLayout ratingContainer;

        public static ArtistCollectionViewHolder create(ViewGroup parent) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View view = layoutInflater.inflate(VIEW_HOLDER_LAYOUT, parent, false);
            return new ArtistCollectionViewHolder(view);
        }

        private ArtistCollectionViewHolder(View v) {
            super(v);
            image = v.findViewById(R.id.artist_image);
            progressLoading = v.findViewById(R.id.image_loading);
            artistName = v.findViewById(R.id.artist_name);
            userRating = v.findViewById(R.id.user_rating);
            allRatingView = v.findViewById(R.id.all_rating);
            deleteBtn = v.findViewById(R.id.delete);
            ratingContainer = v.findViewById(R.id.rating_container);
        }

        private void bindTo(Artist artist, boolean isPrivate) {
            deleteBtn.setVisibility(isPrivate ? View.VISIBLE : View.GONE);

            artistName.setText(artist.getName());
            setUserRating(artist);
            setAllRating(artist);
            loadArtistImageFromLastfm(artist.getName());

            ratingContainer.setOnClickListener(v -> showRatingBar(artist));
        }

        private void showRatingBar(Artist artist) {
            if (oauth.hasAccount()) {
                AlertDialog alertDialog = new AlertDialog.Builder(itemView.getContext()).create();
                alertDialog.show();
                Window win = alertDialog.getWindow();
                if (win != null) {
                    win.setContentView(R.layout.dialog_rating_bar);
                    RatingBar rb = win.findViewById(R.id.rating_bar);
                    View ratingProgress = win.findViewById(R.id.loading);
                    TextView title = win.findViewById(R.id.title_text);
                    title.setText(itemView.getResources().getString(R.string.rate_entity, artist.getName()));
                    rb.setRating(userRating.getRating());

                    rb.setOnRatingBarChangeListener((RatingBar ratingBar, float rating, boolean fromUser) -> {
                        if (oauth.hasAccount() && ratingProgress.getVisibility() == View.INVISIBLE && fromUser) {
                            ratingProgress.setVisibility(View.VISIBLE);
                            rb.setAlpha(0.3F);
                            api.postArtistRating(
                                    artist.getId(), rating,
                                    metadata -> {
                                        ratingProgress.setVisibility(View.INVISIBLE);
                                        rb.setAlpha(1.0F);
                                        if (metadata.getMessage().getText().equals("OK")) {
                                            userRating.setRating(rating);
                                            api.getArtistRatings(
                                                    artist.getId(),
                                                    this::setAllRating,
                                                    t -> ShowUtil.showToast(itemView.getContext(), t.getMessage()));
                                        } else {
                                            ShowUtil.showToast(itemView.getContext(), "Error");
                                        }
                                        alertDialog.dismiss();
                                    },
                                    t -> {
                                        ratingProgress.setVisibility(View.INVISIBLE);
                                        rb.setAlpha(1.0F);
                                        ShowUtil.showToast(itemView.getContext(), t.getMessage());
                                        alertDialog.dismiss();
                                    });
                        } else {
                            ActivityFactory.startLoginActivity(itemView.getContext());
                        }
                    });
                }
            } else {
                ActivityFactory.startLoginActivity(itemView.getContext());
            }
        }

        private void setAllRating(Artist artist) {
            Rating rating = artist.getRating();
            if (rating != null) {
                Float r = rating.getValue();
                if (r != null) {
                    Integer votesCount = rating.getVotesCount();
                    allRatingView.setText(itemView.getResources().getString(R.string.rating_text, r, votesCount));
                } else {
                    allRatingView.setText(itemView.getResources().getString(R.string.rating_text, 0.0, 0));
                }
            }
        }

        private void setUserRating(Artist artist) {
            Rating rating = artist.getUserRating();
            if (rating != null) {
                Float r = rating.getValue();
                if (r == null) r = 0f;
                userRating.setRating(r);
            }
        }

        private void loadArtistImageFromLastfm(String name) {
            image.setVisibility(View.INVISIBLE);
            progressLoading.setVisibility(View.VISIBLE);
            api.getArtistFromLastfm(
                    name,
                    result -> {
                        if (result.getError() == null || result.getError() == 0) {
                            List<Image> images = result.getArtist().getImages();
                            if (images != null && !images.isEmpty()) {
                                for (Image img : images) {
                                    if (img.getSize().equals(Image.SizeType.MEDIUM.toString()) && !TextUtils.isEmpty(img.getText())) {
                                        Picasso.with(itemView.getContext()).load(img.getText()).fit().into(image);
                                        break;
                                    }
                                }
                            }
                        }
                        progressLoading.setVisibility(View.GONE);
                        image.setVisibility(View.VISIBLE);
                    },
                    t -> {
                        progressLoading.setVisibility(View.GONE);
                        image.setVisibility(View.VISIBLE);
                    });
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

    public ArtistCollectionAdapter(List<Artist> artists, boolean isPrivate) {
        this.artists = artists;
        this.isPrivate = isPrivate;
        Collections.sort(this.artists, (a1, a2) -> (a1.getName()).compareTo(a2.getName()));
    }

    @Override
    public void onBind(ArtistCollectionViewHolder holder, final int position) {
        holder.setOnDeleteListener(onDeleteListener);
        holder.bindTo(artists.get(position), isPrivate);
    }

    @Override
    public int getItemCount() {
        return artists.size();
    }

    @NonNull
    @Override
    public ArtistCollectionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return ArtistCollectionViewHolder.create(parent);
    }

    public interface OnDeleteListener {
        void onDelete(int position);
    }

    private OnDeleteListener onDeleteListener;

    public void setOnDeleteListener(OnDeleteListener onDeleteListener) {
        this.onDeleteListener = onDeleteListener;
    }
}
