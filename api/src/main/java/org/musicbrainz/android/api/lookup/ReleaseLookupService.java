package org.musicbrainz.android.api.lookup;

import io.reactivex.Flowable;
import retrofit2.adapter.rxjava2.Result;
import org.musicbrainz.android.api.model.Release;
import org.musicbrainz.android.api.model.ReleaseGroup;

import static org.musicbrainz.android.api.lookup.IncType.*;

/**
 * Created by Alex on 16.11.2017.
 */

public class ReleaseLookupService extends BaseLookupService<Release, ReleaseLookupService.ReleaseIncType> {

    public ReleaseLookupService(String mbid) {
        super(mbid);
    }

    @Override
    public Flowable<Result<Release>> lookup() {
        return getJsonRetrofitService().lookupRelease(getMbid(), getParams());
    }

    public ReleaseLookupService addReleaseGroupType(ReleaseGroup.AlbumType type) {
        addParam(LookupParamType.TYPE, type.toString().toLowerCase());
        return this;
    }

    public enum ReleaseIncType implements LookupServiceInterface.IncTypeInterface {
        ARTISTS(ARTISTS_INC),
        COLLECTIONS(COLLECTIONS_INC),
        LABELS(LABELS_INC),
        RECORDINGS(RECORDINGS_INC),
        RELEASE_GROUPS(RELEASE_GROUPS_INC),

        DISCIDS(DISCIDS_INC),
        MEDIA(MEDIA_INC),
        ARTIST_CREDITS(ARTIST_CREDITS_INC), // equal ARTISTS("artists")
        // TODO: user-collections?
        //USER_COLLECTIONS(USER_COLLECTIONS_INC),

        ALIASES(ALIASES_INC),
        ANNOTATION(ANNOTATION_INC),
        GENRES(GENRES_INC),
        TAGS(TAGS_INC),
        USER_GENRES(USER_GENRES_INC),     //require authorization
        USER_TAGS(USER_TAGS_INC);         //require authorization

        private final String inc;
        ReleaseIncType(String inc) {
            this.inc = inc;
        }
        @Override
        public String toString() {
            return inc;
        }
    }

}
