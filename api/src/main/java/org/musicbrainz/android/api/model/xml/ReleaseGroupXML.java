package org.musicbrainz.android.api.model.xml;

/**
 * Created by Alex on 01.12.2017.
 */

import org.simpleframework.xml.Root;


@Root(name="release-group")
public class ReleaseGroupXML extends BaseTagRatingEntityXML {
    public ReleaseGroupXML(String id) {
        super(id);
    }

    public ReleaseGroupXML(String id, int userRating) {
        super(id, userRating);
    }
}
