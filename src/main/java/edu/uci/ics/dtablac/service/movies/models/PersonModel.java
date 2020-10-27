package edu.uci.ics.dtablac.service.movies.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PersonModel {
    @JsonProperty(value = "person_id", required = true)
    private Integer PERSON_ID;
    @JsonProperty(value = "name", required = true)
    private String NAME;

    @JsonCreator
    public PersonModel(@JsonProperty(value = "person_id", required = true) Integer newPERSON_ID,
                       @JsonProperty(value = "name", required = true) String newNAME) {
        this.PERSON_ID = newPERSON_ID;
        this.NAME = newNAME;
    }

    @JsonProperty(value = "person_id")
    public Integer getPERSON_ID() { return PERSON_ID; }
    @JsonProperty(value = "name")
    public String getNAME() { return NAME; }

}
