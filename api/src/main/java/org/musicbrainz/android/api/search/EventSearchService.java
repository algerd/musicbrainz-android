package org.musicbrainz.android.api.search;

import io.reactivex.Flowable;
import retrofit2.adapter.rxjava2.Result;
import org.musicbrainz.android.api.model.Event;
import org.musicbrainz.android.api.model.Event.EventSearch;

import static org.musicbrainz.android.api.search.EventSearchService.EventSearchField.TYPE;


/**
 * unconditional search: Event search terms with no fields specified search the EVENT, SORT_NAME and ALIAS fields.
 *   new EventSearchService().search("rock")
 *   new EventSearchService().search("rock", 2, 10)
 *
 * Created by Alex on 20.11.2017.
 */

public class EventSearchService extends
        BaseSearchService<EventSearch, EventSearchService.EventSearchField> {

    @Override
    public Flowable<Result<EventSearch>> search() {
        return getJsonRetrofitService().searchEvent(getParams());
    }

    public EventSearchService addType(Event.EventType type) {
        add(TYPE, type.toString());
        return this;
    }

    @Override
    public EventSearchService add(LuceneBuilder.Operator operator) {
        super.add(operator);
        return this;
    }

    //TODO: add search fields
    public enum EventSearchField implements SearchServiceInterface.SearchFieldInterface {
        ALIAS("alias"),
        EVENT("event"),
        SORT_NAME("sortname"),
        TAG("tag"),
        TYPE("type");

        private final String searchField;

        EventSearchField(String searchField) {
            this.searchField = searchField;
        }

        @Override
        public String toString() {
            return searchField;
        }
    }

}
