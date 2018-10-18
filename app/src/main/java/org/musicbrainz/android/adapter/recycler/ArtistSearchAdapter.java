package org.musicbrainz.android.adapter.recycler;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.musicbrainz.android.R;
import org.musicbrainz.android.api.core.ApiUtils;
import org.musicbrainz.android.api.lastfm.model.Image;
import org.musicbrainz.android.api.model.Artist;

import java.util.List;

import static android.text.TextUtils.TruncateAt.END;
import static org.musicbrainz.android.MusicBrainzApp.api;

/**
 * Created by Alex on 17.01.2018.
 */

public class ArtistSearchAdapter extends BaseRecyclerViewAdapter<ArtistSearchAdapter.ArtistSearchViewHolder> {

    private List<Artist> artists;

    public static class ArtistSearchViewHolder extends BaseRecyclerViewAdapter.BaseViewHolder {

        static final int VIEW_HOLDER_LAYOUT = R.layout.card_search_artist;

        private LinearLayout container;
        private ImageView image;
        private ProgressBar progressLoading;
        private TextView artistName;
        private TextView type;

        public static ArtistSearchViewHolder create(ViewGroup parent) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View view = layoutInflater.inflate(VIEW_HOLDER_LAYOUT, parent, false);
            return new ArtistSearchViewHolder(view);
        }

        private ArtistSearchViewHolder(View v) {
            super(v);
            container = v.findViewById(R.id.artist_container);
            image = v.findViewById(R.id.artist_image);
            progressLoading = v.findViewById(R.id.image_loading);
            artistName = v.findViewById(R.id.artist_name);
            type = v.findViewById(R.id.artist_type);
        }

        public void bindTo(Artist artist) {
            artistName.setText(artist.getName());
            type.setText(artist.getType());

            String areastr = "";
            if (artist.getArea() != null && !TextUtils.isEmpty(artist.getArea().getName())) {
                areastr = artist.getArea().getName();
            }
            String foundedstr = "";
            if (artist.getLifeSpan() != null && !TextUtils.isEmpty(artist.getLifeSpan().getBegin())) {
                foundedstr = artist.getLifeSpan().getBegin();
                if (!TextUtils.isEmpty(artist.getLifeSpan().getEnd())) {
                    foundedstr += " - " + artist.getLifeSpan().getEnd();
                }
            }
            String separator = !areastr.equals("") && !foundedstr.equals("") ? ", " : "";
            addTextView(areastr + separator + foundedstr);
            addTextView(artist.getDisambiguation());
            if (artist.getTags() != null && !artist.getTags().isEmpty()) {
                addTextView("Tags: " + ApiUtils.getStringFromList(artist.getTags(), ", "));
            }
            loadArtistImageFromLastfm(artist.getName());
        }

        private void addTextView(String text) {
            if (!TextUtils.isEmpty(text)) {
                TextView textView = new TextView(itemView.getContext());
                textView.setText(text);
                textView.setTextSize(12);
                textView.setEllipsize(END);
                textView.setSingleLine();
                textView.setTextColor(itemView.getResources().getColor(R.color.colorPrimaryLight));
                container.addView(textView);
            }
        }

        private void loadArtistImageFromLastfm(String name) {
            image.setVisibility(View.INVISIBLE);
            progressLoading.setVisibility(View.VISIBLE);
            api.getArtistFromLastfm(
                    name,
                    result -> {
                        if (result.getError() == null || result.getError() == 0) {
                            List<Image> images = result.getArtist().getImages();
                            if (images != null && !images.isEmpty()) {
                                for (Image img : images) {
                                    if (img.getSize().equals(Image.SizeType.MEDIUM.toString()) && !TextUtils.isEmpty(img.getText())) {
                                        Picasso.with(itemView.getContext()).load(img.getText()).fit().into(image);
                                        break;
                                    }
                                }
                            }
                        }
                        progressLoading.setVisibility(View.GONE);
                        image.setVisibility(View.VISIBLE);
                    },
                    t -> {
                        progressLoading.setVisibility(View.GONE);
                        image.setVisibility(View.VISIBLE);
                    });
        }

    }

    public ArtistSearchAdapter(List<Artist> artists) {
        this.artists = artists;
    }

    @Override
    public void onBind(ArtistSearchViewHolder holder, final int position) {
        holder.bindTo(artists.get(position));
    }

    @Override
    public int getItemCount() {
        return artists.size();
    }

    @NonNull
    @Override
    public ArtistSearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return ArtistSearchViewHolder.create(parent);
    }
}
