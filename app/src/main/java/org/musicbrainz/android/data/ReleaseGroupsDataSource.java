package org.musicbrainz.android.data;

import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.PageKeyedDataSource;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;
import org.musicbrainz.android.api.model.ReleaseGroup;

import static org.musicbrainz.android.MusicBrainzApp.api;

public class ReleaseGroupsDataSource extends PageKeyedDataSource<Integer, ReleaseGroup> {

    public static final int RELEASE_GROUPE_BROWSE_LIMIT = 25;

    private CompositeDisposable compositeDisposable;
    private ReleaseGroup.AlbumType albumType;
    private String artistMbid;
    private MutableLiveData<NetworkState> networkState = new MutableLiveData<>();
    private MutableLiveData<NetworkState> initialLoad = new MutableLiveData<>();
    /**
     * Keep Completable reference for the retry event
     */
    private Completable retryCompletable;

    public ReleaseGroupsDataSource(CompositeDisposable compositeDisposable, String artistMbid, ReleaseGroup.AlbumType albumType) {
        this.artistMbid = artistMbid;
        this.albumType = albumType;
        this.compositeDisposable = compositeDisposable;
    }

    public void retry() {
        if (retryCompletable != null) {
            compositeDisposable.add(retryCompletable
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            () -> {
                            },
                            throwable -> {
                                //Timber.e(throwable.getMessage());
                            }));
        }
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams<Integer> params, @NonNull LoadInitialCallback<Integer, ReleaseGroup> callback) {
        // update network states.
        // we also provide an initial getWikidataQ state to the listeners so that the UI can know when the first page is loaded.
        networkState.postValue(NetworkState.LOADING);
        initialLoad.postValue(NetworkState.LOADING);
        compositeDisposable.add(api.getReleaseGroupsByArtistAndAlbumTypes(
                artistMbid,
                releaseGroupBrowse -> {
                    // clear retry since last request succeeded
                    setRetry(null);
                    initialLoad.postValue(NetworkState.LOADED);

                    List<ReleaseGroup> releaseGroups = releaseGroupBrowse.getReleaseGroups();
                    List<ReleaseGroup> rgs = selectReleaseGroups(releaseGroups);

                    callback.onResult(rgs, null,
                            // find offset
                            (releaseGroups.size() == rgs.size() && releaseGroupBrowse.getCount() > RELEASE_GROUPE_BROWSE_LIMIT) ? RELEASE_GROUPE_BROWSE_LIMIT : null);

                    networkState.postValue(NetworkState.LOADED);
                },
                throwable -> {
                    // keep a Completable for future retry
                    setRetry(() -> loadInitial(params, callback));
                    NetworkState error = NetworkState.error(throwable.getMessage());
                    // publish the error
                    networkState.postValue(error);
                    initialLoad.postValue(error);
                },
                RELEASE_GROUPE_BROWSE_LIMIT, 0, albumType));
    }

    @Override
    public void loadBefore(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Integer, ReleaseGroup> callback) {
        // ignored, since we only ever append to our initial getWikidataQ
    }

    @Override
    public void loadAfter(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Integer, ReleaseGroup> callback) {
        // set network value to loading.
        networkState.postValue(NetworkState.LOADING);

        compositeDisposable.add(api.getReleaseGroupsByArtistAndAlbumTypes(
                artistMbid,
                releaseGroupBrowse -> {
                    // clear retry since last request succeeded
                    setRetry(null);
                    initialLoad.postValue(NetworkState.LOADED);

                    List<ReleaseGroup> releaseGroups = releaseGroupBrowse.getReleaseGroups();
                    List<ReleaseGroup> rgs = selectReleaseGroups(releaseGroups);
                    int nextOffset = releaseGroupBrowse.getOffset() + RELEASE_GROUPE_BROWSE_LIMIT;

                    callback.onResult(rgs,
                            (releaseGroups.size() == rgs.size() && releaseGroupBrowse.getCount() > nextOffset) ? nextOffset : null);

                    networkState.postValue(NetworkState.LOADED);
                },
                throwable -> {
                    // keep a Completable for future retry
                    setRetry(() -> loadAfter(params, callback));
                    // publish the error
                    networkState.postValue(NetworkState.error(throwable.getMessage()));
                },
                RELEASE_GROUPE_BROWSE_LIMIT, params.key, albumType));
    }

    private List<ReleaseGroup> selectReleaseGroups(List<ReleaseGroup> releaseGroups) {
        List<ReleaseGroup> rgs = new ArrayList<>();
        if (albumType.equals(ReleaseGroup.PrimaryType.ALBUM)) {
            for (ReleaseGroup rg : releaseGroups) {
                if (rg.getSecondaryTypes() == null || rg.getSecondaryTypes().isEmpty()) {
                    rgs.add(rg);
                }
            }
        } else {
            rgs.addAll(releaseGroups);
        }
        Collections.sort(rgs, (rg1, rg2) -> rg1.getYear() - rg2.getYear());
        return rgs;
    }

    @NonNull
    public MutableLiveData<NetworkState> getNetworkState() {
        return networkState;
    }

    @NonNull
    public MutableLiveData<NetworkState> getInitialLoad() {
        return initialLoad;
    }

    private void setRetry(final Action action) {
        this.retryCompletable = action == null ? null : Completable.fromAction(action);
    }

    public static class Factory extends PageKeyedDataSource.Factory<Integer, ReleaseGroup> {

        private CompositeDisposable compositeDisposable;
        private ReleaseGroup.AlbumType albumType;
        private String artistMbid;
        private MutableLiveData<ReleaseGroupsDataSource> releaseGroupsDataSourceLiveData = new MutableLiveData<>();

        public Factory(CompositeDisposable compositeDisposable, String artistMbid, ReleaseGroup.AlbumType albumType) {
            this.compositeDisposable = compositeDisposable;
            this.artistMbid = artistMbid;
            this.albumType = albumType;
        }

        @Override
        public PageKeyedDataSource<Integer, ReleaseGroup> create() {
            ReleaseGroupsDataSource releaseGroupsDataSource = new ReleaseGroupsDataSource(compositeDisposable, artistMbid, albumType);
            releaseGroupsDataSourceLiveData.postValue(releaseGroupsDataSource);
            return releaseGroupsDataSource;
        }

        @NonNull
        public MutableLiveData<ReleaseGroupsDataSource> getReleaseGroupsDataSourceLiveData() {
            return releaseGroupsDataSourceLiveData;
        }

    }
}
