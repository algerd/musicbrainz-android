package org.musicbrainz.android.adapter.recycler;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.squareup.picasso.Picasso;

import java.util.List;

import org.musicbrainz.android.R;
import org.musicbrainz.android.api.coverart.CoverArtImage;
import org.musicbrainz.android.util.PicassoHelper;

public class CoverArtAdapter extends BaseRecyclerViewAdapter<CoverArtAdapter.ViewHolder> {

    private List<CoverArtImage> coverArts;

    public static class ViewHolder extends BaseRecyclerViewAdapter.BaseViewHolder {

        private ImageView coverart;
        private ProgressBar coverartLoading;

        public ViewHolder(CardView v) {
            super(v);
            coverart = v.findViewById(R.id.coverart);
            coverartLoading = v.findViewById(R.id.coverart_loading);
        }

        public void bindView(@NonNull CoverArtImage coverArtImage) {
            coverartLoading.setVisibility(View.VISIBLE);
            Picasso.with(itemView.getContext()).load(coverArtImage.getThumbnails().getLarge())
                    .into(coverart, PicassoHelper.createPicassoProgressCallback(coverartLoading));
        }
    }

    public CoverArtAdapter(List<CoverArtImage> coverArts) {
        this.coverArts = coverArts;
    }

    @Override
    public void onBind(ViewHolder holder, final int position) {
        holder.bindView(coverArts.get(position));
    }

    @Override
    public int getItemCount() {
        return coverArts.size();
    }

    @Override
    public CoverArtAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(inflateCardView(parent, R.layout.card_coverart));
    }

}
