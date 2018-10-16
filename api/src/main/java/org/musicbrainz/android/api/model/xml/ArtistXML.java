package org.musicbrainz.android.api.model.xml;

import org.simpleframework.xml.Root;

/**
 * Created by Alex on 01.12.2017.
 */

@Root(name="artist")
public class ArtistXML extends BaseTagRatingEntityXML {
    public ArtistXML(String id) {
        super(id);
    }

    public ArtistXML(String id, int userRating) {
        super(id, userRating);
    }
}

