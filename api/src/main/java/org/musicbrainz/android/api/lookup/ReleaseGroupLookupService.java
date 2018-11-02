package org.musicbrainz.android.api.lookup;

import io.reactivex.Flowable;
import retrofit2.adapter.rxjava2.Result;
import org.musicbrainz.android.api.model.Release;
import org.musicbrainz.android.api.model.ReleaseGroup;

import static org.musicbrainz.android.api.lookup.IncType.*;

/**
 * Created by Alex on 16.11.2017.
 */

public class ReleaseGroupLookupService
        extends BaseLookupService<ReleaseGroup, ReleaseGroupLookupService.ReleaseGroupIncType> {

    public ReleaseGroupLookupService(String mbid) {
        super(mbid);
    }

    @Override
    public Flowable<Result<ReleaseGroup>> lookup() {
        return getJsonRetrofitService().lookupReleaseGroup(getMbid(), getParams());
    }

    public ReleaseGroupLookupService addReleaseGroupType(ReleaseGroup.AlbumType type) {
        addParam(LookupParamType.TYPE, type.toString().toLowerCase());
        return this;
    }

    public ReleaseGroupLookupService addReleaseStatus(Release.Status status) {
        addParam(LookupParamType.STATUS, status.toString());
        return this;
    }

    public enum ReleaseGroupIncType implements LookupServiceInterface.IncTypeInterface {
        ARTISTS(ARTISTS_INC),
        RELEASES(RELEASES_INC),
        ARTIST_CREDITS(ARTIST_CREDITS_INC),

        ALIASES(ALIASES_INC),
        ANNOTATION(ANNOTATION_INC),
        TAGS(TAGS_INC),
        GENRES(GENRES_INC),
        RATINGS(RATINGS_INC),
        USER_TAGS(USER_TAGS_INC),             //require authorization
        USER_GENRES(USER_GENRES_INC),         //require authorization
        USER_RATINGS(USER_RATINGS_INC);       //require authorization

        private final String inc;
        ReleaseGroupIncType(String inc) {
            this.inc = inc;
        }
        @Override
        public String toString() {
            return inc;
        }
    }

}
