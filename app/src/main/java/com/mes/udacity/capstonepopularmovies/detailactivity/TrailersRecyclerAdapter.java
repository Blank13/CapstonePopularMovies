package com.mes.udacity.capstonepopularmovies.detailactivity;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mes.udacity.capstonepopularmovies.models.Trailer;
import com.mes.udacity.capstonepopularmovies.R;
import com.mes.udacity.capstonepopularmovies.utils.Constants;
import com.mes.udacity.capstonepopularmovies.utils.ListItemClickListener;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by moham on 2/20/2018.
 */

public class TrailersRecyclerAdapter extends RecyclerView.Adapter<TrailersRecyclerAdapter.TrailerViewHolder> {

    private final static String TAG = TrailersRecyclerAdapter.class.getSimpleName();
    private static ListItemClickListener listItemClickListener;
    private List<Trailer> trailers;

    public TrailersRecyclerAdapter(List<Trailer> trailers, ListItemClickListener listItemClickListener) {
        this.trailers = trailers;
        TrailersRecyclerAdapter.listItemClickListener = listItemClickListener;
    }

    public Trailer getItem(int position) {
        return trailers.get(position);
    }

    @Override
    public TrailerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trailer_item, parent, false);
        return new TrailerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TrailerViewHolder holder, int position) {
        Trailer trailer = trailers.get(position);
        holder.name.setText(trailer.getName());
        Picasso.with(holder.image.getContext())
                .load(Constants.YOUTUBE_THUMBNAIL_BASE_URL + trailer.getKey()
                        + Constants.YOUTUBE_THUMBNAIL_IMGNUM_URL)
                .placeholder(R.drawable.player)
                .into(holder.image);
    }

    @Override
    public int getItemCount() {
        return trailers != null ? trailers.size() : 0;
    }

    public static class TrailerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView image;
        private TextView name;

        public TrailerViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.trailer_image);
            name = itemView.findViewById(R.id.trailer_name);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int clickedItemIndex = getAdapterPosition();
            listItemClickListener.onListItemClick(clickedItemIndex);
        }

    }

    public void updatetrailers(List<Trailer> trailers){
        this.trailers = trailers;
        notifyDataSetChanged();
    }

    public void clear(){
        if(this.trailers != null) {
            this.trailers.clear();
        }
    }
}
