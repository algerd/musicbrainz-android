package org.musicbrainz.android.adapter.pager;

import android.content.res.Resources;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import org.musicbrainz.android.R;
import org.musicbrainz.android.fragment.TagArtistsFragment;
import org.musicbrainz.android.fragment.TagRecordingsFragment;
import org.musicbrainz.android.fragment.TagReleaseGroupsFragment;


public class TagPagerAdapter extends BaseFragmentPagerAdapter {

    public static final int PAGE_COUNT = 3;
    public final static int TAB_ARTISTS_POS = 0;
    public final static int TAB_RELEASE_GROUPS_POS = 1;
    public final static int TAB_RECORDINGS_POS = 2;

    public TagPagerAdapter(FragmentManager fm, Resources resources) {
        super(PAGE_COUNT, fm, resources);

        tabTitles[TAB_ARTISTS_POS] = R.string.tag_tab_artists;
        tabTitles[TAB_RELEASE_GROUPS_POS] = R.string.tag_tab_releases;
        tabTitles[TAB_RECORDINGS_POS] = R.string.tag_tab_recordings;

        tabIcons[TAB_ARTISTS_POS] = R.drawable.ic_artist_24;
        tabIcons[TAB_RELEASE_GROUPS_POS] = R.drawable.ic_album_24;
        tabIcons[TAB_RECORDINGS_POS] = R.drawable.ic_track_24;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case TAB_ARTISTS_POS:
                return TagArtistsFragment.newInstance();
            case TAB_RELEASE_GROUPS_POS:
                return TagReleaseGroupsFragment.newInstance();
            case TAB_RECORDINGS_POS:
                return TagRecordingsFragment.newInstance();
        }
        return null;
    }

}
