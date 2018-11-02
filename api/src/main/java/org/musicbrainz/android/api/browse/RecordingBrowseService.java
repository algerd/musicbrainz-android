package org.musicbrainz.android.api.browse;

import io.reactivex.Flowable;
import retrofit2.adapter.rxjava2.Result;
import org.musicbrainz.android.api.model.Recording;

import static org.musicbrainz.android.api.lookup.IncType.*;
import static org.musicbrainz.android.api.browse.EntityType.*;

/**
 * Created by Alex on 17.11.2017.
 */

public class RecordingBrowseService extends
        BaseBrowseService<Recording.RecordingBrowse, RecordingBrowseService.RecordingIncType, RecordingBrowseService.RecordingBrowseEntityType> {

    public RecordingBrowseService(RecordingBrowseEntityType entityType, String mbid) {
        super(entityType, mbid);
    }

    @Override
    public Flowable<Result<Recording.RecordingBrowse>> browse() {
        return getJsonRetrofitService().browseRecording(getParams());
    }

    public enum RecordingBrowseEntityType implements BaseBrowseService.BrowseEntityTypeInterface {
        ARTIST(ARTIST_ENTITY),
        COLLECTION(COLLECTION_ENTITY),
        RELEASE(RELEASE_ENTITY);

        private final String type;
        RecordingBrowseEntityType(String type) {
            this.type = type;
        }
        @Override
        public String toString() {
            return type;
        }
    }

    public enum RecordingIncType implements BrowseServiceInterface.IncTypeInterface {
        ALIASES(ALIASES_INC),
        ANNOTATION(ANNOTATION_INC),
        GENRES(GENRES_INC),
        TAGS(TAGS_INC),
        RATINGS(RATINGS_INC),
        USER_TAGS(USER_TAGS_INC),         //require authorization
        USER_RATINGS(USER_RATINGS_INC),   //require authorization

        // TODO: ISRCS
        /*
         conflict with return type isrcs field
         search: https://musicbrainz.org/ws/2/recording?fmt=json&query=isrc:USGF19942501
         lookup: https://musicbrainz.org/ws/2/recording/6da76448-982a-4a01-b65b-9a710301c9c9?fmt=json&inc=isrcs
         search return array of objects with fields id = isrc
         lookup ISRCS return array of String isrc
         */
        //ISRCS(ISRCS_INC),
        ARTIST_CREDITS(ARTIST_CREDITS_INC);


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
