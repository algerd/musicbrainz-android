package org.musicbrainz.android.ui;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;

import io.reactivex.disposables.CompositeDisposable;
import org.musicbrainz.android.api.browse.ReleaseBrowseService;
import org.musicbrainz.android.api.model.Release;
import org.musicbrainz.android.data.NetworkState;
import org.musicbrainz.android.data.ReleasesDataSource;

import static org.musicbrainz.android.data.ReleasesDataSource.RELEASE_BROWSE_LIMIT;


public class ReleasesViewModel extends ViewModel {

    public LiveData<PagedList<Release>> realeseLiveData;
    private CompositeDisposable compositeDisposable;
    private MutableLiveData<ReleasesDataSource> releasesDataSourceMutableLiveData;

    public ReleasesViewModel() {
        compositeDisposable = new CompositeDisposable();
    }

    public void load(String mbid, ReleaseBrowseService.ReleaseBrowseEntityType type) {

        ReleasesDataSource.Factory factory = new ReleasesDataSource.Factory(compositeDisposable, mbid, type);
        PagedList.Config config = new PagedList.Config.Builder()
                .setPageSize(RELEASE_BROWSE_LIMIT)
                //.setInitialLoadSizeHint(PAGE_SIZE)
                .setEnablePlaceholders(false)
                .build();

        realeseLiveData = new LivePagedListBuilder<>(factory, config).build();
        releasesDataSourceMutableLiveData = factory.getReleasesDataSourceLiveData();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.dispose();
    }

    public void retry() {
        releasesDataSourceMutableLiveData.getValue().retry();
    }

    public void refresh() {
        releasesDataSourceMutableLiveData.getValue().invalidate();
    }

    public LiveData<NetworkState> getNetworkState() {
        return Transformations.switchMap(releasesDataSourceMutableLiveData, ReleasesDataSource::getNetworkState);
    }

    public LiveData<NetworkState> getRefreshState() {
        return Transformations.switchMap(releasesDataSourceMutableLiveData, ReleasesDataSource::getInitialLoad);
    }

}
