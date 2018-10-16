package org.musicbrainz.android.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import org.musicbrainz.android.R;
import org.musicbrainz.android.adapter.recycler.TagAdapter;
import org.musicbrainz.android.api.model.Release;
import org.musicbrainz.android.api.model.ReleaseGroup;
import org.musicbrainz.android.api.model.Tag;
import org.musicbrainz.android.api.model.xml.UserTagXML;
import org.musicbrainz.android.communicator.GetReleaseCommunicator;
import org.musicbrainz.android.communicator.OnTagCommunicator;
import org.musicbrainz.android.intent.ActivityFactory;
import org.musicbrainz.android.util.ShowUtil;

import static org.musicbrainz.android.MusicBrainzApp.api;
import static org.musicbrainz.android.MusicBrainzApp.oauth;


public class ReleaseTagsFragment extends Fragment {

    private ReleaseGroup releaseGroup;

    private View content;
    private View error;
    private View loading;
    private RecyclerView tagRecycler;
    private TextView loginWarning;
    private EditText tagInput;
    private ImageButton tagBtn;

    public static ReleaseTagsFragment newInstance() {
        Bundle args = new Bundle();
        ReleaseTagsFragment fragment = new ReleaseTagsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_tags, container, false);

        content = layout.findViewById(R.id.content);
        error = layout.findViewById(R.id.error);
        loading = layout.findViewById(R.id.loading);
        tagRecycler = layout.findViewById(R.id.tag_recycler);
        loginWarning = layout.findViewById(R.id.login_warning);
        tagInput = layout.findViewById(R.id.tag_input);
        tagBtn = layout.findViewById(R.id.tag_btn);

        setEditListeners();
        configTagRecycler();
        load();
        return layout;
    }

    public void load() {
        viewProgressLoading(false);
        viewError(false);

        Release release = ((GetReleaseCommunicator) getContext()).getRelease();
        if (release != null && release.getReleaseGroup() != null) {
            releaseGroup = release.getReleaseGroup();
            setTagRecycler();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        loginWarning.setVisibility(oauth.hasAccount() ? View.GONE : View.VISIBLE);
    }

    private void setEditListeners() {
        tagBtn.setOnClickListener(v -> {
            if (loading.getVisibility() == View.VISIBLE) {
                return;
            }
            if (oauth.hasAccount()) {
                String tagString = tagInput.getText().toString().trim();
                if (TextUtils.isEmpty(tagString)) {
                    tagInput.setText("");
                } else {
                    postTag(tagString, UserTagXML.VoteType.UPVOTE);
                }
            } else {
                ActivityFactory.startLoginActivity(getContext());
            }
        });
    }

    private void configTagRecycler() {
        tagRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        tagRecycler.setItemViewCacheSize(25);
        tagRecycler.setDrawingCacheEnabled(true);
        tagRecycler.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        tagRecycler.setHasFixedSize(true);
    }

    private void setTagRecycler() {
        List<Tag> tags = releaseGroup.getTags();
        if (tags != null && !tags.isEmpty()) {
            TagAdapter adapter = new TagAdapter(tags, releaseGroup.getUserTags());
            adapter.setHolderClickListener(pos ->
                    ((OnTagCommunicator) getContext()).onTag(tags.get(pos).getName()));
            tagRecycler.setAdapter(adapter);

            adapter.setOnVoteTagListener((position) -> {
                if (loading.getVisibility() == View.VISIBLE) {
                    return;
                }
                if (oauth.hasAccount()) {
                    String tag = tags.get(position).getName();
                    AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
                    alertDialog.show();
                    Window win = alertDialog.getWindow();
                    if (win != null) {
                        win.setContentView(R.layout.dialog_vote_tag);
                        ImageView voteUpBtn = win.findViewById(R.id.vote_up_btn);
                        voteUpBtn.setOnClickListener(v -> {
                            alertDialog.dismiss();
                            postTag(tag, UserTagXML.VoteType.UPVOTE);
                        });

                        ImageView voteWithdrawBtn = win.findViewById(R.id.vote_withdraw_btn);
                        voteWithdrawBtn.setOnClickListener(v -> {
                            alertDialog.dismiss();
                            postTag(tag, UserTagXML.VoteType.WITHDRAW);
                        });

                        ImageView voteDownBtn = win.findViewById(R.id.vote_down_btn);
                        voteDownBtn.setOnClickListener(v -> {
                            alertDialog.dismiss();
                            postTag(tag, UserTagXML.VoteType.DOWNVOTE);
                        });
                    }
                } else {
                    ActivityFactory.startLoginActivity(getContext());
                }
            });
        }
    }

    private void postTag(String tag, UserTagXML.VoteType voteType) {
        viewProgressLoading(true);
        api.postAlbumTag(
                releaseGroup.getId(), tag, voteType,
                metadata -> {
                    if (metadata.getMessage().getText().equals("OK")) {
                        api.getAlbumTags(
                                releaseGroup.getId(),
                                rg -> {
                                    releaseGroup.setTags(rg.getTags());
                                    releaseGroup.setUserTags(rg.getUserTags());
                                    setTagRecycler();
                                    viewProgressLoading(false);
                                },
                                this::showConnectionWarning);
                    } else {
                        viewProgressLoading(false);
                        ShowUtil.showMessage(getActivity(), "Error");
                    }
                },
                this::showConnectionWarning);
    }

    private void viewProgressLoading(boolean isView) {
        if (isView) {
            content.setAlpha(0.3F);
            loading.setVisibility(View.VISIBLE);
        } else {
            content.setAlpha(1.0F);
            loading.setVisibility(View.GONE);
        }
    }

    private void viewError(boolean isView) {
        if (isView) {
            content.setVisibility(View.INVISIBLE);
            error.setVisibility(View.VISIBLE);
        } else {
            error.setVisibility(View.GONE);
            content.setVisibility(View.VISIBLE);
        }
    }

    private void showConnectionWarning(Throwable t) {
        ShowUtil.showError(getActivity(), t);
        viewProgressLoading(false);
        viewError(true);
        error.setVisibility(View.VISIBLE);
        error.findViewById(R.id.retry_button).setOnClickListener(v -> load());
    }

}
