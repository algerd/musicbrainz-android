package org.musicbrainz.android.communicator;

import android.widget.EditText;

/**
 * Created by Alex on 22.03.2018.
 */

public interface OnCreateCollectionCommunicator {
    void onCreateCollection(String name, int type, String description, int publ, EditText editText);
}
