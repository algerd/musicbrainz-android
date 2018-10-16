package org.musicbrainz.android.functions;

import io.reactivex.disposables.Disposable;

/**
 * Created by Alex on 18.12.2017.
 */

public interface DisposableAction {
    Disposable run();
}
