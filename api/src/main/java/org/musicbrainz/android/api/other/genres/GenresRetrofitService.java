package org.musicbrainz.android.api.other.genres;

import io.reactivex.Flowable;
import okhttp3.ResponseBody;
import retrofit2.adapter.rxjava2.Result;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;


public interface GenresRetrofitService {

    String ENDPOINT = "https://raw.githubusercontent.com";

    @GET("/metabrainz/musicbrainz-server/master/entities.json")
    Flowable<Result<ResponseBody>> getEntities();

}
