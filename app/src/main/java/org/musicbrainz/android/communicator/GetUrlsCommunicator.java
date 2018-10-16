package org.musicbrainz.android.communicator;

import java.util.List;

import org.musicbrainz.android.api.model.Url;

/**
 * Created by Alex on 20.03.2018.
 */

public interface GetUrlsCommunicator {
    List<Url> getUrls();
}
