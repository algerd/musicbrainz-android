package org.musicbrainz.android.communicator;

import org.musicbrainz.android.api.model.Collection;

/**
 * Created by Alex on 20.03.2018.
 */

public interface GetCollectionCommunicator {
    Collection getCollection();

    String getCollectionMbid();
}
