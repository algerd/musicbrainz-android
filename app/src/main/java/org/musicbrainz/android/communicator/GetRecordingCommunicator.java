package org.musicbrainz.android.communicator;

import org.musicbrainz.android.api.model.Recording;

/**
 * Created by Alex on 20.03.2018.
 */

public interface GetRecordingCommunicator {
    Recording getRecording();
    String getRecordingMbid();
}
