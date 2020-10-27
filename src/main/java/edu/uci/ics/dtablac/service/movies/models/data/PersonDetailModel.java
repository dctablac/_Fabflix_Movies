package edu.uci.ics.dtablac.service.movies.models.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.dtablac.service.movies.models.PersonModel;

public class PersonDetailModel extends PersonModel {
    @JsonProperty(value = "gender")
    private String GENDER;
    @JsonProperty(value = "birthday")
    private String BIRTHDAY;
    @JsonProperty(value = "deathday")
    private String DEATHDAY;
    @JsonProperty(value = "biography")
    private String BIOGRAPHY;
    @JsonProperty(value = "birthplace")
    private String BIRTHPLACE;
    @JsonProperty(value = "popularity")
    private Float POPULARITY;
    @JsonProperty(value = "profile_path")
    private String PROFILE_PATH;

    @JsonCreator
    public PersonDetailModel(@JsonProperty(value = "person_id", required = true) Integer newPERSON_ID,
                             @JsonProperty(value = "name", required = true) String newNAME,
                             @JsonProperty(value = "gender") String newGENDER,
                             @JsonProperty(value = "birthday") String newBIRTHDAY,
                             @JsonProperty(value = "deathday") String newDEATHDAY,
                             @JsonProperty(value = "biography") String newBIOGRAPHY,
                             @JsonProperty(value = "birthplace ") String newBIRTHPLACE,
                             @JsonProperty(value = "popularity") Float newPOPULARITY,
                             @JsonProperty(value = "profile_path") String newPROFILE_PATH) {
        super(newPERSON_ID, newNAME);
        this.GENDER = newGENDER;
        this.BIRTHDAY = newBIRTHDAY;
        this.DEATHDAY = newDEATHDAY;
        this.BIOGRAPHY = newBIOGRAPHY;
        this.BIRTHPLACE = newBIRTHPLACE;
        this.POPULARITY = newPOPULARITY;
        this.PROFILE_PATH = newPROFILE_PATH;
    }

    @JsonProperty(value = "gender")
    public String getGENDER() { return GENDER; }
    @JsonProperty(value = "birthday")
    public String getBIRTHDAY() { return BIRTHDAY; }
    @JsonProperty(value = "deathday")
    public String getDEATHDAY() { return DEATHDAY; }
    @JsonProperty(value = "biography")
    public String getBIOGRAPHY() { return BIOGRAPHY; }
    @JsonProperty(value = "birthplace")
    public String getBIRTHPLACE() { return BIRTHPLACE; }
    @JsonProperty(value = "popularity")
    public Float getPOPULARITY() { return POPULARITY; }
    @JsonProperty(value = "profile_path")
    public String getPROFILE_PATH() { return PROFILE_PATH; }
}
