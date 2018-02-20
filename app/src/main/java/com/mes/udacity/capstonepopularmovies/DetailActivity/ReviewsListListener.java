package com.mes.udacity.capstonepopularmovies.DetailActivity;

import com.mes.udacity.capstonepopularmovies.Models.Review;

import java.util.List;

/**
 * Created by moham on 2/18/2018.
 */

public interface ReviewsListListener {

    void onReviewListReady(List<Review> reviewList);

    void onReviewError(String message);
}
