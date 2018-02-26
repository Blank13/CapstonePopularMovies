package com.mes.udacity.capstonepopularmovies.widget;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.google.gson.Gson;
import com.mes.udacity.capstonepopularmovies.database.MovieContract;
import com.mes.udacity.capstonepopularmovies.models.Movie;
import com.mes.udacity.capstonepopularmovies.R;
import com.mes.udacity.capstonepopularmovies.utils.Constants;
import com.squareup.picasso.Picasso;

import java.io.IOException;

/**
 * Created by moham on 2/20/2018.
 */

public class FavoriteWidgetListService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new FavoriteMoviesListRemoteViewFactory(this.getApplicationContext());
    }
}

class FavoriteMoviesListRemoteViewFactory implements RemoteViewsService.RemoteViewsFactory {

    Context context;
    Cursor cursor;

    public FavoriteMoviesListRemoteViewFactory(Context context) {
        this.context = context;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
        if(cursor != null) {
            cursor.close();
        }
        cursor = context.getContentResolver().query(MovieContract.MovieEntry.MOVIE_CONTENT_URI,
                        null, null, null, null);
    }

    @Override
    public void onDestroy() {
        if(cursor != null) {
            cursor.close();
        }
    }

    @Override
    public int getCount() {
        return cursor != null ? cursor.getCount() : 0;
    }

    @Override
    public RemoteViews getViewAt(int i) {
        if (cursor == null || cursor.getCount() == 0) {
            return null;
        }
        cursor.moveToPosition(i);
        Movie movie = new Movie();
        movie.setId(Long.parseLong(cursor.getString(
                cursor.getColumnIndex(MovieContract.MovieEntry._ID))));
        movie.setTitle(cursor.getString(
                cursor.getColumnIndex(MovieContract.MovieEntry.MOVIE_TITLE)));
        movie.setPosterPath(cursor.getString(
                cursor.getColumnIndex(MovieContract.MovieEntry.MOVIE_POSTER_PATH)));
        movie.setReleaseDate(cursor.getString(
                cursor.getColumnIndex(MovieContract.MovieEntry.MOVIE_RELEASE_DATE)));
        movie.setVoteAverage(Double.parseDouble(cursor.getString(
                cursor.getColumnIndex(MovieContract.MovieEntry.MOVIE_VOTE_AVERAGE))));
        movie.setOverView(cursor.getString(
                cursor.getColumnIndex(MovieContract.MovieEntry.MOVIE_OVER_VIEW)));
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.favourite_widget_item);
        remoteViews.setTextViewText(R.id.widget_fav_name, movie.getTitle());
        remoteViews.setTextViewText(R.id.widget_fav_rate, String.valueOf(movie.getVoteAverage()));
        try {
            Bitmap b = Picasso.with(context)
                    .load(Constants.MOVIE_API_IMAGE_BASE_URL+movie.getPosterPath()).get();
            remoteViews.setImageViewBitmap(R.id.widget_fav_image,b);
        } catch (IOException e) {
            remoteViews.setImageViewResource(R.id.widget_fav_image,R.drawable.no_image_found);
            e.printStackTrace();
        }
        Gson gson = new Gson();
        String movieStr = gson.toJson(movie);
        Intent fillIntent = new Intent();
        fillIntent.putExtra(Intent.EXTRA_TEXT,movieStr);
        remoteViews.setOnClickFillInIntent(R.id.widget_fav_item,fillIntent);
        return remoteViews;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }
}