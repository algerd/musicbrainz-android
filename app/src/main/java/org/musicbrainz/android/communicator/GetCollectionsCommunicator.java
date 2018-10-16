package org.musicbrainz.android.communicator;

import java.util.List;

import org.musicbrainz.android.api.model.Collection;

/**
 * Created by Alex on 20.03.2018.
 */

public interface GetCollectionsCommunicator {
    List<Collection> getCollections();
}
