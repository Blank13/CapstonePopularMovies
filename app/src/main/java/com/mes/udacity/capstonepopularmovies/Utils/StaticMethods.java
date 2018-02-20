package com.mes.udacity.capstonepopularmovies.Utils;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.mes.udacity.capstonepopularmovies.DetailActivity.MovieDetailFragment;
import com.mes.udacity.capstonepopularmovies.MoviePostersActivity.MoviePostersFragment;

/**
 * Created by moham on 2/18/2018.
 */

public class StaticMethods {

    public static void attachPostersFragment(FragmentManager fragmentManager, int container) {
        Fragment fragment = fragmentManager.findFragmentByTag(Constants.POSTER_FRAGMENT);
        if (fragment == null) {
            fragment = new MoviePostersFragment();
        }
        fragmentManager.beginTransaction()
                .replace(container, fragment, Constants.POSTER_FRAGMENT)
                .commit();
    }

    public static void attachDetailFragment(FragmentManager fragmentManager, int container) {
        Fragment fragment = fragmentManager.findFragmentByTag(Constants.DETAIL_FRAGMENT);
        if (fragment == null) {
            fragment = new MovieDetailFragment();
        }
        fragmentManager.beginTransaction()
                .replace(container, fragment, Constants.DETAIL_FRAGMENT)
                .commit();
    }

    public static boolean haveNetworkConnection(Activity activity) {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) activity
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI")) {
                if (ni.isConnected()) {
                    haveConnectedWifi = true;
                }
            }
            else if (ni.getTypeName().equalsIgnoreCase("MOBILE")) {
                if (ni.isConnected()) {
                    haveConnectedMobile = true;
                }
            }
        }
        return haveConnectedWifi || haveConnectedMobile;
    }
}
