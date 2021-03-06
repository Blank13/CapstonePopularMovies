package com.mes.udacity.capstonepopularmovies.response;

import com.google.gson.annotations.SerializedName;
import com.mes.udacity.capstonepopularmovies.models.Trailer;

import java.util.List;

/**
 * Created by moham on 2/18/2018.
 */

public class TrailerResponse {

    @SerializedName("results")
    private List<Trailer> trailers;

    public List<Trailer> getTrailers() {
        return trailers;
    }

    public void setTrailers(List<Trailer> trailers) {
        this.trailers = trailers;
    }
}
