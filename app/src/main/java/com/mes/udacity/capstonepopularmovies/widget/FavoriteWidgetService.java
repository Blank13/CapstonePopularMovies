package com.mes.udacity.capstonepopularmovies.widget;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.mes.udacity.capstonepopularmovies.R;

/**
 * Created by moham on 2/20/2018.
 */

public class FavoriteWidgetService extends IntentService {

    public static final String ACTION_UPDATE_FAVORITE_WIDGET
            = "com.mes.udacity.capstonepopularmovies.action.update_favourite_widget";

    public FavoriteWidgetService() {
        super("FavouriteMoviesWidgetService");
    }

    public static void startActionUpdateFavouriteWidgets(Context context) {
        Intent intent = new Intent(context, FavoriteWidgetService.class);
        intent.setAction(ACTION_UPDATE_FAVORITE_WIDGET);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_UPDATE_FAVORITE_WIDGET.equals(action)) {
                handleActionUpdateFavoriteMoviesWidgets();
            }
        }
    }

    private void handleActionUpdateFavoriteMoviesWidgets() {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this
                , FavoriteWidgetProvider.class));
        //Trigger data update to handle the ListView widgets and force a data refresh
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_fav_listview);
        //Now update all widgets
        FavoriteWidgetProvider.updateFavoriteMoviesWidgets(this, appWidgetManager, appWidgetIds);
    }
}
