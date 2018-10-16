package org.musicbrainz.android.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import org.musicbrainz.android.R;
import org.musicbrainz.android.adapter.recycler.CollectionsAdapter;
import org.musicbrainz.android.api.model.Collection;
import org.musicbrainz.android.communicator.GetCollectionsCommunicator;
import org.musicbrainz.android.communicator.GetUsernameCommunicator;
import org.musicbrainz.android.communicator.OnCollectionCommunicator;
import org.musicbrainz.android.util.ShowUtil;

import static org.musicbrainz.android.MusicBrainzApp.api;
import static org.musicbrainz.android.MusicBrainzApp.oauth;
import static org.musicbrainz.android.adapter.pager.CollectionsPagerAdapter.CollectionTab.AREAS;
import static org.musicbrainz.android.adapter.pager.CollectionsPagerAdapter.CollectionTab.ARTISTS;
import static org.musicbrainz.android.adapter.pager.CollectionsPagerAdapter.CollectionTab.EVENTS;
import static org.musicbrainz.android.adapter.pager.CollectionsPagerAdapter.CollectionTab.INSTRUMENTS;
import static org.musicbrainz.android.adapter.pager.CollectionsPagerAdapter.CollectionTab.LABELS;
import static org.musicbrainz.android.adapter.pager.CollectionsPagerAdapter.CollectionTab.PLACES;
import static org.musicbrainz.android.adapter.pager.CollectionsPagerAdapter.CollectionTab.RECORDINGS;
import static org.musicbrainz.android.adapter.pager.CollectionsPagerAdapter.CollectionTab.RELEASES;
import static org.musicbrainz.android.adapter.pager.CollectionsPagerAdapter.CollectionTab.RELEASE_GROUPS;
import static org.musicbrainz.android.adapter.pager.CollectionsPagerAdapter.CollectionTab.SERIES;
import static org.musicbrainz.android.adapter.pager.CollectionsPagerAdapter.CollectionTab.WORKS;
import static org.musicbrainz.android.api.model.Collection.AREA_ENTITY_TYPE;
import static org.musicbrainz.android.api.model.Collection.ARTIST_ENTITY_TYPE;
import static org.musicbrainz.android.api.model.Collection.EVENT_ENTITY_TYPE;
import static org.musicbrainz.android.api.model.Collection.INSTRUMENT_ENTITY_TYPE;
import static org.musicbrainz.android.api.model.Collection.LABEL_ENTITY_TYPE;
import static org.musicbrainz.android.api.model.Collection.PLACE_ENTITY_TYPE;
import static org.musicbrainz.android.api.model.Collection.RECORDING_ENTITY_TYPE;
import static org.musicbrainz.android.api.model.Collection.RELEASE_ENTITY_TYPE;
import static org.musicbrainz.android.api.model.Collection.RELEASE_GROUP_ENTITY_TYPE;
import static org.musicbrainz.android.api.model.Collection.SERIES_ENTITY_TYPE;
import static org.musicbrainz.android.api.model.Collection.WORK_ENTITY_TYPE;


public class CollectionsTabFragment extends Fragment {

    private static final String COLLECTION_TAB = "COLLECTION_TAB";

    private int collectionTab;
    private boolean isPrivate;

    private View loading;
    private RecyclerView collectionsRecycler;

    public static CollectionsTabFragment newInstance(int collectionTab) {
        Bundle args = new Bundle();
        args.putInt(COLLECTION_TAB, collectionTab);
        CollectionsTabFragment fragment = new CollectionsTabFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_collections_tab, container, false);

        collectionTab = getArguments().getInt(COLLECTION_TAB);
        collectionsRecycler = layout.findViewById(R.id.collections_recycler);
        loading = layout.findViewById(R.id.loading);

        load();
        return layout;
    }

    public void load() {
        viewProgressLoading(false);
        isPrivate = false;
        if (getContext() instanceof GetUsernameCommunicator) {
            String username = ((GetUsernameCommunicator) getContext()).getUsername();
            isPrivate = oauth.hasAccount() && username.equals(oauth.getName());
        }
        List<Collection> collections = ((GetCollectionsCommunicator) getParentFragment()).getCollections();
        if (collections != null) {
            List<Collection> tabCollections = new ArrayList<>();
            for (Collection collection : collections) {
                if ((AREA_ENTITY_TYPE.equals(collection.getEntityType()) && collectionTab == AREAS.ordinal())
                        || (ARTIST_ENTITY_TYPE.equals(collection.getEntityType()) && collectionTab == ARTISTS.ordinal())
                        || (EVENT_ENTITY_TYPE.equals(collection.getEntityType()) && collectionTab == EVENTS.ordinal())
                        || (INSTRUMENT_ENTITY_TYPE.equals(collection.getEntityType()) && collectionTab == INSTRUMENTS.ordinal())
                        || (LABEL_ENTITY_TYPE.equals(collection.getEntityType()) && collectionTab == LABELS.ordinal())
                        || (PLACE_ENTITY_TYPE.equals(collection.getEntityType()) && collectionTab == PLACES.ordinal())
                        || (RECORDING_ENTITY_TYPE.equals(collection.getEntityType()) && collectionTab == RECORDINGS.ordinal())
                        || (RELEASE_ENTITY_TYPE.equals(collection.getEntityType()) && collectionTab == RELEASES.ordinal())
                        || (RELEASE_GROUP_ENTITY_TYPE.equals(collection.getEntityType()) && collectionTab == RELEASE_GROUPS.ordinal())
                        || (SERIES_ENTITY_TYPE.equals(collection.getEntityType()) && collectionTab == SERIES.ordinal())
                        || (WORK_ENTITY_TYPE.equals(collection.getEntityType()) && collectionTab == WORKS.ordinal())) {
                    tabCollections.add(collection);
                }
            }
            if (!tabCollections.isEmpty()) {
                CollectionsAdapter adapter = new CollectionsAdapter(tabCollections, isPrivate);

                adapter.setHolderClickListener(pos ->
                        ((OnCollectionCommunicator) getContext()).onCollection(tabCollections.get(pos)));

                if (isPrivate) {
                    adapter.setOnDeleteCollectionListener(
                            pos -> {
                                Collection collection = tabCollections.get(pos);
                                View titleView = getLayoutInflater().inflate(R.layout.layout_custom_alert_dialog_title, null);
                                TextView titleText = titleView.findViewById(R.id.title_text);
                                titleText.setText(getString(R.string.collection_alert_title, collection.getName()));

                                new AlertDialog.Builder(getContext())
                                        .setCustomTitle(titleView)
                                        .setMessage(getString(R.string.delete_alert_message))
                                        .setPositiveButton(android.R.string.yes,
                                                (dialog, which) -> {
                                                    viewProgressLoading(true);
                                                    api.deleteCollection(collection,
                                                            responseBody -> {
                                                                viewProgressLoading(false);
                                                                tabCollections.remove(collection);
                                                                adapter.notifyItemRemoved(pos);
                                                            },
                                                            t -> {
                                                                viewProgressLoading(false);
                                                                ShowUtil.showError(getContext(), t);
                                                            });
                                                })
                                        .setNegativeButton(android.R.string.no, (dialog, which) -> dialog.cancel())
                                        .show();
                            });
                }
                collectionsRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
                collectionsRecycler.setItemViewCacheSize(100);
                collectionsRecycler.setHasFixedSize(true);
                collectionsRecycler.setAdapter(adapter);
            }
        }
    }

    private void viewProgressLoading(boolean isView) {
        if (isView) {
            collectionsRecycler.setAlpha(0.3F);
            loading.setVisibility(View.VISIBLE);
        } else {
            collectionsRecycler.setAlpha(1.0F);
            loading.setVisibility(View.GONE);
        }
    }
}
