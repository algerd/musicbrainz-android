package org.musicbrainz.android.ui;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;

import io.reactivex.disposables.CompositeDisposable;
import org.musicbrainz.android.api.model.ReleaseGroup;
import org.musicbrainz.android.data.NetworkState;
import org.musicbrainz.android.data.ReleaseGroupsDataSource;

import static org.musicbrainz.android.data.ReleaseGroupsDataSource.RELEASE_GROUPE_BROWSE_LIMIT;


public class ReleaseGroupsViewModel extends ViewModel {

    public LiveData<PagedList<ReleaseGroup>> realeseGroupLiveData;
    private CompositeDisposable compositeDisposable;
    private MutableLiveData<ReleaseGroupsDataSource> releaseGroupsDataSourceMutableLiveData;

    public ReleaseGroupsViewModel() {
        compositeDisposable = new CompositeDisposable();
    }

    public void load(String artistMbid, ReleaseGroup.AlbumType albumType) {

        ReleaseGroupsDataSource.Factory factory = new ReleaseGroupsDataSource.Factory(compositeDisposable, artistMbid, albumType);
        PagedList.Config config = new PagedList.Config.Builder()
                .setPageSize(RELEASE_GROUPE_BROWSE_LIMIT)
                //.setInitialLoadSizeHint(PAGE_SIZE)
                .setEnablePlaceholders(false)
                .build();

        realeseGroupLiveData = new LivePagedListBuilder<>(factory, config).build();
        releaseGroupsDataSourceMutableLiveData = factory.getReleaseGroupsDataSourceLiveData();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.dispose();
    }

    public void retry() {
        releaseGroupsDataSourceMutableLiveData.getValue().retry();
    }

    public void refresh() {
        releaseGroupsDataSourceMutableLiveData.getValue().invalidate();
    }

    public LiveData<NetworkState> getNetworkState() {
        return Transformations.switchMap(releaseGroupsDataSourceMutableLiveData, ReleaseGroupsDataSource::getNetworkState);
    }

    public LiveData<NetworkState> getRefreshState() {
        return Transformations.switchMap(releaseGroupsDataSourceMutableLiveData, ReleaseGroupsDataSource::getInitialLoad);
    }

}
