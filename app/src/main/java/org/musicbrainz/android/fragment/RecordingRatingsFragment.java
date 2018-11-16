package org.musicbrainz.android.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TableLayout;
import android.widget.TextView;

import java.util.List;

import org.musicbrainz.android.R;
import org.musicbrainz.android.api.lastfm.model.Track;
import org.musicbrainz.android.api.model.Artist;
import org.musicbrainz.android.api.model.Rating;
import org.musicbrainz.android.api.model.Recording;
import org.musicbrainz.android.communicator.GetRecordingCommunicator;
import org.musicbrainz.android.intent.ActivityFactory;
import org.musicbrainz.android.util.ShowUtil;
import org.musicbrainz.android.util.StringFormat;

import static org.musicbrainz.android.MusicBrainzApp.api;
import static org.musicbrainz.android.MusicBrainzApp.oauth;


public class RecordingRatingsFragment extends LazyFragment {

    private final float LASTFM_TRACK_LISTENERS_COEFF = 35;
    private final float LASTFM_TRACK_PLAYCOUNT_COEFF = 120;

    private Recording recording;

    private View content;
    private View error;
    private View loading;
    private TextView loginWarning;
    private TextView allRatingText;
    private RatingBar userRatingBar;
    private TableLayout lastfmTable;
    private TextView lastfmListeners;
    private TextView lastfmPlaycount;
    private RatingBar lastfmListenersRatingBar;
    private RatingBar lastfmPlaycountRatingBar;

    public static RecordingRatingsFragment newInstance() {
        Bundle args = new Bundle();
        RecordingRatingsFragment fragment = new RecordingRatingsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_recording_ratings, container, false);

        content = layout.findViewById(R.id.content);
        error = layout.findViewById(R.id.error);
        loading = layout.findViewById(R.id.loading);
        loginWarning = layout.findViewById(R.id.login_warning);
        allRatingText = layout.findViewById(R.id.all_rating_text);
        userRatingBar = layout.findViewById(R.id.user_rating_bar);
        lastfmTable = layout.findViewById(R.id.lastfm_table);
        lastfmListeners = layout.findViewById(R.id.lastfm_listeners);
        lastfmPlaycount = layout.findViewById(R.id.lastfm_playcount);
        lastfmListenersRatingBar = layout.findViewById(R.id.lastfm_listeners_rating_bar);
        lastfmPlaycountRatingBar = layout.findViewById(R.id.lastfm_playcount_rating_bar);

        setEditListeners();
        loadView();

        return layout;
    }

    @Override
    public void onStart() {
        super.onStart();
        loginWarning.setVisibility(oauth.hasAccount() ? View.GONE : View.VISIBLE);
    }

    private void setEditListeners() {
        userRatingBar.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            if (loading.getVisibility() == View.VISIBLE) {
                return;
            }
            if (oauth.hasAccount()) {
                if (fromUser) {
                    postRating(rating);
                }
            } else {
                ActivityFactory.startLoginActivity(getContext());
            }
        });
    }

    @Override
    public void lazyLoad() {
        viewProgressLoading(false);
        viewError(false);

        recording = ((GetRecordingCommunicator) getContext()).getRecording();
        if (recording != null) {
            setUserRating();
            setAllRating();
            setLastfmInfo();
        }
    }

    private void setAllRating() {
        Rating rating = recording.getRating();
        if (rating != null) {
            Float r = rating.getValue();
            if (r != null) {
                Integer votesCount = rating.getVotesCount();
                allRatingText.setText(getString(R.string.rating_text, r, votesCount));
            } else {
                allRatingText.setText(getString(R.string.rating_text, 0.0, 0));
            }
        }
    }

    private void setUserRating() {
        Rating rating = recording.getUserRating();
        if (rating != null) {
            Float r = rating.getValue();
            if (r == null) r = 0f;
            userRatingBar.setRating(r);
        }
    }

    private void setLastfmInfo() {
        List<Artist.ArtistCredit> credits = recording.getArtistCredits();
        if (credits != null && !credits.isEmpty()) {
            api.getTrackFromLastfm(
                    credits.get(0).getName(), recording.getTitle(),
                    info -> {
                        Track track = info.getTrack();
                        if (track != null) {
                            lastfmTable.setVisibility(View.VISIBLE);

                            int listeners = track.getListeners();
                            int playCount = track.getPlaycount();

                            lastfmListeners.setText(StringFormat.decimalFormat(listeners));
                            lastfmPlaycount.setText(StringFormat.decimalFormat(playCount));

                            lastfmListenersRatingBar.setRating((float) Math.sqrt(listeners) / LASTFM_TRACK_LISTENERS_COEFF);
                            lastfmPlaycountRatingBar.setRating((float) Math.sqrt(playCount) / LASTFM_TRACK_PLAYCOUNT_COEFF);
                        } else {
                            lastfmTable.setVisibility(View.GONE);
                        }
                    },
                    t -> lastfmTable.setVisibility(View.GONE)
            );
        } else {
            lastfmTable.setVisibility(View.GONE);
        }
    }

    private void postRating(float rating) {
        viewProgressLoading(true);
        api.postRecordingRating(
                recording.getId(), rating,
                metadata -> {
                    if (metadata.getMessage().getText().equals("OK")) {
                        api.getRecordingRatings(
                                recording.getId(),
                                a -> {
                                    recording.setRating(a.getRating());
                                    recording.setUserRating(a.getUserRating());
                                    setAllRating();
                                    setUserRating();
                                    viewProgressLoading(false);
                                },
                                this::showConnectionWarning);
                    } else {
                        viewProgressLoading(false);
                        ShowUtil.showMessage(getActivity(), "Error");
                    }
                },
                this::showConnectionWarning);
    }

    private void viewProgressLoading(boolean isView) {
        if (isView) {
            content.setAlpha(0.3F);
            userRatingBar.setIsIndicator(true);
            loading.setVisibility(View.VISIBLE);
        } else {
            content.setAlpha(1.0F);
            userRatingBar.setIsIndicator(false);
            loading.setVisibility(View.GONE);
        }
    }

    private void viewError(boolean isView) {
        if (isView) {
            content.setVisibility(View.INVISIBLE);
            error.setVisibility(View.VISIBLE);
        } else {
            error.setVisibility(View.GONE);
            content.setVisibility(View.VISIBLE);
        }
    }

    private void showConnectionWarning(Throwable t) {
        //ShowUtil.showError(getActivity(), t);
        viewProgressLoading(false);
        viewError(true);
        error.setVisibility(View.VISIBLE);
        error.findViewById(R.id.retry_button).setOnClickListener(v -> lazyLoad());
    }

}
