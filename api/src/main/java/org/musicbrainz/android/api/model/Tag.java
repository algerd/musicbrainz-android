package org.musicbrainz.android.api.model;

import com.squareup.moshi.Json;

import java.util.List;

import static org.musicbrainz.android.api.model.Tag.TagType.TAG;

/**
 * https://musicbrainz.org/doc/Folksonomy_Tagging
 * Created by Alex on 16.11.2017.
 */

public class Tag {

    public enum TagType {
        GENRE, TAG
    }

    public static class TagSearch extends BaseSearch {
        @Json(name = "tags")
        private List<Tag> tags;

        public List<Tag> getTags() {
            return tags;
        }

        public void setTags(List<Tag> tags) {
            this.tags = tags;
        }
    }

    private TagType tagType = TAG;

    @Json(name = "name")
    private String name;

    @Json(name = "count")
    private int count;

    @Json(name = "score")
    private int score;

    public Tag() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public TagType getTagType() {
        return tagType;
    }

    public void setTagType(TagType tagType) {
        this.tagType = tagType;
    }

    @Override
    public String toString() {
        return name;
    }
}
