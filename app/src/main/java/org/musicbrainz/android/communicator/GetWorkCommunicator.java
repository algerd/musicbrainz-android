package org.musicbrainz.android.communicator;

import org.musicbrainz.android.api.model.Work;

/**
 * Created by Alex on 20.03.2018.
 */

public interface GetWorkCommunicator {
    Work getWork();

    String getWorkMbid();
}
