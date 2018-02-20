package com.mes.udacity.capstonepopularmovies.DetailActivity;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.AsyncTaskLoader;

import com.google.gson.Gson;
import com.mes.udacity.capstonepopularmovies.DataBase.MovieContract;
import com.mes.udacity.capstonepopularmovies.Models.Movie;
import com.mes.udacity.capstonepopularmovies.Models.Review;
import com.mes.udacity.capstonepopularmovies.Response.ReviewResponse;
import com.mes.udacity.capstonepopularmovies.Utils.Constants;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static com.mes.udacity.capstonepopularmovies.Utils.StaticMethods.getBodyString;

/**
 * Created by moham on 2/20/2018.
 */

public class FetchReviewsLoader extends AsyncTaskLoader<List<Review>> {

    private Movie movie;

    public FetchReviewsLoader(Context context, Movie movie) {
        super(context);
        this.movie = movie;
    }

    @Override
    public List<Review> loadInBackground() {
        Cursor cursor = getContext().getContentResolver()
                .query(MovieContract.ReviewEntry.REVIEW_CONTENT_URI,
                        null,
                        MovieContract.ReviewEntry.MOVIE_ID + " = " + movie.getId(),
                        null,
                        null);
        if(cursor != null){
            if(cursor.getCount() > 0){
                List<Review> trailers = new ArrayList<>();
                while (cursor.moveToNext()){
                    Review review = new Review();
                    review.setAuthor(cursor.getString(
                            cursor.getColumnIndex(MovieContract.ReviewEntry.REVIEWS_AUTHOR)));
                    review.setContent(cursor.getString(
                            cursor.getColumnIndex(MovieContract.ReviewEntry.REVIEWS_CONTENT)));
                    trailers.add(review);
                }
                cursor.close();
                return trailers;
            }
            cursor.close();
        }
        HttpURLConnection urlConnection = null;
        ReviewResponse reviewsResponse;

        final String APPID_PARAM = "api_key";
        final String TYPE_PARAM = "reviews";

        Uri uri = Uri.parse(Constants.MOVIE_API_BASE_URL).buildUpon()
                .appendPath(String.valueOf(movie.getId()))
                .appendEncodedPath(TYPE_PARAM)
                .appendQueryParameter(APPID_PARAM,Constants.MOVIE_API_KEY)
                .build();
        try {
            URL url = new URL(uri.toString());
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            Gson gson = new Gson();
            reviewsResponse = gson.fromJson(
                    getBodyString(urlConnection.getInputStream()),ReviewResponse.class);
            return reviewsResponse.getReviews();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return null;
    }
}
