package org.musicbrainz.android.activity;

import android.os.Bundle;

import org.musicbrainz.android.R;

public class AboutActivity extends BaseOptionsMenuActivity {

    @Override
    protected int getContentLayout() {
        return R.layout.activity_about;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

}
