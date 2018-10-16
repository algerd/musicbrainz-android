package org.musicbrainz.android.adapter.recycler.tracks;

import org.musicbrainz.android.adapter.recycler.expandedRecycler.BaseHeader;

public class Header extends BaseHeader {

    private String title;

    private long length;

    public Header() {
        super();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }
}
