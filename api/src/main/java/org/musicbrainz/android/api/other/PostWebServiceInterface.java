package org.musicbrainz.android.api.other;

import io.reactivex.Flowable;
import retrofit2.adapter.rxjava2.Result;
import org.musicbrainz.android.api.model.xml.Metadata;
import org.musicbrainz.android.api.model.xml.RecordingXML;
import org.musicbrainz.android.api.model.xml.ReleaseXML;
import org.musicbrainz.android.api.model.xml.UserTagXML;

/**
 * Created by Alex on 30.11.2017.
 */

public interface PostWebServiceInterface {

    Flowable<Result<Metadata>> postMetadata(PathType pathType, Metadata metadata);

    Flowable<Result<Metadata>> postArtistRating(String artistId, int userRating);
    Flowable<Result<Metadata>> postRecordingRating(String recordingId, int userRating);
    Flowable<Result<Metadata>> postReleaseGroupRating(String releaseGroupId, int userRating);

    Flowable<Result<Metadata>> postArtistTags(String artistId, UserTagXML... tags);
    Flowable<Result<Metadata>> postRecordingTags(String recordingId, UserTagXML... tags);
    Flowable<Result<Metadata>> postReleaseGroupTags(String releaseGroupId, UserTagXML... tags);

    //TODO: require tests postBarcodes() and postIsrcs()
    Flowable<Result<Metadata>> postBarcodes(ReleaseXML... releases);
    Flowable<Result<Metadata>> postBarcode(String releaseId, String barcode);
    Flowable<Result<Metadata>> postIsrcs(RecordingXML... recordings);
    Flowable<Result<Metadata>> postIsrcs(String recordingId, String... isrcs);

    enum PathType {

        RATING("rating"),
        TAG("tag"),
        RELEASE("release"),
        RECORDING("recording");

        private final String param;
        PathType(String param) {
            this.param = param;
        }
        @Override
        public String toString() {
            return param;
        }
    }

}
