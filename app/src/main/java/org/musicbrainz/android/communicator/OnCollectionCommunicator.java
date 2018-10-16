package org.musicbrainz.android.communicator;

import org.musicbrainz.android.api.model.Collection;

/**
 * Created by Alex on 22.03.2018.
 */

public interface OnCollectionCommunicator {
    void onCollection(Collection collection);
}
