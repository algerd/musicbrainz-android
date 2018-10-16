package org.musicbrainz.android.api.lastfm;

import io.reactivex.Flowable;
import retrofit2.adapter.rxjava2.Result;
import org.musicbrainz.android.api.lastfm.model.LastfmResult;

/**
 * Created by Alex on 30.01.2018.
 */

public interface LastfmServiceInterface {

    @Deprecated
    Flowable<Result<LastfmResult>> getArtistInfoByMbid(String mbid);

    Flowable<Result<LastfmResult>> getArtistInfoByName(String name);

    @Deprecated
    Flowable<Result<LastfmResult>> getAlbumInfoByMbid(String mbid);

    Flowable<Result<LastfmResult>> getAlbumInfoByName(String artistName, String albumName);

    @Deprecated
    Flowable<Result<LastfmResult>> getTrackInfoByMbid(String mbid);

    Flowable<Result<LastfmResult>> getTrackInfoByName(String artistName, String trackName);

}
