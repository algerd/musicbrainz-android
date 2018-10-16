package org.musicbrainz.android.communicator;

import org.musicbrainz.android.api.model.Collection;

/**
 * Created by Alex on 22.03.2018.
 */

public interface OnDeleteCollectionCommunicator {
    void onDeleteCollection(Collection deletedCollection);
}
