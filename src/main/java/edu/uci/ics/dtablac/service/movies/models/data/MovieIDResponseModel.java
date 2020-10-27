package edu.uci.ics.dtablac.service.movies.models.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.dtablac.service.movies.models.MovieModel;
import edu.uci.ics.dtablac.service.movies.models.base.ResponseModel;

public class MovieIDResponseModel extends ResponseModel {
    @JsonProperty(value = "movie", required = true)
    private MovieModel MOVIE;

    @JsonCreator
    public MovieIDResponseModel(@JsonProperty(value = "resultCode", required = true) Integer newRESULTCODE,
                                @JsonProperty(value = "message", required = true) String newMESSAGE,
                                @JsonProperty(value = "movie", required = true) MovieModel newMOVIE) {
        super(newRESULTCODE, newMESSAGE);
        this.MOVIE = newMOVIE;
    }

    @JsonProperty(value = "movie")
    public MovieModel getMOVIE() { return MOVIE; }
}
