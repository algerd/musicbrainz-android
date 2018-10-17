package org.musicbrainz.android.account;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class MusicBrainzAuthenticatorService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return new MusicBrainzAuthenticator(this).getIBinder();
    }
}
