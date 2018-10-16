package org.musicbrainz.android.activity;

import android.os.Bundle;

import org.musicbrainz.android.R;
import org.musicbrainz.android.fragment.SettingsFragment;

public class SettingsActivity extends BaseOptionsMenuActivity {

    @Override
    protected int getContentLayout() {
        return R.layout.activity_settings;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction()
                .replace(R.id.settings_fragment, new SettingsFragment())
                .commit();
    }
}
