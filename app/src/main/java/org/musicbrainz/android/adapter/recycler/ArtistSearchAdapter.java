package org.musicbrainz.android.adapter.recycler;

import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import org.musicbrainz.android.R;
import org.musicbrainz.android.api.core.ApiUtils;
import org.musicbrainz.android.api.lastfm.model.Image;
import org.musicbrainz.android.api.model.Artist;

import static android.text.TextUtils.TruncateAt.END;
import static org.musicbrainz.android.MusicBrainzApp.api;

/**
 * Created by Alex on 17.01.2018.
 */

public class ArtistSearchAdapter extends BaseRecyclerViewAdapter<ArtistSearchAdapter.ViewHolder> {

    private List<Artist> artists;

    public static class ViewHolder extends BaseRecyclerViewAdapter.BaseViewHolder {

        private LinearLayout container;
        private ImageView image;
        private ProgressBar progressLoading;
        private TextView artistName;
        private TextView type;

        public ViewHolder(CardView v) {
            super(v);
            container = v.findViewById(R.id.artist_container);
            image = v.findViewById(R.id.artist_image);
            progressLoading = v.findViewById(R.id.image_loading);
            artistName = v.findViewById(R.id.artist_name);
            type = v.findViewById(R.id.artist_type);
        }

        public void bindView(Artist artist) {
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
    public void onBind(ViewHolder holder, final int position) {
        holder.bindView(artists.get(position));
    }

    @Override
    public int getItemCount() {
        return artists.size();
    }

    @Override
    public ArtistSearchAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(inflateCardView(parent, R.layout.card_search_artist));
    }
}
