package org.musicbrainz.android.adapter.pager;

import android.content.res.Resources;
import android.support.v4.app.FragmentManager;

import org.musicbrainz.android.fragment.ArtistInfoPagerFragment;
import org.musicbrainz.android.fragment.ArtistRatingsFragment;
import org.musicbrainz.android.fragment.EditTagsPagerFragment;
import org.musicbrainz.android.fragment.LazyFragment;
import org.musicbrainz.android.fragment.ReleaseGroupsPagerFragment;

import static org.musicbrainz.android.fragment.EditTagsPagerFragment.TagsPagerType.ARTIST;


public class ArtistNavigationPagerAdapter extends BaseFragmentPagerAdapter {

    public static final int PAGE_COUNT = 4;
    public static final int TAB_RELEASES_POS = 0;
    public static final int TAB_INFO_POS = 1;
    public static final int TAB_RATINGS_POS = 2;
    public static final int TAB_TAGS_POS = 3;

    public ArtistNavigationPagerAdapter(FragmentManager fm, Resources resources) {
        super(PAGE_COUNT, fm, resources);
    }

    @Override
    public LazyFragment getItem(int position) {
        switch (position) {
            case TAB_RELEASES_POS:
                return ReleaseGroupsPagerFragment.newInstance();
            case TAB_INFO_POS:
                return ArtistInfoPagerFragment.newInstance();
            case TAB_RATINGS_POS:
                return ArtistRatingsFragment.newInstance();
            case TAB_TAGS_POS:
                return EditTagsPagerFragment.newInstance(ARTIST.ordinal());
        }
        return null;
    }

}
