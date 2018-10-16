package org.musicbrainz.android.adapter.pager;

import android.content.res.Resources;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import org.musicbrainz.android.fragment.RecordingInfoPagerFragment;
import org.musicbrainz.android.fragment.RecordingLyricsFragment;
import org.musicbrainz.android.fragment.RecordingRatingsFragment;
import org.musicbrainz.android.fragment.RecordingTagsFragment;
import org.musicbrainz.android.fragment.ReleasesFragment;


public class RecordingNavigationPagerAdapter extends BaseFragmentPagerAdapter {

    public static final int PAGE_COUNT = 5;
    public static final int TAB_LYRICS_POS = 0;
    public static final int TAB_INFO_POS = 1;
    public static final int TAB_RELEASES_POS = 2;
    public static final int TAB_RATINGS_POS = 3;
    public static final int TAB_TAGS_POS = 4;

    public RecordingNavigationPagerAdapter(FragmentManager fm, Resources resources) {
        super(PAGE_COUNT, fm, resources);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case TAB_LYRICS_POS:
                return RecordingLyricsFragment.newInstance();
            case TAB_INFO_POS:
                return RecordingInfoPagerFragment.newInstance();
            case TAB_RELEASES_POS:
                return ReleasesFragment.newInstance(ReleasesFragment.RECORDING_TYPE);
            case TAB_RATINGS_POS:
                return RecordingRatingsFragment.newInstance();
            case TAB_TAGS_POS:
                return RecordingTagsFragment.newInstance();
        }
        return null;
    }

}
