package org.musicbrainz.android.api.lookup;

import io.reactivex.Flowable;
import retrofit2.adapter.rxjava2.Result;
import org.musicbrainz.android.api.model.ISRC;
import org.musicbrainz.android.api.model.Release;
import org.musicbrainz.android.api.model.ReleaseGroup;

/**
 * Created by Alex on 16.11.2017.
 */

public class ISRCLookupService extends BaseLookupService<ISRC, RecordingLookupService.RecordingIncType> {

    public ISRCLookupService(String mbid) {
        super(mbid);
    }

    @Override
    public Flowable<Result<ISRC>> lookup() {
        return getJsonRetrofitService().lookupISRC(getMbid(), getParams());
    }

    public ISRCLookupService addReleaseGroupType(ReleaseGroup.AlbumType type) {
        addParam(LookupParamType.TYPE, type.toString().toLowerCase());
        return this;
    }

    public ISRCLookupService addReleaseStatus(Release.Status status) {
        addParam(LookupParamType.STATUS, status.toString());
        return this;
    }

}
