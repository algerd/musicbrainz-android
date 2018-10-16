package org.musicbrainz.android.util;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ProgressBar;

import com.squareup.picasso.Callback;

import org.musicbrainz.android.functions.Action;

public class PicassoHelper {

    public static Callback createPicassoProgressCallback(@NonNull ProgressBar progressBar) {
        return new Callback() {
            @Override
            public void onSuccess() {
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onError() {
                progressBar.setVisibility(View.GONE);
            }
        };
    }

    public static Callback createPicassoProgressCallback(@NonNull ProgressBar progressBar, Action onError) {
        return new Callback() {
            @Override
            public void onSuccess() {
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onError() {
                progressBar.setVisibility(View.GONE);
                if (onError != null) {
                    onError.run();
                }
            }
        };
    }

}
