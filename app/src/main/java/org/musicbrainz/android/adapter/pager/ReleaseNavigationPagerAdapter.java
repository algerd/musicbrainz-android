package org.musicbrainz.android.adapter.pager;

import android.content.res.Resources;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import org.musicbrainz.android.fragment.LazyFragment;
import org.musicbrainz.android.fragment.ReleaseInfoPagerFragment;
import org.musicbrainz.android.fragment.ReleaseRatingsFragment;
import org.musicbrainz.android.fragment.ReleaseTagsFragment;
import org.musicbrainz.android.fragment.ReleaseTracksFragment;
import org.musicbrainz.android.fragment.ReleasesFragment;


public class ReleaseNavigationPagerAdapter extends BaseFragmentPagerAdapter {

    public static final int PAGE_COUNT = 5;
    public static final int TAB_TRACKS_POS = 0;
    public static final int TAB_INFO_POS = 1;
    public static final int TAB_RELEASES_POS = 2;
    public static final int TAB_RATINGS_POS = 3;
    public static final int TAB_TAGS_POS = 4;

    public ReleaseNavigationPagerAdapter(FragmentManager fm, Resources resources) {
        super(PAGE_COUNT, fm, resources);
    }

    @Override
    public LazyFragment getItem(int position) {
        switch (position) {
            case TAB_TRACKS_POS:
                return ReleaseTracksFragment.newInstance();
            case TAB_INFO_POS:
                return ReleaseInfoPagerFragment.newInstance();
            case TAB_RELEASES_POS:
                return ReleasesFragment.newInstance(ReleasesFragment.ALBUM_TYPE);
            case TAB_RATINGS_POS:
                return ReleaseRatingsFragment.newInstance();
            case TAB_TAGS_POS:
                return ReleaseTagsFragment.newInstance();
        }
        return null;
    }

}
