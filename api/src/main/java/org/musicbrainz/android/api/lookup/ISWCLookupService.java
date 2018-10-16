package org.musicbrainz.android.api.lookup;

import io.reactivex.Flowable;
import retrofit2.adapter.rxjava2.Result;
import org.musicbrainz.android.api.model.ISWC;

/**
 * Created by Alex on 16.11.2017.
 */

public class ISWCLookupService extends BaseLookupService<ISWC, WorkLookupService.WorkIncType> {

    public ISWCLookupService(String mbid) {
        super(mbid);
    }

    @Override
    public Flowable<Result<ISWC>> lookup() {
        return getJsonRetrofitService().lookupISWC(getMbid(), getParams());
    }

}
