package org.musicbrainz.android.account;

import android.accounts.Account;
import android.os.Parcel;

public class MusicBrainAccount extends Account {

    public static final String ACCOUNT_TYPE = "ru.javafx.musicbrain";
    public static final String ACCESS_TOKEN = "access_token";
    public static final String REFRESH_TOKEN = "refresh_token";
    public static final String EXPIRE_IN = "expire_in";

    public MusicBrainAccount(Parcel in) {
        super(in);
    }

    public MusicBrainAccount(String name) {
        super(name, ACCOUNT_TYPE);
    }

}
