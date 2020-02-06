package edu.uci.ics.dtablac.service.movies.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.dtablac.service.movies.models.PersonModel;

public class PeopleSearchModel extends PersonModel {
    @JsonProperty(value = "birthday")
    private String BIRTHDAY;
    @JsonProperty(value = "popularity")
    private Float POPULARITY;
    @JsonProperty(value = "profile_path")
    private String PROFILE_PATH;

    @JsonCreator
    public PeopleSearchModel(@JsonProperty(value = "person_id", required = true) Integer newPERSON_ID,
                             @JsonProperty(value = "name", required = true) String newNAME,
                             @JsonProperty(value = "birthday") String newBIRTHDAY,
                             @JsonProperty(value = "popularity") Float newPOPULARITY,
                             @JsonProperty(value = "profile_path") String newPROFILE_PATH) {
        super(newPERSON_ID, newNAME);
        this.BIRTHDAY = newBIRTHDAY;
        this.POPULARITY = newPOPULARITY;
        this.PROFILE_PATH = newPROFILE_PATH;
    }
    @JsonProperty(value = "birthday")
    public String getBIRTHDAY() { return BIRTHDAY; }
    @JsonProperty(value = "popularity")
    public Float getPOPULARITY() { return POPULARITY; }
    @JsonProperty(value = "profile_path")
    public String getPROFILE_PATH() { return PROFILE_PATH; }
}
