package edu.uci.ics.dtablac.service.movies.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class GenreModel {
    @JsonProperty(value = "genre_id")
    private Integer GENRE_ID;
    @JsonProperty(value = "name")
    private String NAME;

    @JsonCreator
    public GenreModel(@JsonProperty(value = "genre_id", required = true) Integer newGENRE_ID,
                      @JsonProperty(value = "name", required = true) String newNAME) {
        this.GENRE_ID = newGENRE_ID;
        this.NAME = newNAME;
    }

    @JsonProperty(value = "genre_id")
    public Integer getGENRE_ID() { return GENRE_ID; }
    @JsonProperty(value = "name")
    public String getNAME() { return NAME; }

}
