package org.musicbrainz.android.api.coverart;

import io.reactivex.Flowable;
import retrofit2.adapter.rxjava2.Result;

/**
 * Created by Alex on 20.12.2017.
 */

public interface CoverArtServiceInterface {

    Flowable<Result<ReleaseCoverArt>> getReleaseCoverArt(String mbid);

    Flowable<Result<ReleaseCoverArt>> getReleaseGroupCoverArt(String mbid);
}
