package org.musicbrainz.android.api.browse;

import io.reactivex.Flowable;
import retrofit2.adapter.rxjava2.Result;
import org.musicbrainz.android.api.model.ReleaseGroup;
import org.musicbrainz.android.api.core.ApiUtils;

import static org.musicbrainz.android.api.lookup.IncType.*;
import static org.musicbrainz.android.api.browse.EntityType.*;

/**
 * Created by Alex on 17.11.2017.
 */

public class ReleaseGroupBrowseService extends
        BaseBrowseService<ReleaseGroup.ReleaseGroupBrowse, ReleaseGroupBrowseService.ReleaseGroupIncType, ReleaseGroupBrowseService.ReleaseGroupBrowseEntityType> {

    public ReleaseGroupBrowseService(ReleaseGroupBrowseEntityType entityType, String mbid) {
        super(entityType, mbid);
    }

    @Override
    public Flowable<Result<ReleaseGroup.ReleaseGroupBrowse>> browse() {
        return getJsonRetrofitService().browseReleaseGroup(getParams());
    }

    public ReleaseGroupBrowseService addTypes(ReleaseGroup.AlbumType... types) {
        addParam(BrowseParamType.TYPE, ApiUtils.getStringFromArray(types, "|").toLowerCase());
        return this;
    }

    public enum ReleaseGroupBrowseEntityType implements BaseBrowseService.BrowseEntityTypeInterface {
        ARTIST(ARTIST_ENTITY),
        COLLECTION(COLLECTION_ENTITY),
        RELEASE(RELEASE_ENTITY);

        private final String type;
        ReleaseGroupBrowseEntityType(String type) {
            this.type = type;
        }
        @Override
        public String toString() {
            return type;
        }
    }

    public enum ReleaseGroupIncType implements BrowseServiceInterface.IncTypeInterface {
        ALIASES(ALIASES_INC),
        ANNOTATION(ANNOTATION_INC),
        TAGS(TAGS_INC),
        GENRES(GENRES_INC),
        RATINGS(RATINGS_INC),
        USER_GENRES(USER_GENRES_INC),     //require authorization
        USER_TAGS(USER_TAGS_INC),             //require authorization
        USER_RATINGS(USER_RATINGS_INC),       //require authorization

        ARTIST_CREDITS(ARTIST_CREDITS_INC);

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
