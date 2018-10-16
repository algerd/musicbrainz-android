package org.musicbrainz.android.api.model.xml;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

/**
 * Created by Alex on 03.12.2017.
 */

@Root(name="isrc")
public class IsrcXML {

    @Attribute(name="id")
    private String id;

    public IsrcXML(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
