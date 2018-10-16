package org.musicbrainz.android.util;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

/**
 * Created by Alex on 18.12.2017.
 */

public class ShowUtil {

    public static void showError(Context context, Throwable t) {
        Toast.makeText(context, t.getMessage(), Toast.LENGTH_LONG).show();
        //Log.e(context.getClass().getSimpleName(), "Exception from Retrofit request to musicbrainz.org", t);
    }

    public static void showMessage(Activity activity, final String msg) {
        if (TextUtils.isEmpty(msg)) return;
        activity.runOnUiThread(() -> Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show());
    }

    public static void showToast(Context context, final String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

}
