package org.musicbrainz.android.adapter.recycler;

import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import org.musicbrainz.android.R;
import org.musicbrainz.android.api.model.Rating;
import org.musicbrainz.android.api.model.Recording;
import org.musicbrainz.android.intent.ActivityFactory;
import org.musicbrainz.android.util.ShowUtil;

import static org.musicbrainz.android.MusicBrainzApp.api;
import static org.musicbrainz.android.MusicBrainzApp.oauth;

/**
 * Created by Alex on 17.01.2018.
 */

public class RecordingCollectionAdapter extends BaseRecyclerViewAdapter<RecordingCollectionAdapter.ViewHolder> {

    private List<Recording> recordings;

    public static class ViewHolder extends BaseRecyclerViewAdapter.BaseViewHolder {

        private TextView recordingNameTextView;
        private ImageView deleteButton;
        private RatingBar userRating;
        private TextView allRatingView;
        private LinearLayout ratingContainer;

        public ViewHolder(CardView v) {
            super(v);
            recordingNameTextView = v.findViewById(R.id.recording_name);
            deleteButton = v.findViewById(R.id.delete);
            userRating = v.findViewById(R.id.user_rating);
            allRatingView = v.findViewById(R.id.all_rating);
            ratingContainer = v.findViewById(R.id.rating_container);
        }

        public void bindView(Recording recording, boolean isPrivate) {
            deleteButton.setVisibility(isPrivate ? View.VISIBLE : View.GONE);
            recordingNameTextView.setText(recording.getTitle());
            setUserRating(recording);
            setAllRating(recording);

            ratingContainer.setOnClickListener(v -> showRatingBar(recording));
        }

        private void showRatingBar(Recording recording) {
            if (oauth.hasAccount()) {
                AlertDialog alertDialog = new AlertDialog.Builder(itemView.getContext()).create();
                alertDialog.show();
                Window win = alertDialog.getWindow();
                if (win != null) {
                    win.setContentView(R.layout.dialog_rating_bar);
                    RatingBar rb = win.findViewById(R.id.rating_bar);
                    rb.setRating(userRating.getRating());
                    TextView title = win.findViewById(R.id.title_text);
                    title.setText(itemView.getResources().getString(R.string.rate_entity, recording.getTitle()));

                    rb.setOnRatingBarChangeListener((RatingBar ratingBar, float rating, boolean fromUser) -> {
                        if (oauth.hasAccount()) {
                            if (fromUser) {
                                api.postRecordingRating(
                                        recording.getId(), rating,
                                        metadata -> {
                                            if (metadata.getMessage().getText().equals("OK")) {
                                                userRating.setRating(rating);
                                                api.getRecordingRatings(
                                                        recording.getId(),
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

        private void setAllRating(Recording recording) {
            Rating rating = recording.getRating();
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

        private void setUserRating(Recording recording) {
            Rating rating = recording.getUserRating();
            if (rating != null) {
                Float r = rating.getValue();
                if (r == null) r = 0f;
                userRating.setRating(r);
            }
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

    public RecordingCollectionAdapter(List<Recording> recordings, boolean isPrivate) {
        this.recordings = recordings;
        this.isPrivate = isPrivate;
        Collections.sort(this.recordings, (a1, a2) -> (a1.getTitle()).compareTo(a2.getTitle()));
    }

    @Override
    public void onBind(ViewHolder holder, final int position) {
        holder.setOnDeleteListener(onDeleteListener);
        holder.bindView(recordings.get(position), isPrivate);
    }

    @Override
    public int getItemCount() {
        return recordings.size();
    }

    @Override
    public RecordingCollectionAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(inflateCardView(parent, R.layout.card_recording_collection));
    }

    public interface OnDeleteListener {
        void onDelete(int position);
    }

    private OnDeleteListener onDeleteListener;

    public void setOnDeleteListener(OnDeleteListener onDeleteListener) {
        this.onDeleteListener = onDeleteListener;
    }
}
