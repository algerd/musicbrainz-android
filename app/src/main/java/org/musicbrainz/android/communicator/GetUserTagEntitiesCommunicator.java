package org.musicbrainz.android.communicator;

import java.util.List;

import org.musicbrainz.android.api.site.TagEntity;
import org.musicbrainz.android.api.site.TagServiceInterface;

/**
 * Created by Alex on 20.03.2018.
 */

public interface GetUserTagEntitiesCommunicator {

    List<TagEntity> getEntities(TagServiceInterface.UserTagType userTagType);

}
