package org.musicbrainz.android.communicator;

import org.musicbrainz.android.R;

/**
 * Created by Alex on 20.03.2018.
 */

public interface ShowFloatingActionButtonCommunicator {

    void showFloatingActionButton(boolean visible, FloatingButtonType floatingButtonType);

    enum FloatingButtonType {
        ADD_TO_COLLECTION(R.drawable.ic_collection_add_24),
        EDIT_COLLECTION(R.drawable.ic_collection_edit_24);

        private int imgResource;

        FloatingButtonType(int imgResource) {
            this.imgResource = imgResource;
        }

        public int getImgResource() {
            return imgResource;
        }
    }

}
