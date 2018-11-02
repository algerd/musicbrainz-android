package org.musicbrainz.android.api.lookup;

import io.reactivex.Flowable;
import retrofit2.adapter.rxjava2.Result;
import org.musicbrainz.android.api.model.Recording;
import org.musicbrainz.android.api.model.Release;
import org.musicbrainz.android.api.model.ReleaseGroup;

import static org.musicbrainz.android.api.lookup.IncType.*;

/**
 * Created by Alex on 16.11.2017.
 */

public class RecordingLookupService extends BaseLookupService<Recording, RecordingLookupService.RecordingIncType> {

    public RecordingLookupService(String mbid) {
        super(mbid);
    }

    @Override
    public Flowable<Result<Recording>> lookup() {
        return getJsonRetrofitService().lookupRecording(getMbid(), getParams());
    }

    public RecordingLookupService addReleaseGroupType(ReleaseGroup.AlbumType type) {
        addParam(LookupParamType.TYPE, type.toString().toLowerCase());
        return this;
    }

    public RecordingLookupService addReleaseStatus(Release.Status status) {
        addParam(LookupParamType.STATUS, status.toString());
        return this;
    }

    public enum RecordingIncType implements LookupServiceInterface.IncTypeInterface {
        ARTISTS(ARTISTS_INC),
        RELEASES(RELEASES_INC),

        ARTIST_CREDITS(ARTIST_CREDITS_INC), // equal ARTISTS("artists")
        // TODO: ISRCS
        /*
         conflict with return type isrcs field
         search: https://musicbrainz.org/ws/2/recording?fmt=json&query=isrc:USGF19942501
         lookup: https://musicbrainz.org/ws/2/recording/6da76448-982a-4a01-b65b-9a710301c9c9?fmt=json&inc=isrcs
         search return array of objects with fields id = isrc
         lookup ISRCS return array of String isrc
         */
        ISRCS(ISRCS_INC),

        ALIASES(ALIASES_INC),
        ANNOTATION(ANNOTATION_INC),
        GENRES(GENRES_INC),
        TAGS(TAGS_INC),
        RATINGS(RATINGS_INC),
        USER_GENRES(USER_GENRES_INC),     //require authorization
        USER_TAGS(USER_TAGS_INC),         //require authorization
        USER_RATINGS(USER_RATINGS_INC);   //require authorization

        private final String inc;
        RecordingIncType(String inc) {
            this.inc = inc;
        }
        @Override
        public String toString() {
            return inc;
        }
    }

}
