package com.mes.udacity.capstonepopularmovies.detailactivity;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mes.udacity.capstonepopularmovies.models.Review;
import com.mes.udacity.capstonepopularmovies.R;

import java.util.List;

/**
 * Created by moham on 2/18/2018.
 */

class ReviewsListAdapter extends BaseAdapter {

    private List<Review> reviews;
    private final Context context;

    public ReviewsListAdapter(Context context, List<Review> reviews) {
        this.reviews = reviews;
        this.context = context;
    }


    @Override
    public int getCount() {
        return reviews != null ? reviews.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return reviews.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rootView = convertView;
        ReviewsListAdapter.ReviewViewHolder reviewViewHolder = null;
        if(rootView == null){
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            rootView = inflater.inflate(R.layout.review_item,parent,false);
            reviewViewHolder = new ReviewsListAdapter.ReviewViewHolder();
            reviewViewHolder.author = (TextView) rootView.findViewById(R.id.review_author);
            reviewViewHolder.content = (TextView) rootView.findViewById(R.id.review_content);
            rootView.setTag(reviewViewHolder);
        }
        else {
            reviewViewHolder = (ReviewsListAdapter.ReviewViewHolder) rootView.getTag();
        }
        Review review = reviews.get(position);
        reviewViewHolder.author.setText(review.getAuthor());
        reviewViewHolder.content.setText(review.getContent());
        return rootView;
    }

    public static class ReviewViewHolder{
        private TextView author;
        private TextView content;
    }

    public void updateReviews(List<Review> reviews){
        this.reviews = reviews;
        notifyDataSetChanged();
    }

    public void clear(){
        if(this.reviews != null) {
            this.reviews.clear();
        }
    }
}