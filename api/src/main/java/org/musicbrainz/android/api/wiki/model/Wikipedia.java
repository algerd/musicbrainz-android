package org.musicbrainz.android.api.wiki.model;

import com.squareup.moshi.Json;

/**
 * Created by Alex on 29.01.2018.
 */

public class Wikipedia {

    @Json(name = "mobileview")
    private Mobileview mobileview;

    public Wikipedia() {
    }

    public Mobileview getMobileview() {
        return mobileview;
    }

    public void setMobileview(Mobileview mobileview) {
        this.mobileview = mobileview;
    }
}
