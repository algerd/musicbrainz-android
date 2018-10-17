package org.musicbrainz.android.adapter.pager;

import android.content.res.Resources;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import org.musicbrainz.android.R;
import org.musicbrainz.android.fragment.WikipediaWebViewFragment;
import org.musicbrainz.android.fragment.ArtistRelationsTabFragment;
import org.musicbrainz.android.fragment.LinksTabFragment;


public class ArtistInfoPagerAdapter extends BaseFragmentPagerAdapter {

    public static final int PAGE_COUNT = 3;
    public final static int TAB_WIKI_POS = 0;
    public final static int TAB_RELATIONS_POS = 1;
    public final static int TAB_LINKS_POS = 2;

    public ArtistInfoPagerAdapter(FragmentManager fm, Resources resources) {
        super(PAGE_COUNT, fm, resources);

        tabTitles[TAB_WIKI_POS] = R.string.artist_info_tab_wiki;
        tabTitles[TAB_RELATIONS_POS] = R.string.artist_info_tab_relations;
        tabTitles[TAB_LINKS_POS] = R.string.artist_info_tab_links;

        tabIcons[TAB_WIKI_POS] = R.drawable.ic_wiki_24_dark;
        tabIcons[TAB_RELATIONS_POS] = R.drawable.ic_artist_relations_24_dark;
        tabIcons[TAB_LINKS_POS] = R.drawable.ic_link_24_dark;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case TAB_WIKI_POS:
                return WikipediaWebViewFragment.newInstance();
            case TAB_RELATIONS_POS:
                return ArtistRelationsTabFragment.newInstance();
            case TAB_LINKS_POS:
                return LinksTabFragment.newInstance();
        }
        return null;
    }

}
