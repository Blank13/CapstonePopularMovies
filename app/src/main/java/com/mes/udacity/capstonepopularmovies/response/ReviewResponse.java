package com.mes.udacity.capstonepopularmovies.response;

import com.google.gson.annotations.SerializedName;
import com.mes.udacity.capstonepopularmovies.models.Review;

import java.util.List;

/**
 * Created by moham on 2/18/2018.
 */

public class ReviewResponse {

    @SerializedName("results")
    private List<Review> reviews;

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }
}
