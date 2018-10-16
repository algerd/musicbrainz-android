package org.musicbrainz.android.api.browse;

import io.reactivex.Flowable;
import retrofit2.adapter.rxjava2.Result;
import org.musicbrainz.android.api.model.Area;

import static org.musicbrainz.android.api.lookup.IncType.*;
import static org.musicbrainz.android.api.browse.EntityType.COLLECTION_ENTITY;

/**
 * Created by Alex on 17.11.2017.
 */

public class AreaBrowseService extends
        BaseBrowseService<Area.AreaBrowse, AreaBrowseService.AreaIncType, AreaBrowseService.AreaBrowseEntityType> {

    public AreaBrowseService(AreaBrowseEntityType entityType, String mbid) {
        super(entityType, mbid);
    }

    @Override
    public Flowable<Result<Area.AreaBrowse>> browse() {
        return getJsonRetrofitService().browseArea(getParams());
    }

    public enum AreaBrowseEntityType implements BaseBrowseService.BrowseEntityTypeInterface {
        COLLECTION(COLLECTION_ENTITY);

        private final String type;
        AreaBrowseEntityType(String type) {
            this.type = type;
        }
        @Override
        public String toString() {
            return type;
        }
    }

    public enum AreaIncType implements BrowseServiceInterface.IncTypeInterface {
        ALIASES(ALIASES_INC),
        ANNOTATION(ANNOTATION_INC),
        TAGS(TAGS_INC),
        USER_TAGS(USER_TAGS_INC);         //require authorization

        private final String inc;
        AreaIncType(String inc) {
            this.inc = inc;
        }
        @Override
        public String toString() {
            return inc;
        }
    }

}
