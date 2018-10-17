package org.musicbrainz.android.account;

import android.accounts.Account;
import android.os.Parcel;

public class MusicBrainzAccount extends Account {

    public static final String ACCOUNT_TYPE = "ru.javafx.musicbrain";
    public static final String ACCESS_TOKEN = "access_token";
    public static final String REFRESH_TOKEN = "refresh_token";
    public static final String EXPIRE_IN = "expire_in";

    public MusicBrainzAccount(Parcel in) {
        super(in);
    }

    public MusicBrainzAccount(String name) {
        super(name, ACCOUNT_TYPE);
    }

}
