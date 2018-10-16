package org.musicbrainz.android.api.lookup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.musicbrainz.android.api.Config;
import org.musicbrainz.android.api.model.BaseLookupEntity;
import org.musicbrainz.android.api.core.BaseWebService;
import org.musicbrainz.android.api.core.ApiUtils;

import static org.musicbrainz.android.api.Config.AUTHORIZATED_INCS;
import static org.musicbrainz.android.api.Config.FORMAT_JSON;
import static org.musicbrainz.android.api.lookup.LookupServiceInterface.LookupParamType.ACCESS_TOKEN;
import static org.musicbrainz.android.api.lookup.LookupServiceInterface.LookupParamType.FORMAT;
import static org.musicbrainz.android.api.lookup.LookupServiceInterface.LookupParamType.INC;

/**
 * Created by Alex on 16.11.2017.
 */

public abstract class BaseLookupService <R extends BaseLookupEntity, P extends Enum<P> & LookupServiceInterface.IncTypeInterface>
        extends BaseWebService
        implements LookupServiceInterface <R, P> {

    private final String mbid;
    private final List<IncTypeInterface> incs = new ArrayList<>();
    private final Map<LookupParamType, String> params = new HashMap<>();

    public BaseLookupService(String mbid) {
        super();
        this.mbid = mbid;
        params.put(FORMAT, FORMAT_JSON);
        if (Config.accessToken != null) {
            params.put(ACCESS_TOKEN, Config.accessToken);
        }
    }

    protected LookupServiceInterface<R, P> addParam(LookupParamType param, String value) {
        params.put(param, value);
        return this;
    }

    @Override
    public LookupServiceInterface<R, P> addIncs(P... incTypes) {
        incs.addAll(Arrays.asList(incTypes));
        if (Config.accessToken == null) {
            for (P incType : incTypes) {
                for (String authType : AUTHORIZATED_INCS) {
                    if (incType.toString().equals(authType)) {
                        digestAuth = true;
                    }
                }
            }
        }
        return this;
    }

    @Override
    public LookupServiceInterface<R, P> addRels(RelsType... relTypes) {
        incs.addAll(Arrays.asList(relTypes));
        return this;
    }

    protected Map<String, String> getParams() {
        Map<String, String> map = new HashMap<>();
        Iterator<LookupParamType> iter = params.keySet().iterator();
        while(iter.hasNext()) {
            LookupParamType key = iter.next();
            map.put(key.toString(), params.get(key));
        }
        String inc = ApiUtils.getStringFromList(incs, "+");
        incs.clear();
        if (!inc.equals("")) {
            map.put(INC.toString(), inc);
        }
        return map;
    }

    protected String getMbid() {
        return mbid;
    }

}
