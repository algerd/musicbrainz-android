package org.musicbrainz.android.api.lookup;

import io.reactivex.Flowable;
import retrofit2.adapter.rxjava2.Result;
import org.musicbrainz.android.api.model.Collection;

/**
 * Created by Alex on 16.11.2017.
 */

public class CollectionLookupService extends BaseLookupService<Collection, LookupServiceInterface.EmptyIncType> {

    public CollectionLookupService(String mbid) {
        super(mbid);
    }

    @Override
    public Flowable<Result<Collection>> lookup() {
        return getJsonRetrofitService().lookupCollection(getMbid(), getParams());
    }

}
