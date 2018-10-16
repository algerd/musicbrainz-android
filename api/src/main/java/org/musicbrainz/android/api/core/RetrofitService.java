package org.musicbrainz.android.api.core;

import java.util.Map;

import io.reactivex.Flowable;
import retrofit2.adapter.rxjava2.Result;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;
import org.musicbrainz.android.api.model.Annotation;
import org.musicbrainz.android.api.model.Area;
import org.musicbrainz.android.api.model.Artist;
import org.musicbrainz.android.api.model.CDStub;
import org.musicbrainz.android.api.model.Collection;
import org.musicbrainz.android.api.model.Disc;
import org.musicbrainz.android.api.model.Event;
import org.musicbrainz.android.api.model.ISRC;
import org.musicbrainz.android.api.model.ISWC;
import org.musicbrainz.android.api.model.Instrument;
import org.musicbrainz.android.api.model.Label;
import org.musicbrainz.android.api.model.Place;
import org.musicbrainz.android.api.model.Recording;
import org.musicbrainz.android.api.model.Release;
import org.musicbrainz.android.api.model.ReleaseGroup;
import org.musicbrainz.android.api.model.Series;
import org.musicbrainz.android.api.model.Tag;
import org.musicbrainz.android.api.model.Url;
import org.musicbrainz.android.api.model.Work;
import org.musicbrainz.android.api.model.xml.Metadata;

import static org.musicbrainz.android.api.Config.ANNOTATION_QUERY;
import static org.musicbrainz.android.api.Config.AREA_QUERY;
import static org.musicbrainz.android.api.Config.ARTIST_QUERY;
import static org.musicbrainz.android.api.Config.CDSTUB_QUERY;
import static org.musicbrainz.android.api.Config.COLLECTION_QUERY;
import static org.musicbrainz.android.api.Config.DISCID_QUERY;
import static org.musicbrainz.android.api.Config.EVENT_QUERY;
import static org.musicbrainz.android.api.Config.INSTRUMENT_QUERY;
import static org.musicbrainz.android.api.Config.ISRC_QUERY;
import static org.musicbrainz.android.api.Config.ISWC_QUERY;
import static org.musicbrainz.android.api.Config.LABEL_QUERY;
import static org.musicbrainz.android.api.Config.PLACE_QUERY;
import static org.musicbrainz.android.api.Config.RECORDING_QUERY;
import static org.musicbrainz.android.api.Config.RELEASE_GROUP_QUERY;
import static org.musicbrainz.android.api.Config.RELEASE_QUERY;
import static org.musicbrainz.android.api.Config.SERIES_QUERY;
import static org.musicbrainz.android.api.Config.TAG_QUERY;
import static org.musicbrainz.android.api.Config.URL_QUERY;
import static org.musicbrainz.android.api.Config.WEB_SERVICE_PREFIX;
import static org.musicbrainz.android.api.Config.WORK_QUERY;

/**
 * Created by Alex on 16.11.2017.
 */
public interface RetrofitService {

    @PUT(COLLECTION_QUERY + "/{collId}/{collType}/{ids}")
    Flowable<Result<Metadata>> putCollection(
            @Path("collId") String collId,
            @Path("collType") String collType,
            @Path("ids") String ids,
            @QueryMap Map<String, String> params);

    @DELETE(COLLECTION_QUERY + "/{collId}/{collType}/{ids}")
    Flowable<Result<Metadata>> deleteCollection(
            @Path("collId") String collId,
            @Path("collType") String collType,
            @Path("ids") String ids,
            @QueryMap Map<String, String> params);

    @POST(WEB_SERVICE_PREFIX + "{path}")
    Flowable<Result<Metadata>> postMetadata(@Path("path") String path, @Body Metadata metadata, @QueryMap Map<String, String> params);

    @GET(WEB_SERVICE_PREFIX + "{path}")
    Flowable<Result<Metadata>> browseMetadata(@Path("path") String path, @QueryMap Map<String, String> params);

    @GET(ANNOTATION_QUERY)
    Flowable<Result<Annotation.AnnotationSearch>> searchAnnotation(@QueryMap Map<String, String> params);

    @GET(AREA_QUERY)
    Flowable<Result<Area.AreaSearch>> searchArea(@QueryMap Map<String, String> params);

    @GET(AREA_QUERY)
    Flowable<Result<Area.AreaBrowse>> browseArea(@QueryMap Map<String, String> params);

    @GET(AREA_QUERY + "/{mbid}")
    Flowable<Result<Area>> lookupArea(@Path("mbid") String mbid, @QueryMap Map<String, String> params);

    @GET(ARTIST_QUERY)
    Flowable<Result<Artist.ArtistSearch>> searchArtist(@QueryMap Map<String, String> params);

    @GET(ARTIST_QUERY)
    Flowable<Result<Artist.ArtistBrowse>> browseArtist(@QueryMap Map<String, String> params);

    @GET(ARTIST_QUERY + "/{mbid}")
    Flowable<Result<Artist>> lookupArtist(@Path("mbid") String mbid, @QueryMap Map<String, String> params);

    @GET(CDSTUB_QUERY)
    Flowable<Result<CDStub.CDStubSearch>> searchCDStub(@QueryMap Map<String, String> params);

    @GET(COLLECTION_QUERY)
    Flowable<Result<Collection.CollectionBrowse>> browseCollection(@QueryMap Map<String, String> params);

    @GET(COLLECTION_QUERY + "/{mbid}")
    Flowable<Result<Collection>> lookupCollection(@Path("mbid") String mbid, @QueryMap Map<String, String> params);

    @GET(DISCID_QUERY + "/{mbid}")
    Flowable<Result<Disc>> lookupDisc(@Path("mbid") String mbid, @QueryMap Map<String, String> params);

    @GET(EVENT_QUERY)
    Flowable<Result<Event.EventBrowse>> browseEvent(@QueryMap Map<String, String> params);

    @GET(EVENT_QUERY)
    Flowable<Result<Event.EventSearch>> searchEvent(@QueryMap Map<String, String> params);

    @GET(EVENT_QUERY + "/{mbid}")
    Flowable<Result<Event>> lookupEvent(@Path("mbid") String mbid, @QueryMap Map<String, String> params);

    @GET(INSTRUMENT_QUERY)
    Flowable<Result<Instrument.InstrumentSearch>> searchInstrument(@QueryMap Map<String, String> params);

    @GET(LABEL_QUERY)
    Flowable<Result<Label.LabelBrowse>> browseLabel(@QueryMap Map<String, String> params);

    @GET(LABEL_QUERY)
    Flowable<Result<Label.LabelSearch>> searchLabel(@QueryMap Map<String, String> params);

    @GET(LABEL_QUERY + "/{mbid}")
    Flowable<Result<Label>> lookupLabel(@Path("mbid") String mbid, @QueryMap Map<String, String> params);

    @GET(INSTRUMENT_QUERY + "/{mbid}")
    Flowable<Result<Instrument>> lookupInstrument(@Path("mbid") String mbid, @QueryMap Map<String, String> params);

    @GET(ISRC_QUERY + "/{mbid}")
    Flowable<Result<ISRC>> lookupISRC(@Path("mbid") String mbid, @QueryMap Map<String, String> params);

    @GET(ISWC_QUERY + "/{mbid}")
    Flowable<Result<ISWC>> lookupISWC(@Path("mbid") String mbid, @QueryMap Map<String, String> params);

    @GET(PLACE_QUERY)
    Flowable<Result<Place.PlaceBrowse>> browsePlace(@QueryMap Map<String, String> params);

    @GET(PLACE_QUERY)
    Flowable<Result<Place.PlaceSearch>> searchPlace(@QueryMap Map<String, String> params);

    @GET(PLACE_QUERY + "/{mbid}")
    Flowable<Result<Place>> lookupPlace(@Path("mbid") String mbid, @QueryMap Map<String, String> params);

    @GET(RECORDING_QUERY)
    Flowable<Result<Recording.RecordingBrowse>> browseRecording(@QueryMap Map<String, String> params);

    @GET(RECORDING_QUERY)
    Flowable<Result<Recording.RecordingSearch>> searchRecording(@QueryMap Map<String, String> params);

    @GET(RECORDING_QUERY + "/{mbid}")
    Flowable<Result<Recording>> lookupRecording(@Path("mbid") String mbid, @QueryMap Map<String, String> params);

    @GET(RELEASE_QUERY)
    Flowable<Result<Release.ReleaseBrowse>> browseRelease(@QueryMap Map<String, String> params);

    @GET(RELEASE_QUERY)
    Flowable<Result<Release.ReleaseSearch>> searchRelease(@QueryMap Map<String, String> params);

    @GET(RELEASE_QUERY + "/{mbid}")
    Flowable<Result<Release>> lookupRelease(@Path("mbid") String mbid, @QueryMap Map<String, String> params);

    @GET(RELEASE_GROUP_QUERY)
    Flowable<Result<ReleaseGroup.ReleaseGroupBrowse>> browseReleaseGroup(@QueryMap Map<String, String> params);

    @GET(RELEASE_GROUP_QUERY)
    Flowable<Result<ReleaseGroup.ReleaseGroupSearch>> searchReleaseGroup(@QueryMap Map<String, String> params);

    @GET(RELEASE_GROUP_QUERY + "/{mbid}")
    Flowable<Result<ReleaseGroup>> lookupReleaseGroup(@Path("mbid") String mbid, @QueryMap Map<String, String> params);

    @GET(SERIES_QUERY)
    Flowable<Result<Series.SeriesBrowse>> browseSeries(@QueryMap Map<String, String> params);

    @GET(SERIES_QUERY)
    Flowable<Result<Series.SeriesSearch>> searchSeries(@QueryMap Map<String, String> params);

    @GET(SERIES_QUERY + "/{mbid}")
    Flowable<Result<Series>> lookupSeries(@Path("mbid") String mbid, @QueryMap Map<String, String> params);

    @GET(TAG_QUERY)
    Flowable<Result<Tag.TagSearch>> searchTag(@QueryMap Map<String, String> params);

    @GET(URL_QUERY)
    Flowable<Result<Url>> browseUrl(@QueryMap Map<String, String> params);

    @GET(URL_QUERY + "/{mbid}")
    Flowable<Result<Url>> lookupUrl(@Path("mbid") String mbid, @QueryMap Map<String, String> params);

    @GET(WORK_QUERY)
    Flowable<Result<Work.WorkBrowse>> browseWork(@QueryMap Map<String, String> params);

    @GET(WORK_QUERY)
    Flowable<Result<Work.WorkSearch>> searchWork(@QueryMap Map<String, String> params);

    @GET(WORK_QUERY + "/{mbid}")
    Flowable<Result<Work>> lookupWork(@Path("mbid") String mbid, @QueryMap Map<String, String> params);

}
