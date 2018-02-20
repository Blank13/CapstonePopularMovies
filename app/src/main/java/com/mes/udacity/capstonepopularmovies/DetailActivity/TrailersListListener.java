package com.mes.udacity.capstonepopularmovies.DetailActivity;

import com.mes.udacity.capstonepopularmovies.Models.Trailer;

import java.util.List;

/**
 * Created by moham on 2/18/2018.
 */

public interface TrailersListListener {

    void onTrailerListReady(List<Trailer> trailerList);

    void onTrailerError(String message);
}
