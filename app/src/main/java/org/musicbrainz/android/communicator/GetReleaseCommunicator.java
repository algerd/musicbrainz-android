package org.musicbrainz.android.communicator;

import org.musicbrainz.android.api.model.Release;

/**
 * Created by Alex on 20.03.2018.
 */

public interface GetReleaseCommunicator {
    Release getRelease();
    String getReleaseMbid();
}
