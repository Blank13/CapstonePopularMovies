package com.mes.udacity.capstonepopularmovies.moviepostersactivity;

import com.mes.udacity.capstonepopularmovies.models.Movie;

import java.util.List;

/**
 * Created by moham on 2/18/2018.
 */

interface MoviePostersListListener {

    void onPosterListReady(List<Movie> movieList);

    void onPosterError(String message);
}
