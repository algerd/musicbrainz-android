package org.musicbrainz.android.api.lyrics;

import io.reactivex.Flowable;
import io.reactivex.annotations.NonNull;
import retrofit2.adapter.rxjava2.Result;
import org.musicbrainz.android.api.lyrics.model.LyricsResult;
import org.musicbrainz.android.api.lyrics.model.LyricsApi;

/**
 * Created by Alex on 29.01.2018.
 */

public interface LyricsServiceInterface {

    Flowable<Result<LyricsResult>> getLyricsWikia(@NonNull String artist, @NonNull String song);

    Flowable<Result<LyricsApi>> getLyricsWikiaApi(@NonNull String artist, @NonNull String song);

}
