package com.mes.udacity.capstonepopularmovies.detailactivity;

import com.mes.udacity.capstonepopularmovies.models.Review;

import java.util.List;

/**
 * Created by moham on 2/18/2018.
 */

interface ReviewsListListener {

    void onReviewListReady(List<Review> reviewList);

    void onReviewError(String message);
}
