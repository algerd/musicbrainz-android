package org.musicbrainz.android.communicator;

import java.util.List;

import org.musicbrainz.android.api.model.Release;

/**
 * Created by Alex on 20.03.2018.
 */

public interface GetReleasesCommunicator {
    List<Release> getReleases();
}
