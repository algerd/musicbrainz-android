package org.musicbrainz.android.api.core;

/**
 * Created by Alex on 06.02.2018.
 */

public interface WebServiceInterface<T> {

    T getRetrofitService();

    T getJsonRetrofitService();

    T getDigestAuthJsonRetrofitService();

    T getXmlRetrofitService();

    T getDigestAuthXmlRetrofitService();

}
