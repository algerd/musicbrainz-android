package org.musicbrainz.android.api.site;

import java.util.List;
import java.util.Map;

import io.reactivex.Flowable;
import retrofit2.adapter.rxjava2.Result;
import org.musicbrainz.android.api.model.Tag;

public interface TagServiceInterface {

    enum TagType {

        ARTIST("artist"),
        RELEASE_GROUP("release-group"),
        RECORDING("recording");

        private final String type;

        TagType(String type) {
            this.type = type;
        }

        @Override
        public String toString() {
            return type;
        }
    }

    enum UserTagType {

        ARTISTS("Artists"),
        RELEASE_GROUPS("Release Groups"),
        RECORDINGS("Recordings");

        private final String type;

        UserTagType(String type) {
            this.type = type;
        }

        @Override
        public String toString() {
            return type;
        }
    }

    Flowable<Result<Tag.TagSearch>> getUserTags(String username);

    Flowable<Result<Map<UserTagType, List<TagEntity>>>> getUserTagEntities(String username, String tag);

    Flowable<Result<Integer>> getCountPage(String tag, TagType tagType);

    Flowable<Result<TagEntity.Page>> getTagEntities(String tag, TagType tagType, int page, boolean findPageCount);

    Flowable<Result<TagEntity.Page>> getTagEntities(String tag, TagType tagType, int page);
}
