package org.musicbrainz.android.api.coverart;

import io.reactivex.Flowable;
import retrofit2.adapter.rxjava2.Result;
import org.musicbrainz.android.api.core.WebService;
import org.musicbrainz.android.api.core.WebServiceInterface;

import static org.musicbrainz.android.api.coverart.CoverArtRetrofitService.RELEASE_GROUP_PATH;
import static org.musicbrainz.android.api.coverart.CoverArtRetrofitService.RELEASE_PATH;
import static org.musicbrainz.android.api.coverart.CoverArtRetrofitService.COVERART_WEB_SERVICE;

/**
 * Created by Alex on 20.12.2017.
 */

public class CoverArtService implements CoverArtServiceInterface {

    private static final WebServiceInterface<CoverArtRetrofitService> webService =
            new WebService(CoverArtRetrofitService.class, COVERART_WEB_SERVICE);

    @Override
    public Flowable<Result<ReleaseCoverArt>> getReleaseCoverArt(String mbid) {
        return webService.getJsonRetrofitService().getCoverArts(RELEASE_PATH, mbid);
    }

    @Override
    public Flowable<Result<ReleaseCoverArt>> getReleaseGroupCoverArt(String mbid) {
        return webService.getJsonRetrofitService().getCoverArts(RELEASE_GROUP_PATH, mbid);
    }
}
