package edu.uci.ics.dtablac.service.movies.models.base;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class RequestModel {
    @JsonProperty(value = "movie_ids", required = true)
    private String[] MOVIE_IDS;

    @JsonCreator
    public RequestModel(@JsonProperty(value = "movie_ids", required = true) String[] newMOVIE_IDS) {
        this.MOVIE_IDS = newMOVIE_IDS;
    }

    @JsonProperty(value = "movie_ids")
    public String[] getMOVIE_IDS() { return MOVIE_IDS; }
}
