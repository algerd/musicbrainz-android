package org.musicbrainz.android.api.model;

import com.squareup.moshi.Json;

/**
 * Created by Alex on 21.11.2017.
 */

public class ReleaseEvent {

    //"yyyy-mm-dd"
    @Json(name = "date")
    private String date;

    @Json(name = "area")
    private Area area;

    public ReleaseEvent() {
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Area getArea() {
        return area;
    }

    public void setArea(Area area) {
        this.area = area;
    }
}
