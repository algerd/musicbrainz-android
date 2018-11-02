package org.musicbrainz.android.api.browse;

import io.reactivex.Flowable;
import retrofit2.adapter.rxjava2.Result;
import org.musicbrainz.android.api.model.Artist;

import static org.musicbrainz.android.api.lookup.IncType.*;
import static org.musicbrainz.android.api.browse.EntityType.*;

/**
 * Created by Alex on 17.11.2017.
 */

public class ArtistBrowseService extends
        BaseBrowseService<Artist.ArtistBrowse, ArtistBrowseService.ArtistIncType, ArtistBrowseService.ArtistBrowseEntityType> {

    public ArtistBrowseService(ArtistBrowseEntityType entityType, String mbid) {
        super(entityType, mbid);
    }

    @Override
    public Flowable<Result<Artist.ArtistBrowse>> browse() {
        return getJsonRetrofitService().browseArtist(getParams());
    }

    public enum ArtistBrowseEntityType implements BaseBrowseService.BrowseEntityTypeInterface {
        AREA(AREA_ENTITY),
        COLLECTION(COLLECTION_ENTITY),
        RECORDING(RECORDING_ENTITY),
        RELEASE(RELEASE_ENTITY),
        RELEASE_GROUP(RELEASE_GROUP_ENTITY),
        WORK(WORK_ENTITY);

        private final String type;
        ArtistBrowseEntityType(String type) {
            this.type = type;
        }
        @Override
        public String toString() {
            return type;
        }
    }

    public enum ArtistIncType implements BrowseServiceInterface.IncTypeInterface {
        ALIASES(ALIASES_INC),
        ANNOTATION(ANNOTATION_INC),
        GENRES(GENRES_INC),
        TAGS(TAGS_INC),
        RATINGS(RATINGS_INC),
        USER_TAGS(USER_TAGS_INC),         //require authorization
        USER_RATINGS(USER_RATINGS_INC);   //require authorization

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
