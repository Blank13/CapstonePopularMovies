package com.mes.udacity.capstonepopularmovies.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.RemoteViews;

import com.mes.udacity.capstonepopularmovies.detailactivity.MovieDetailActivity;
import com.mes.udacity.capstonepopularmovies.R;

/**
 * Created by moham on 2/20/2018.
 */

public class FavoriteWidgetProvider extends AppWidgetProvider{

    private static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                        int appWidgetId) {

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.favorite_widget);
        Intent intent = new Intent(context, FavoriteWidgetListService.class);
        views.setRemoteAdapter(R.id.widget_fav_listview, intent);
        Intent appIntent = new Intent(context, MovieDetailActivity.class);
        PendingIntent appPendingIntent = PendingIntent.getActivity(context, 0, appIntent
                , PendingIntent.FLAG_UPDATE_CURRENT);
        views.setPendingIntentTemplate(R.id.widget_fav_listview, appPendingIntent);
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        //Start the intent service update widget action, the service takes care of updating the widgets UI
        //This check because the service doesn't work on Oreo.
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            FavoriteWidgetService.startActionUpdateFavouriteWidgets(context);
        }

    }

    public static void updateFavoriteMoviesWidgets(Context context,
                                                   AppWidgetManager appWidgetManager,
                                                   int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }
}
