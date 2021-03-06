package org.musicbrainz.android.api.core;

import com.burgstaller.okhttp.AuthenticationCacheInterceptor;
import com.burgstaller.okhttp.CachingAuthenticatorDecorator;
import com.burgstaller.okhttp.digest.CachingAuthenticator;
import com.burgstaller.okhttp.digest.DigestAuthenticator;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.moshi.MoshiConverterFactory;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;
import org.musicbrainz.android.api.Config;

import static org.musicbrainz.android.api.Config.USER_AGENT_HEADER;

/**
 * Created by Alex on 05.02.2018.
 */

public class WebService<T> implements WebServiceInterface<T> {

    public static final OkHttpClient httpClient = getHttpClient();
    public static final OkHttpClient digestAuthHttpClient = getDigestAuthHttpClient();


    private static OkHttpClient getHttpClient() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return new OkHttpClient.Builder()
                .addNetworkInterceptor(interceptor)
                .addInterceptor(getHeaderInterceptor())
                .build();
    }

    private static Interceptor getHeaderInterceptor () {
        return chain -> {
            Request request = chain.request().newBuilder().addHeader("User-Agent", USER_AGENT_HEADER).build();
            return chain.proceed(request);
        };
    }

    private static OkHttpClient getDigestAuthHttpClient() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        Map<String, CachingAuthenticator> authCache = new ConcurrentHashMap<>();
        return new OkHttpClient.Builder()
                .authenticator(new CachingAuthenticatorDecorator(new DigestAuthenticator(Config.getCredentials()), authCache))
                .addInterceptor(new AuthenticationCacheInterceptor(authCache))
                .addNetworkInterceptor(interceptor)
                .build();
    }

    private final T retrofitService;
    private final T jsonRetrofitService;
    private final T digestAuthJsonRetrofitService;
    private final T xmlRetrofitService;
    private final T digestAuthXmlRetrofitService;

    public WebService(Class<T> retrofitClass, String url) {
        retrofitService = new Retrofit.Builder().baseUrl(url)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(httpClient)
                .build().create(retrofitClass);

        jsonRetrofitService = new Retrofit.Builder().baseUrl(url)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(MoshiConverterFactory.create())
                .client(httpClient)
                .build().create(retrofitClass);

        digestAuthJsonRetrofitService = new Retrofit.Builder().baseUrl(url)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(MoshiConverterFactory.create())
                .client(digestAuthHttpClient)
                .build().create(retrofitClass);

        xmlRetrofitService = new Retrofit.Builder().baseUrl(url)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .client(httpClient)
                .build().create(retrofitClass);

        digestAuthXmlRetrofitService = new Retrofit.Builder().baseUrl(url)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .client(digestAuthHttpClient)
                .build().create(retrofitClass);
    }

    @Override
    public final T getJsonRetrofitService() {
        return jsonRetrofitService;
    }

    @Override
    public final T getDigestAuthJsonRetrofitService() {
        return digestAuthJsonRetrofitService;
    }

    @Override
    public final T getXmlRetrofitService() {
        return xmlRetrofitService;
    }

    @Override
    public final T getDigestAuthXmlRetrofitService() {
        return digestAuthXmlRetrofitService;
    }

    public T getRetrofitService() {
        return retrofitService;
    }
}
