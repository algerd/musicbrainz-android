package org.musicbrainz.android.account;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class MusicBrainAuthenticatorService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return new MusicBrainAuthenticator(this).getIBinder();
    }
}
