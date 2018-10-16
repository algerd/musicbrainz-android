package org.musicbrainz.android.api.search;

import io.reactivex.Flowable;
import retrofit2.adapter.rxjava2.Result;
import org.musicbrainz.android.api.model.Tag.TagSearch;

/**
 * Require only digest autorization.
 * unconditional search: Release group search terms with no fields search the RELEASE_GROUP field only.
 *  new TagSearchService().search("rock")
 *  new TagSearchService().search("rock", 1, 10)
 *
 * Created by Alex on 20.11.2017.
 */

public class TagSearchService extends
        BaseSearchService<TagSearch, TagSearchService.TagSearchField> {

    @Override
    public Flowable<Result<TagSearch>> search() {
        digestAuth = true;
        return getJsonRetrofitService().searchTag(getParams());
    }

    public enum TagSearchField implements SearchServiceInterface.SearchFieldInterface {
        /**
         * the tag's text
         */
        TAG("tag");

        private final String searchField;

        TagSearchField(String searchField) {
            this.searchField = searchField;
        }

        @Override
        public String toString() {
            return searchField;
        }
    }

}
