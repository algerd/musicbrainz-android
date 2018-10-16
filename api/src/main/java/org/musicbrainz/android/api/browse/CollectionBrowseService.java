package org.musicbrainz.android.api.browse;

import io.reactivex.Flowable;
import retrofit2.adapter.rxjava2.Result;
import org.musicbrainz.android.api.model.Collection;

import static org.musicbrainz.android.api.browse.EntityType.AREA_ENTITY;
import static org.musicbrainz.android.api.browse.EntityType.ARTIST_ENTITY;
import static org.musicbrainz.android.api.browse.EntityType.EDITOR_ENTITY;
import static org.musicbrainz.android.api.browse.EntityType.EVENT_ENTITY;
import static org.musicbrainz.android.api.browse.EntityType.LABEL_ENTITY;
import static org.musicbrainz.android.api.browse.EntityType.PLACE_ENTITY;
import static org.musicbrainz.android.api.browse.EntityType.RECORDING_ENTITY;
import static org.musicbrainz.android.api.browse.EntityType.RELEASE_ENTITY;
import static org.musicbrainz.android.api.browse.EntityType.RELEASE_GROUP_ENTITY;
import static org.musicbrainz.android.api.browse.EntityType.WORK_ENTITY;
import static org.musicbrainz.android.api.lookup.IncType.USER_COLLECTIONS_INC;

/**
 * Created by Alex on 17.11.2017.
 */

public class CollectionBrowseService extends
        BaseBrowseService<Collection.CollectionBrowse, CollectionBrowseService.CollectionIncType, CollectionBrowseService.CollectionBrowseEntityType> {

    public CollectionBrowseService(CollectionBrowseEntityType entityType, String mbid) {
        super(entityType, mbid);
    }

    @Override
    public Flowable<Result<Collection.CollectionBrowse>> browse() {
        return getJsonRetrofitService().browseCollection(getParams());
    }

    public enum CollectionBrowseEntityType implements BaseBrowseService.BrowseEntityTypeInterface {
        AREA(AREA_ENTITY),
        ARTIST(ARTIST_ENTITY),
        EDITOR(EDITOR_ENTITY),
        EVENT(EVENT_ENTITY),
        LABEL(LABEL_ENTITY),
        PLACE(PLACE_ENTITY),
        RECORDING(RECORDING_ENTITY),
        RELEASE(RELEASE_ENTITY),
        RELEASE_GROUP(RELEASE_GROUP_ENTITY),
        WORK(WORK_ENTITY);

        private final String type;
        CollectionBrowseEntityType(String type) {
            this.type = type;
        }
        @Override
        public String toString() {
            return type;
        }
    }

    public enum CollectionIncType implements BrowseServiceInterface.IncTypeInterface {
        USER_COLLECTIONS(USER_COLLECTIONS_INC);   //require authorization

        private final String inc;
        CollectionIncType(String inc) {
            this.inc = inc;
        }
        @Override
        public String toString() {
            return inc;
        }
    }

}
