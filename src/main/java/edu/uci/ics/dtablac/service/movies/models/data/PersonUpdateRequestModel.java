package edu.uci.ics.dtablac.service.movies.models.data;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

public class PersonUpdateRequestModel {
    @JsonProperty(value = "person_id", required = true)
    private Integer PERSON_ID;
    @JsonProperty(value = "name", required = true)
    private String NAME;
    @JsonProperty(value = "gender_id")
    private Integer GENDER_ID;
    @JsonProperty(value = "birthday")
    private Date BIRTHDAY;
    @JsonProperty(value = "deathday")
    private Date DEATHDAY;
    @JsonProperty(value = "biography")
    private String BIOGRAPHY;
    @JsonProperty(value = "birthplace")
    private String BIRTHPLACE;
    @JsonProperty(value = "popularity")
    private Float POPULARITY;
    @JsonProperty(value = "profile_path")
    private String PROFILE_PATH;

    public PersonUpdateRequestModel(@JsonProperty(value = "person_id", required = true) Integer newPERSON_ID,
                                    @JsonProperty(value = "name", required = true) String newNAME,
                                    @JsonProperty(value = "gender_id") Integer newGENDER_ID,
                                    @JsonProperty(value = "birthday") Date newBIRTHDAY,
                                    @JsonProperty(value = "deathday") Date newDEATHDAY,
                                    @JsonProperty(value = "biography") String newBIOGRAPHY,
                                    @JsonProperty(value = "birthplace") String newBIRTHPLACE,
                                    @JsonProperty(value = "popularity") Float newPOPULARITY,
                                    @JsonProperty(value = "profile_path") String newPROFILE_PATH) {
        this.PERSON_ID = newPERSON_ID;
        this.NAME = newNAME;
        this.GENDER_ID = newGENDER_ID;
        this.BIRTHDAY = newBIRTHDAY;
        this.DEATHDAY = newDEATHDAY;
        this.BIOGRAPHY = newBIOGRAPHY;
        this.BIRTHPLACE = newBIRTHPLACE;
        this.POPULARITY = newPOPULARITY;
        this.PROFILE_PATH = newPROFILE_PATH;
    }

    @JsonProperty(value = "person_id")
    public Integer getPERSON_ID() { return PERSON_ID; }
    @JsonProperty(value = "name")
    public String getNAME() { return NAME; }
    @JsonProperty(value = "gender_id")
    public Integer getGENDER_ID() { return GENDER_ID; }
    @JsonProperty(value = "birthday")
    public Date getBIRTHDAY() { return BIRTHDAY; }
    @JsonProperty(value = "deathday")
    public Date getDEATHDAY() { return DEATHDAY; }
    @JsonProperty(value = "biography")
    public String getBIOGRAPHY() { return BIOGRAPHY; }
    @JsonProperty(value = "birthplace")
    public String getBIRTHPLACE() { return BIRTHPLACE; }
    @JsonProperty(value = "popularity")
    public Float getPOPULARITY() { return POPULARITY; }
    @JsonProperty(value = "profile_path")
    public String getPROFILE_PATH() { return PROFILE_PATH; }
}
