package org.musicbrainz.android.api.lookup;

import io.reactivex.Flowable;
import retrofit2.adapter.rxjava2.Result;
import org.musicbrainz.android.api.model.Artist;
import org.musicbrainz.android.api.model.Release;
import org.musicbrainz.android.api.model.ReleaseGroup;

import static org.musicbrainz.android.api.lookup.IncType.*;


/**
 * Created by Alex on 16.11.2017.
 */
public class ArtistLookupService extends BaseLookupService<Artist, ArtistLookupService.ArtistIncType> {

    public ArtistLookupService(String mbid) {
        super(mbid);
    }

    @Override
    public Flowable<Result<Artist>> lookup() {
        return getJsonRetrofitService().lookupArtist(getMbid(), getParams());
    }

    public ArtistLookupService addReleaseGroupType(ReleaseGroup.AlbumType type) {
        addParam(LookupParamType.TYPE, type.toString().toLowerCase());
        return this;
    }

    public ArtistLookupService addReleaseStatus(Release.Status status) {
        addParam(LookupParamType.STATUS, status.toString());
        return this;
    }

    public enum ArtistIncType implements LookupServiceInterface.IncTypeInterface {
        ALIASES(ALIASES_INC),
        ANNOTATION(ANNOTATION_INC),
        TAGS(TAGS_INC),
        RATINGS(RATINGS_INC),
        USER_TAGS(USER_TAGS_INC),         //require authorization
        USER_RATINGS(USER_RATINGS_INC),   //require authorization

        RECORDINGS(RECORDINGS_INC),
        RELEASES(RELEASES_INC),
        RELEASE_GROUPS(RELEASE_GROUPS_INC),
        //TODO: various-artists?
        //VARIOUS_ARTISTS(VARIOUS_ARTISTS_INC),
        WORKS(WORKS_INC);

        private final String inc;

        ArtistIncType(String inc) {
            this.inc = inc;
        }

        @Override
        public String toString() {
            return inc;
        }
    }

}
