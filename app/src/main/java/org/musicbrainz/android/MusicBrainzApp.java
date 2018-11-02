package org.musicbrainz.android;

import android.accounts.AccountManager;
import android.app.Application;
import android.content.pm.PackageManager;

import org.musicbrainz.android.account.OAuth;
import org.musicbrainz.android.api.Api;
import org.musicbrainz.android.account.Preferences;
import org.musicbrainz.android.api.Config;

/**
 * Created by Alex on 18.12.2017.
 */

public class MusicBrainzApp extends Application {

    public static final String SUPPORT_MAIL = "algerd75@mail.ru";

    public static OAuth oauth;
    public static Api api;

    private static MusicBrainzApp instance;
    private static Preferences preferences;

    public void onCreate() {
        super.onCreate();
        instance = this;
        Config.setUserAgentHeader(getPackage() + "/" + getVersion() + " (" + SUPPORT_MAIL + ")");

        oauth = new OAuth(AccountManager.get(this));
        api = new Api(oauth);
        preferences = new Preferences();
    }

    public static MusicBrainzApp getContext() {
        return instance;
    }

    public static Preferences getPreferences() {
        return preferences;
    }

    public static String getVersion() {
        try {
            return instance.getPackageManager().getPackageInfo(instance.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return "unknown";
        }
    }

    public static String getPackage() {
        try {
            return instance.getPackageManager().getPackageInfo(instance.getPackageName(), 0).packageName;
        } catch (PackageManager.NameNotFoundException e) {
            return "unknown";
        }
    }

}
