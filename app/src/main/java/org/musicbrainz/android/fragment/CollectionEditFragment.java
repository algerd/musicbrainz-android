package org.musicbrainz.android.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import java.util.List;

import org.musicbrainz.android.R;
import org.musicbrainz.android.api.model.Collection;
import org.musicbrainz.android.api.site.SiteService;
import org.musicbrainz.android.communicator.GetCollectionCommunicator;
import org.musicbrainz.android.communicator.OnEditCollectionCommunicator;
import org.musicbrainz.android.communicator.ShowTitleCommunicator;
import org.musicbrainz.android.util.ShowUtil;

import static org.musicbrainz.android.MusicBrainzApp.api;


public class CollectionEditFragment extends Fragment {

    public static final String TAG = "CollectionEditFragment";

    boolean isLoading;
    boolean isError;
    private Collection collection;

    private View error;
    private View loading;
    private View content;
    private EditText nameEditText;
    private EditText descriptionEditText;
    private CheckBox publicCheckBox;
    private Button editButton;

    public static CollectionEditFragment newInstance() {
        Bundle args = new Bundle();
        CollectionEditFragment fragment = new CollectionEditFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_collection_edit, container, false);

        content = layout.findViewById(R.id.content);
        error = layout.findViewById(R.id.error);
        loading = layout.findViewById(R.id.loading);
        nameEditText = layout.findViewById(R.id.collection_name);
        descriptionEditText = layout.findViewById(R.id.collection_description);
        publicCheckBox = layout.findViewById(R.id.collection_public);

        editButton = layout.findViewById(R.id.collection_edit_btn);
        editButton.setOnClickListener(v -> edit());

        collection = ((GetCollectionCommunicator) getContext()).getCollection();
        if (collection != null) {
            nameEditText.setText(collection.getName());
            nameEditText.setError(null);
        }

        ((ShowTitleCommunicator) getContext()).getTopTitle().setText(R.string.title_edit_collection);
        return layout;
    }

    private void edit() {
        nameEditText.setError(null);

        String name = nameEditText.getText().toString().trim();
        if (!TextUtils.isEmpty(name)) {
            viewProgressLoading(true);
            //TODO: make .browse(n, m)
            api.getCollections(
                    collectionBrowse -> {
                        viewProgressLoading(false);
                        boolean existName = false;
                        if (collectionBrowse.getCount() > 0) {
                            List<Collection> collections = collectionBrowse.getCollections();
                            for (Collection coll : collections) {
                                if (coll.getName().equalsIgnoreCase(name) && coll.getType().equals(collection.getType())) {
                                    existName = true;
                                    nameEditText.setError(getString(R.string.collection_create_exist_name));
                                    break;
                                }
                            }
                        }
                        if (!existName) {
                            ((OnEditCollectionCommunicator) getContext()).onEditCollection(
                                    name,
                                    SiteService.getCollectionTypeFromSpinner(collection.getType()),
                                    descriptionEditText.getText().toString(),
                                    publicCheckBox.isChecked() ? 1 : 0);
                        }
                    },
                    this::showConnectionWarning,
                    100, 0);
        }
    }

    private void showConnectionWarning(Throwable t) {
        ShowUtil.showError(getContext(), t);
        viewProgressLoading(false);
        viewError(true);
        error.findViewById(R.id.retry_button).setOnClickListener(v -> edit());
    }

    private void viewProgressLoading(boolean isView) {
        if (isView) {
            isLoading = true;
            content.setAlpha(0.3F);
            loading.setVisibility(View.VISIBLE);
        } else {
            isLoading = false;
            content.setAlpha(1.0F);
            loading.setVisibility(View.GONE);
        }
    }

    private void viewError(boolean isView) {
        if (isView) {
            isError = true;
            content.setVisibility(View.INVISIBLE);
            error.setVisibility(View.VISIBLE);
        } else {
            isError = false;
            error.setVisibility(View.GONE);
            content.setVisibility(View.VISIBLE);
        }
    }

}
