package org.musicbrainz.android.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import org.musicbrainz.android.R;
import org.musicbrainz.android.api.model.Artist;
import org.musicbrainz.android.api.model.Recording;
import org.musicbrainz.android.communicator.GetRecordingCommunicator;
import org.musicbrainz.android.util.ShowUtil;

import static org.musicbrainz.android.MusicBrainzApp.api;
import static org.musicbrainz.android.api.lyrics.model.LyricsApi.LYRICS_INSTRUMENTAL;
import static org.musicbrainz.android.api.lyrics.model.LyricsApi.LYRICS_NOT_FOUND;


public class RecordingLyricsFragment extends LazyFragment {

    private View content;
    private View error;
    private View loading;
    private View noresults;
    private TextView lyrics;

    public static RecordingLyricsFragment newInstance() {
        Bundle args = new Bundle();
        RecordingLyricsFragment fragment = new RecordingLyricsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_recording_lyrics, container, false);

        content = layout.findViewById(R.id.content);
        error = layout.findViewById(R.id.error);
        loading = layout.findViewById(R.id.loading);
        noresults = layout.findViewById(R.id.noresults);
        lyrics = layout.findViewById(R.id.lyrics);

        loadView();
        return layout;
    }

    @Override
    public void lazyLoad() {
        content.setVisibility(View.GONE);
        loading.setVisibility(View.GONE);
        error.setVisibility(View.GONE);
        noresults.setVisibility(View.GONE);

        Recording recording = ((GetRecordingCommunicator) getContext()).getRecording();
        if (recording != null) {
            List<Artist.ArtistCredit> artists = recording.getArtistCredits();
            if (!artists.isEmpty()) {
                viewProgressLoading(true);
                // Получить лирику из сервиса http://lyrics.wikia.com/wikia.php
                // Нестабильно даёт результат. Лучше парсить http://lyrics.wikia.com
                // пример: Deep Purple Smoke On The Water. С сервиса - error, с парсинга - текст песни.
                /*
                api.getLyricsWikia(
                        artists.get(0).getName(), recording.getTitle(),
                        lyricsResult -> {
                            lyrics.setText(lyricsResult.getResult().getLyrics());
                            viewProgressLoading(false);
                        },
                        t -> {
                            viewProgressLoading(false);
                            if (LyricsService.isNotFound(((HttpException) t).getContent())) {
                                noresults.setVisibility(View.VISIBLE);
                                content.setVisibility(View.GONE);
                            } else {
                                showConnectionWarning(t);
                            }
                        });
                */

                // Получить лирику парсингом сайта http://lyrics.wikia.com.
                api.getLyricsWikiaApi(
                        artists.get(0).getName(), recording.getTitle(),
                        lyricsApi -> {
                            viewProgressLoading(false);
                            String text = lyricsApi.getLyrics();
                            if (text.equals(LYRICS_NOT_FOUND) || text.equals(LYRICS_INSTRUMENTAL)) {
                                noresults.setVisibility(View.VISIBLE);
                                content.setVisibility(View.GONE);
                            } else {
                                lyrics.setText(text);
                            }
                        },
                        this::showConnectionWarning);

            } else {
                noresults.setVisibility(View.VISIBLE);
                content.setVisibility(View.GONE);
            }
        }
    }

    private void viewProgressLoading(boolean view) {
        if (view) {
            content.setVisibility(View.GONE);
            loading.setVisibility(View.VISIBLE);
        } else {
            content.setVisibility(View.VISIBLE);
            loading.setVisibility(View.GONE);
        }
    }

    private void viewError(boolean view) {
        if (view) {
            content.setVisibility(View.INVISIBLE);
            error.setVisibility(View.VISIBLE);
        } else {
            error.setVisibility(View.GONE);
            content.setVisibility(View.VISIBLE);
        }
    }

    private void showConnectionWarning(Throwable t) {
        ShowUtil.showError(getActivity(), t);
        viewProgressLoading(false);
        viewError(true);
        error.findViewById(R.id.retry_button).setOnClickListener(v -> lazyLoad());
    }

}
