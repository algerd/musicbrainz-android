package org.musicbrainz.android.api.search;

import io.reactivex.Flowable;
import retrofit2.adapter.rxjava2.Result;
import org.musicbrainz.android.api.model.Place;
import org.musicbrainz.android.api.model.Place.PlaceSearch;

import static org.musicbrainz.android.api.search.PlaceSearchService.PlaceSearchField.TYPE;

/**
 * unconditional search: Place search terms with no fields specified search the PLACE, ALIAS, ADDRESS and AREA fields.
 *  new PlaceSearchService().search("Street")
 *  new PlaceSearchService().search("Street", 2, 10)
 *
 * Created by Alex on 20.11.2017.
 */

public class PlaceSearchService extends
        BaseSearchService<PlaceSearch, PlaceSearchService.PlaceSearchField> {

    @Override
    public Flowable<Result<PlaceSearch>> search() {
        return getJsonRetrofitService().searchPlace(getParams());
    }

    public PlaceSearchService addType(Place.PlaceType type) {
        add(TYPE, type.toString());
        return this;
    }

    @Override
    public PlaceSearchService add(LuceneBuilder.Operator operator) {
        super.add(operator);
        return this;
    }

    public enum PlaceSearchField implements SearchServiceInterface.SearchFieldInterface {
        /**
         * the place ID
         */
        PID("pid"),

        /**
         * the address of this place
         */
        ADDRESS("address"),

        /**
         * the aliases/misspellings for this area
         */
        ALIAS("alias"),

        /**
         * area name
         */
        AREA("area"),

        /**
         * place begin date
         */
        BEGIN("begin"),

        /**
         * disambiguation comment
         */
        COMMENT("comment"),

        /**
         * place end date
         */
        END("end"),

        /**
         * place ended
         */
        ENDED("ended"),

        /**
         * place latitude
         */
        LAT("lat"),

        /**
         * place longitude
         */
        LONG("long"),

        /**
         * the places type
         */
        TYPE("type");

        private final String searchField;

        PlaceSearchField(String searchField) {
            this.searchField = searchField;
        }

        @Override
        public String toString() {
            return searchField;
        }
    }

}
