package org.musicbrainz.android.api.other;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Flowable;
import retrofit2.adapter.rxjava2.Result;
import org.musicbrainz.android.api.Config;
import org.musicbrainz.android.api.model.xml.ArtistXML;
import org.musicbrainz.android.api.model.xml.IsrcXML;
import org.musicbrainz.android.api.model.xml.Metadata;
import org.musicbrainz.android.api.model.xml.RecordingXML;
import org.musicbrainz.android.api.model.xml.ReleaseGroupXML;
import org.musicbrainz.android.api.model.xml.ReleaseXML;
import org.musicbrainz.android.api.model.xml.UserTagXML;
import org.musicbrainz.android.api.core.BaseWebService;


/**
 * Created by Alex on 30.11.2017.
 */

public class PostWebService extends BaseWebService implements PostWebServiceInterface {

    public static final String CLIENT_QUERY = "client";
    public static final String ACCESS_TOKEN_QUERY = "access_token";

    private Map<String, String> map = new HashMap<>();

    public PostWebService(String client) {
        map.put(CLIENT_QUERY, client);

        if (Config.accessToken != null) {
            digestAuth = false;
            map.put(ACCESS_TOKEN_QUERY, Config.accessToken);
        } else {
            digestAuth = true;
        }
    }

    @Override
    public Flowable<Result<Metadata>> postMetadata(PathType pathType, Metadata metadata) {
        return getXmlRetrofitService().postMetadata(pathType.toString(), metadata, map);
    }

    @Override
    public Flowable<Result<Metadata>> postBarcodes(ReleaseXML... releases) {
        Metadata metadata = new Metadata();
        metadata.addReleases(releases);
        return getXmlRetrofitService().postMetadata(PathType.RELEASE.toString(), metadata, map);
    }

    @Override
    public Flowable<Result<Metadata>> postBarcode(String releaseId, String barcode) {
        return postBarcodes(new ReleaseXML(releaseId, barcode));
    }

    @Override
    public Flowable<Result<Metadata>> postIsrcs(RecordingXML... recordings) {
        Metadata metadata = new Metadata();
        metadata.addRecordings(recordings);
        return getXmlRetrofitService().postMetadata(PathType.RECORDING.toString(), metadata, map);
    }

    @Override
    public Flowable<Result<Metadata>> postIsrcs(String recordingId, String... isrcs) {
        RecordingXML recordingXML = new RecordingXML(recordingId);
        for (String isrc : isrcs) {
            recordingXML.addIsrcs(new IsrcXML(isrc));
        }
        return postIsrcs(recordingXML);
    }

    @Override
    public Flowable<Result<Metadata>> postArtistRating(String artistId, int userRating) {
        Metadata metadata = new Metadata();
        metadata.addArtists(new ArtistXML(artistId, userRating));
        return getXmlRetrofitService().postMetadata(PathType.RATING.toString(), metadata, map);
    }

    @Override
    public Flowable<Result<Metadata>> postRecordingRating(String recordingId, int userRating) {
        Metadata metadata = new Metadata();
        metadata.addRecordings(new RecordingXML(recordingId, userRating));
        return getXmlRetrofitService().postMetadata(PathType.RATING.toString(), metadata, map);
    }

    @Override
    public Flowable<Result<Metadata>>postReleaseGroupRating(String releaseGroupId, int userRating) {
        Metadata metadata = new Metadata();
        metadata.addReleaseGroups(new ReleaseGroupXML(releaseGroupId, userRating));
        return getXmlRetrofitService().postMetadata(PathType.RATING.toString(), metadata, map);
    }

    @Override
    public Flowable<Result<Metadata>> postArtistTags(String artistId, UserTagXML... tags) {
        ArtistXML artistXml = new ArtistXML(artistId);
        artistXml.addUserTags(tags);
        Metadata metadata = new Metadata();
        metadata.addArtists(artistXml);
        return getXmlRetrofitService().postMetadata(PathType.TAG.toString(), metadata, map);
    }

    @Override
    public Flowable<Result<Metadata>> postRecordingTags(String recordingId, UserTagXML... tags) {
        RecordingXML recordingXML = new RecordingXML(recordingId);
        recordingXML.addUserTags(tags);
        Metadata metadata = new Metadata();
        metadata.addRecordings(recordingXML);
        return getXmlRetrofitService().postMetadata(PathType.TAG.toString(), metadata, map);
    }

    @Override
    public Flowable<Result<Metadata>> postReleaseGroupTags(String releaseGroupId, UserTagXML... tags) {
        ReleaseGroupXML releaseGroupXML = new ReleaseGroupXML(releaseGroupId);
        releaseGroupXML.addUserTags(tags);
        Metadata metadata = new Metadata();
        metadata.addReleaseGroups(releaseGroupXML);
        return getXmlRetrofitService().postMetadata(PathType.TAG.toString(), metadata, map);
    }

}
