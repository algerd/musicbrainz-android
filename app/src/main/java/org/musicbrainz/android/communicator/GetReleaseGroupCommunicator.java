package org.musicbrainz.android.communicator;

import org.musicbrainz.android.api.model.ReleaseGroup;

/**
 * Created by Alex on 20.03.2018.
 */

public interface GetReleaseGroupCommunicator {
    ReleaseGroup getReleaseGroup();
    String getReleaseGroupMbid();
}
