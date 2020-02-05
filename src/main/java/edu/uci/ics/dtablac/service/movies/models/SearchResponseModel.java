package edu.uci.ics.dtablac.service.movies.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.dtablac.service.movies.models.base.ResponseModel;


public class SearchResponseModel extends ResponseModel {
    @JsonProperty(value = "movies", required = true)
    private Object[] MOVIES;

    @JsonCreator
    public SearchResponseModel(@JsonProperty(value = "resultCode", required = true) Integer newRESULTCODE,
                               @JsonProperty(value = "message", required = true) String newMESSAGE,
                               @JsonProperty(value = "movies", required = true) Object[] newMOVIES){
        super(newRESULTCODE, newMESSAGE);
        this.MOVIES = newMOVIES;
    }

    @JsonProperty(value = "movies")
    public Object[] getMOVIES() {
        return MOVIES;
    }
}
