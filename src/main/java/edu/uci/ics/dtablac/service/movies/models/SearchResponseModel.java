package edu.uci.ics.dtablac.service.movies.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SearchResponseModel {
    @JsonProperty(value = "resultCode", required = true)
    private Integer RESULTCODE;
    @JsonProperty(value = "message", required = true)
    private String MESSAGE;
    @JsonProperty(value = "movies", required = true)
    private Object[] MOVIES;

    @JsonCreator
    public SearchResponseModel(@JsonProperty(value = "resultCode", required = true) Integer newRESULTCODE,
                               @JsonProperty(value = "message", required = true) String newMESSAGE,
                               @JsonProperty(value = "movies", required = true) Object[] newMOVIES){
        this.RESULTCODE = newRESULTCODE;
        this.MESSAGE = newMESSAGE;
        this.MOVIES = newMOVIES;
    }

    @JsonProperty(value = "resultCode")
    public Integer getRESULTCODE() {
        return RESULTCODE;
    }
    @JsonProperty(value = "message")
    public String getMESSAGE() {
        return MESSAGE;
    }
    @JsonProperty(value = "movies")
    public Object[] getMOVIES() {
        return MOVIES;
    }
}