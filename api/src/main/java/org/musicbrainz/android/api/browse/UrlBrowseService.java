package org.musicbrainz.android.api.browse;

import io.reactivex.Flowable;
import retrofit2.adapter.rxjava2.Result;
import org.musicbrainz.android.api.model.Url;

import static org.musicbrainz.android.api.browse.EntityType.RESOURCE_ENTITY;

/**
 * Created by Alex on 17.11.2017.
 */

public class UrlBrowseService extends
        BaseBrowseService<Url, BrowseServiceInterface.EmptyIncType, UrlBrowseService.UrlBrowseEntityType> {

    public UrlBrowseService(UrlBrowseEntityType entityType, String mbid) {
        super(entityType, mbid);
    }

    @Override
    public Flowable<Result<Url>> browse() {
        return getJsonRetrofitService().browseUrl(getParams());
    }

    public enum UrlBrowseEntityType implements BaseBrowseService.BrowseEntityTypeInterface {
        RESOURCE(RESOURCE_ENTITY);

        private final String type;
        UrlBrowseEntityType(String type) {
            this.type = type;
        }
        @Override
        public String toString() {
            return type;
        }
    }

}
