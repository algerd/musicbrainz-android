package org.musicbrainz.android.api.search;

import io.reactivex.Flowable;
import retrofit2.adapter.rxjava2.Result;
import org.musicbrainz.android.api.model.Annotation.AnnotationSearch;

/**
 * Created by Alex on 20.11.2017.
 */

public class AnnotationSearchService extends
        BaseSearchService<AnnotationSearch, AnnotationSearchService.AnnotationSearchField> {

    @Override
    public Flowable<Result<AnnotationSearch>> search() {
        return getJsonRetrofitService().searchAnnotation(getParams());
    }

    public enum AnnotationSearchField implements SearchServiceInterface.SearchFieldInterface {
        /**
         * the annotated entity's name or title
         */
        NAME("name"),

        /**
         * the annotated entity's MBID
         */
        ENTITY("entity"),

        /**
         * the annotation's content (includes wiki formatting)
         */
        TEXT("text"),

        /**
         * the annotated entity's entity type
         */
        TYPE("type");

        private final String searchField;

        AnnotationSearchField(String searchField) {
            this.searchField = searchField;
        }

        @Override
        public String toString() {
            return searchField;
        }
    }

}
