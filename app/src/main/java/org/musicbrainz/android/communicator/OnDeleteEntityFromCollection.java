package org.musicbrainz.android.communicator;

import org.musicbrainz.android.api.model.BaseLookupEntity;

public interface OnDeleteEntityFromCollection {

    void onDelete(BaseLookupEntity entity);

}
