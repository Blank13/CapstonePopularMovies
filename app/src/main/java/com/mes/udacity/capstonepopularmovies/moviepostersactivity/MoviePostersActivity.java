package com.mes.udacity.capstonepopularmovies.moviepostersactivity;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;

import com.mes.udacity.capstonepopularmovies.detailactivity.MovieDetailActivity;
import com.mes.udacity.capstonepopularmovies.detailactivity.MovieDetailFragment;
import com.mes.udacity.capstonepopularmovies.R;
import com.mes.udacity.capstonepopularmovies.utils.Constants;

import static com.mes.udacity.capstonepopularmovies.utils.StaticMethods.attachPostersFragment;

public class MoviePostersActivity extends AppCompatActivity implements MoviePostersFragment.Callback
        ,MovieDetailFragment.DetailCallBack{

    private FragmentManager fragmentManager;

    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_posters);
        fragmentManager = getSupportFragmentManager();
        attachPostersFragment(fragmentManager,R.id.movie_posters_container);
        if(findViewById(R.id.activity_detail_container) != null){
            mTwoPane = true;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public void onItemSelected(String movieStr) {
        if(mTwoPane){
            Bundle args = new Bundle();
            args.putString(MovieDetailFragment.MOVIE_CALL, movieStr);
            MovieDetailFragment fragment = new MovieDetailFragment();
            fragment.setArguments(args);
            fragmentManager.beginTransaction()
                    .replace(R.id.activity_detail_container,fragment, Constants.DETAIL_FRAGMENT)
                    .commit();
        }
        else {
            Intent intent = new Intent(this,MovieDetailActivity.class)
                    .putExtra(Intent.EXTRA_TEXT, movieStr);
            startActivity(intent);
        }
    }

    @Override
    public void onChangeSort() {
        if(mTwoPane){
            Fragment fragment = fragmentManager.findFragmentByTag(Constants.DETAIL_FRAGMENT);
            if(fragment != null){
                fragmentManager.beginTransaction().detach(fragment).commit();
            }
        }
    }

    @Override
    public void onFavouriteClick() {
        if(mTwoPane){
            Fragment fragment = fragmentManager.findFragmentByTag(Constants.POSTER_FRAGMENT);
            ((MoviePostersFragment)fragment).onFavouriteChange();
        }
    }
}
