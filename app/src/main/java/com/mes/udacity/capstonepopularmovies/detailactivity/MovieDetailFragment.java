package com.mes.udacity.capstonepopularmovies.detailactivity;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RemoteViews;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.mes.udacity.capstonepopularmovies.database.MovieContract;
import com.mes.udacity.capstonepopularmovies.database.MovieDBHelper;
import com.mes.udacity.capstonepopularmovies.models.Movie;
import com.mes.udacity.capstonepopularmovies.models.Review;
import com.mes.udacity.capstonepopularmovies.models.Trailer;
import com.mes.udacity.capstonepopularmovies.moviepostersactivity.MoviePostersActivity;
import com.mes.udacity.capstonepopularmovies.R;
import com.mes.udacity.capstonepopularmovies.response.TrailerResponse;
import com.mes.udacity.capstonepopularmovies.utils.Constants;
import com.mes.udacity.capstonepopularmovies.utils.ListItemClickListener;
import com.mes.udacity.capstonepopularmovies.utils.SizedListView;
import com.mes.udacity.capstonepopularmovies.widget.FavoriteWidgetProvider;
import com.mes.udacity.capstonepopularmovies.widget.FavoriteWidgetService;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static com.mes.udacity.capstonepopularmovies.utils.StaticMethods.getBodyString;

/**
 * Created by moham on 2/18/2018.
 */

public class MovieDetailFragment extends Fragment implements ReviewsListListener,
        TrailersListListener, ListItemClickListener, LoaderManager.LoaderCallbacks<List<Review>>{

    public static final String MOVIE_CALL = "movie";

    private Movie movie;

    private ScrollView scrollView;
    private TextView title;
    private TextView date;
    private TextView rate;
    private TextView overView;
    private TextView trailersFound;
    private TextView reviewsFound;
    private Button favButton;
    private ImageView image;
    private ProgressBar trailersProgressBar;
    private ProgressBar reviewsProgressBar;

    private RecyclerView trailers;
    private SizedListView reviews;
    private TrailersRecyclerAdapter trailersRecyclerAdapter;
    private ReviewsListAdapter reviewsListAdapter;

    private boolean firstTime = true;

    public interface DetailCallBack{
        void onFavouriteClick();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movie_detail,container,false);
        Intent intent = getActivity().getIntent();
        String movieJson;
        Gson gson = new Gson();
        if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)){
            movieJson = intent.getStringExtra(Intent.EXTRA_TEXT);
            movie = gson.fromJson(movieJson,Movie.class);
        }
        else {
            Bundle args = getArguments();
            movieJson = args.getString(MOVIE_CALL);
            movie = gson.fromJson(movieJson, Movie.class);
        }

        scrollView = view.findViewById(R.id.detail_scroll);

        title = view.findViewById(R.id.movie_titile);
        date = view.findViewById(R.id.movie_date);
        rate = view.findViewById(R.id.movie_rate);
        overView = view.findViewById(R.id.movie_overview);

        favButton = view.findViewById(R.id.movie_fav_button);
        if(checkExistenceInFavourite()){
            favButton.setSelected(true);
        }
        initFavouriteAction();

        image = view.findViewById(R.id.movie_image);
        trailersProgressBar = view.findViewById(R.id.movie_trials_progressbar);
        reviewsProgressBar = view.findViewById(R.id.movie_reviews_progressbar);
        title.setText(movie.getTitle());
        date.setText(movie.getReleaseDate());
        rate.setText(Double.toString(movie.getVoteAverage())+"/10");
        overView.setText(movie.getOverView());

        trailers = view.findViewById(R.id.movie_trials);
        reviews = view.findViewById(R.id.movie_reviews);
        trailersFound = view.findViewById(R.id.trials_found);
        reviewsFound = view.findViewById(R.id.review_found);
        Picasso.with(getContext())
                .load(Constants.MOVIE_API_IMAGE_BASE_URL+ movie.getPosterPath())
                .into(image);

        initTrailersAndReviews();

        if(trailersRecyclerAdapter == null){
            trailersRecyclerAdapter = new TrailersRecyclerAdapter(new ArrayList<Trailer>(), this);
        }
        if(reviewsListAdapter == null){
            reviewsListAdapter = new ReviewsListAdapter(getContext(),new ArrayList<Review>());
        }
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false);
        reviews.setFocusable(false);
        trailers.setLayoutManager(layoutManager);
        trailers.setAdapter(trailersRecyclerAdapter);
        reviews.setAdapter(reviewsListAdapter);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        setRetainInstance(true);
        super.onActivityCreated(savedInstanceState);
    }

    private boolean checkExistenceInFavourite() {
        if(movie == null){
            return false;
        }
        MovieDBHelper movieDBHelper = new MovieDBHelper(getContext());
        SQLiteDatabase db = movieDBHelper.getReadableDatabase();
        final String search = "SELECT "+ MovieContract.MovieEntry._ID + " FROM "+
                MovieContract.MovieEntry.MOVIES_TABLE + " WHERE " +
                MovieContract.MovieEntry._ID  + " = " + movie.getId() + "";
        Cursor cursor = db.rawQuery(search, null);
        if(cursor.getCount() <= 0){
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

    private void initFavouriteAction() {
        favButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!v.isSelected()){
                    v.setSelected(true);
                    addToFavourite();
                }
                else {
                    v.setSelected(false);
                    removeFromFavourite();
                    if (getActivity() instanceof MoviePostersActivity) {
                        ((DetailCallBack) getActivity()).onFavouriteClick();
                    }
                }
            }
        });
    }

    private void initTrailersAndReviews() {
        if(firstTime){
            String id = String.valueOf(movie.getId());
            FetchTrailers fetchTrailers = new FetchTrailers();
            fetchTrailers.execute(id);
            getLoaderManager().initLoader(Constants.MOVIE_REVIEWS_LOADER,null, this)
                    .forceLoad();
        }
    }

    private void updateWidget(){
        FavoriteWidgetService.startActionUpdateFavouriteWidgets(this.getContext());
//        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getContext());
//        RemoteViews remoteViews = new RemoteViews(getContext().getPackageName(),R.layout.favorite_widget);
//        int[] appWidgetIds = appWidgetManager
//                .getAppWidgetIds(new ComponentName(getContext(),
//                        FavoriteWidgetProvider.class));
//        for (int appWidgetId : appWidgetIds){
//
//            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.widget_fav_listview);
//        }
    }

    //Required Implemented methods
    //Trailer ClickListListener
    @Override
    public void onListItemClick(int clickedItemIndex) {
        Trailer trailer = trailersRecyclerAdapter.getItem(clickedItemIndex);
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse(Constants.YOUTUBE_VIDEO_BASE_URL + trailer.getKey()));
        startActivity(webIntent);
    }
    //Reviews Loader
    @Override
    public Loader<List<Review>> onCreateLoader(int id, Bundle args) {
        return new FetchReviewsLoader(getContext(),movie);
    }

    @Override
    public void onLoadFinished(Loader<List<Review>> loader, List<Review> data) {
        if(data != null){
            onReviewListReady(data);
        }
        else {
            onReviewError("");
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Review>> loader) {
        reviewsListAdapter.clear();
    }
    //Review ListListener
    @Override
    public void onReviewListReady(List<Review> reviewList) {
        reviewsProgressBar.setVisibility(View.GONE);
        reviewsListAdapter.updateReviews(reviewList);
        if(reviewsListAdapter.getCount() > 0) {
            reviewsFound.setVisibility(View.GONE);
        }
        else {
            reviewsFound.setVisibility(View.VISIBLE);
        }
        reviews.setExpanded(true);
    }

    @Override
    public void onReviewError(String message) {
        reviewsFound.setVisibility(View.VISIBLE);
    }
    //Trailers ListListener
    @Override
    public void onTrailerListReady(List<Trailer> trailerList) {
        trailersProgressBar.setVisibility(View.GONE);
        trailersRecyclerAdapter.updatetrailers(trailerList);
        if(trailersRecyclerAdapter.getItemCount() > 0) {
            trailersFound.setVisibility(View.GONE);
        }
        else {
            trailersFound.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onTrailerError(String message) {
        trailersFound.setVisibility(View.VISIBLE);
    }

    //Async Tasks
    private class FetchTrailers extends AsyncTask<String, Void, List<Trailer>> {

        @Override
        protected List<Trailer> doInBackground(String... params) {
            Cursor cursor = getContext().getContentResolver()
                    .query(MovieContract.TrailerEntry.TRAILER_CONTENT_URI,
                            null,
                            MovieContract.TrailerEntry.MOVIE_ID + " = " + movie.getId(),
                            null,
                            null);
            if(cursor != null){
                if(cursor.getCount() > 0){
                    List<Trailer> trailers = new ArrayList<>();
                    while (cursor.moveToNext()){
                        Trailer trailer = new Trailer();
                        trailer.setKey(cursor.getString(
                                cursor.getColumnIndex(MovieContract.TrailerEntry.TRAILER_KEY)));
                        trailer.setName(cursor.getString(
                                cursor.getColumnIndex(MovieContract.TrailerEntry.TRAILER_NAME)));
                        trailers.add(trailer);
                    }
                    cursor.close();
                    return trailers;
                }
                cursor.close();
            }
            HttpURLConnection urlConnection = null;
            TrailerResponse trailerResponse = null;

            final String APPID_PARAM = "api_key";
            final String TYPE_PARAM = "videos";

            Uri uri = Uri.parse(Constants.MOVIE_API_BASE_URL).buildUpon()
                    .appendPath(params[0])
                    .appendEncodedPath(TYPE_PARAM)
                    .appendQueryParameter(APPID_PARAM,Constants.MOVIE_API_KEY)
                    .build();
            try {
                URL url = new URL(uri.toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                Gson gson = new Gson();
                trailerResponse = gson.fromJson(getBodyString(urlConnection.getInputStream()),TrailerResponse.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            return trailerResponse.getTrailers();
        }

        @Override
        protected void onPostExecute(List<Trailer> trailerList) {
            if(trailerList != null){
                onTrailerListReady(trailerList);
            }
            else{
                onTrailerError("");
            }
        }
    }

    private void addToFavourite() {
        AddToDB db = new AddToDB();
        db.execute();
    }

    private void removeFromFavourite() {
        DeleteFromDB db = new DeleteFromDB();
        db.execute();
    }

    private class AddToDB extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... params) {
            ContentValues values = new ContentValues();
            values.put(MovieContract.MovieEntry._ID, movie.getId());
            values.put(MovieContract.MovieEntry.MOVIE_TITLE, movie.getTitle());
            values.put(MovieContract.MovieEntry.MOVIE_POSTER_PATH, movie.getPosterPath());
            values.put(MovieContract.MovieEntry.MOVIE_RELEASE_DATE, movie.getReleaseDate());
            values.put(MovieContract.MovieEntry.MOVIE_VOTE_AVERAGE, movie.getVoteAverage());
            values.put(MovieContract.MovieEntry.MOVIE_OVER_VIEW, movie.getOverView());
            getContext().getContentResolver()
                    .insert(MovieContract.MovieEntry.MOVIE_CONTENT_URI, values);
            for(int i = 0; i < trailersRecyclerAdapter.getItemCount(); i++){
                Trailer trailer = trailersRecyclerAdapter.getItem(i);
                values.clear();
                values.put(MovieContract.TrailerEntry.MOVIE_ID, movie.getId());
                values.put(MovieContract.TrailerEntry.TRAILER_KEY, trailer.getKey());
                values.put(MovieContract.TrailerEntry.TRAILER_NAME, trailer.getName());
                getContext().getContentResolver()
                        .insert(MovieContract.TrailerEntry.TRAILER_CONTENT_URI,values);
            }

            for(int i = 0; i < reviewsListAdapter.getCount(); i++){
                Review review = (Review) reviewsListAdapter.getItem(i);
                values.clear();
                values.put(MovieContract.ReviewEntry.MOVIE_ID, movie.getId());
                values.put(MovieContract.ReviewEntry.REVIEWS_AUTHOR, review.getAuthor());
                values.put(MovieContract.ReviewEntry.REVIEWS_CONTENT, review.getContent());
                getContext().getContentResolver()
                        .insert(MovieContract.ReviewEntry.REVIEW_CONTENT_URI, values);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            updateWidget();
        }
    }

    private class DeleteFromDB extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... params) {
            getContext().getContentResolver()
                    .delete(MovieContract.MovieEntry.MOVIE_CONTENT_URI,
                            MovieContract.MovieEntry._ID + " = " + movie.getId(),
                            null);
            getContext().getContentResolver()
                    .delete(MovieContract.TrailerEntry.TRAILER_CONTENT_URI,
                            MovieContract.ReviewEntry.MOVIE_ID + " = " + movie.getId(),
                            null);
            getContext().getContentResolver()
                    .delete(MovieContract.ReviewEntry.REVIEW_CONTENT_URI,
                            MovieContract.ReviewEntry.MOVIE_ID + " = " + movie.getId(),
                            null);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            updateWidget();
        }
    }

}