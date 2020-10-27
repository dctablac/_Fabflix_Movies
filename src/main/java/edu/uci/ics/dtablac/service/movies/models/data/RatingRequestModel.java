package edu.uci.ics.dtablac.service.movies.models.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class RatingRequestModel {

    @JsonProperty(value = "movie_id", required = true)
    private String MOVIE_ID;
    @JsonProperty(value = "rating", required = true)
    private Float RATING;

    @JsonCreator
    public RatingRequestModel(@JsonProperty(value = "movie_id", required = true) String newMOVIE_ID,
                              @JsonProperty(value = "rating", required = true) Float newRATING) {
        this.MOVIE_ID = newMOVIE_ID;
        this.RATING = newRATING;
    }

    @JsonProperty(value = "movie_id", required = true)
    public String getMOVIE_ID() { return MOVIE_ID; }
    @JsonProperty(value = "rating", required = true)
    public Float getRATING() { return RATING; }
}
