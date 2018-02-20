package com.mes.udacity.capstonepopularmovies.DetailActivity;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mes.udacity.capstonepopularmovies.Models.Trailer;
import com.mes.udacity.capstonepopularmovies.R;
import com.mes.udacity.capstonepopularmovies.Utils.Constants;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by moham on 2/18/2018.
 */

public class TrailersListAdapter extends BaseAdapter {

    private final static String TAG = TrailersListAdapter.class.getSimpleName();

    private List<Trailer> trailers;
    private Context context;

    public TrailersListAdapter(Context context, List<Trailer> trailers) {
        this.trailers = trailers;
        this.context = context;
    }


    @Override
    public int getCount() {
        return trailers != null ? trailers.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return trailers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rootView = convertView;
        TrailersListAdapter.TrailerViewHolder trailerViewHolder = null;
        if(rootView == null){
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            rootView = inflater.inflate(R.layout.trailer_item,parent,false);
            trailerViewHolder = new TrailersListAdapter.TrailerViewHolder();
            trailerViewHolder.name = rootView.findViewById(R.id.trailer_name);
            trailerViewHolder.image = rootView.findViewById(R.id.trailer_image);
            rootView.setTag(trailerViewHolder);
        }
        else {
            trailerViewHolder = (TrailersListAdapter.TrailerViewHolder) rootView.getTag();
        }
        Trailer trailer = trailers.get(position);
        trailerViewHolder.name.setText(trailer.getName());
        Picasso.with(trailerViewHolder.image.getContext())
                .load(Constants.YOUTUBE_THUMBNAIL_BASE_URL + trailer.getKey()
                        + Constants.YOUTUBE_THUMBNAIL_IMGNUM_URL)
                .placeholder(R.drawable.player)
                .into(trailerViewHolder.image);
        return rootView;
    }

    public static class TrailerViewHolder{
        private TextView name;
        private ImageView image;
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
