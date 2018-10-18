package org.musicbrainz.android.activity;

import android.view.Menu;
import android.view.MenuItem;

import org.musicbrainz.android.R;
import org.musicbrainz.android.intent.ActivityFactory;
import org.musicbrainz.android.intent.zxing.IntentIntegrator;

import static org.musicbrainz.android.MusicBrainzApp.oauth;

public abstract class BaseOptionsMenuActivity extends BaseActivity {

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_user_profile:
                if (oauth.hasAccount()) {
                    ActivityFactory.startUserActivity2(this, oauth.getName());
                } else {
                    ActivityFactory.startLoginActivity(this);
                }
                return true;

            case R.id.action_scan_barcode:
                IntentIntegrator.initiateScan(this, getString(R.string.zx_title), getString(R.string.zx_message),
                        getString(R.string.zx_pos), getString(R.string.zx_neg), IntentIntegrator.PRODUCT_CODE_TYPES);
                return true;
            /*
            case R.id.action_settings:
                ActivityFactory.startSettingsActivity(this);
                return true;
            */
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
