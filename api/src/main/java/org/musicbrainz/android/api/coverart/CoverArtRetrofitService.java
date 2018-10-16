package org.musicbrainz.android.api.coverart;

import io.reactivex.Flowable;
import retrofit2.adapter.rxjava2.Result;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by Alex on 20.12.2017.
 */

public interface CoverArtRetrofitService {

    String COVERART_WEB_SERVICE = "http://coverartarchive.org";
    String RELEASE_PATH = "release";
    String RELEASE_GROUP_PATH = "release-group";

    @GET(COVERART_WEB_SERVICE + "/{path}" + "/{mbid}")
    Flowable<Result<ReleaseCoverArt>> getCoverArts(
            @Path("path") String path,
            @Path("mbid") String mbid);
}
