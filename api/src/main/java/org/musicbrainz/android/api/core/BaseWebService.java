package org.musicbrainz.android.api.core;

import static org.musicbrainz.android.api.Config.WEB_SERVICE;

/**
 * Created by Alex on 15.11.2017.
 */

public abstract class BaseWebService {

    private static final WebServiceInterface<RetrofitService> webService =
            new WebService(RetrofitService.class, WEB_SERVICE);

    protected boolean digestAuth = false;

    public RetrofitService getJsonRetrofitService() {
        return (!digestAuth) ? webService.getJsonRetrofitService()
                : webService.getDigestAuthJsonRetrofitService();
    }

    public RetrofitService getXmlRetrofitService() {
        return (!digestAuth) ? webService.getXmlRetrofitService()
                : webService.getDigestAuthXmlRetrofitService();
    }

    public boolean isDigestAuth() {
        return digestAuth;
    }

    public void setDigestAuth(boolean digestAuth) {
        this.digestAuth = digestAuth;
    }

}
