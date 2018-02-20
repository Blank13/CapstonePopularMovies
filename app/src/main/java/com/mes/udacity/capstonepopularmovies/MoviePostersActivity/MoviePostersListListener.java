package com.mes.udacity.capstonepopularmovies.MoviePostersActivity;

import com.mes.udacity.capstonepopularmovies.Models.Movie;

import java.util.List;

/**
 * Created by moham on 2/18/2018.
 */

public interface MoviePostersListListener {

    void onPosterListReady(List<Movie> movieList);

    void onPosterError(String message);
}
