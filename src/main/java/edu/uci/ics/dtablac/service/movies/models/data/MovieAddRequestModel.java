package edu.uci.ics.dtablac.service.movies.models.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class MovieAddRequestModel {
    @JsonProperty(value = "title", required = true)
    private String TITLE;
    @JsonProperty(value = "year", required = true)
    private Integer YEAR;
    @JsonProperty(value = "director", required = true)
    private String DIRECTOR;
    @JsonProperty(value = "rating")
    private Float RATING;
    @JsonProperty(value = "num_votes")
    private Integer NUM_VOTES;
    @JsonProperty(value = "budget")
    private String BUDGET;
    @JsonProperty(value = "revenue")
    private String REVENUE;
    @JsonProperty(value = "overview")
    private String OVERVIEW;
    @JsonProperty(value = "backdrop_path")
    private String BACKDROP_PATH;
    @JsonProperty(value = "poster_path")
    private String POSTER_PATH;
    @JsonProperty(value = "hidden")
    private Boolean HIDDEN;

    @JsonCreator
    public MovieAddRequestModel(@JsonProperty(value = "title", required = true) String newTITLE,
                                @JsonProperty(value = "year", required = true) Integer newYEAR,
                                @JsonProperty(value = "director", required = true) String newDIRECTOR,
                                @JsonProperty(value = "rating") Float newRATING,
                                @JsonProperty(value = "num_votes") Integer newNUM_VOTES,
                                @JsonProperty(value = "budget") String newBUDGET,
                                @JsonProperty(value = "revenue") String newREVENUE,
                                @JsonProperty(value = "overview") String newOVERVIEW,
                                @JsonProperty(value = "backdrop_path") String newBACKDROP_PATH,
                                @JsonProperty(value = "poster_path") String newPOSTER_PATH,
                                @JsonProperty(value = "hidden") Boolean newHIDDEN) {
        this.TITLE = newTITLE;
        this.YEAR = newYEAR;
        this.DIRECTOR = newDIRECTOR;
        this.RATING = newRATING;
        this.NUM_VOTES = newNUM_VOTES;
        this.BUDGET = newBUDGET;
        this.REVENUE = newREVENUE;
        this.OVERVIEW = newOVERVIEW;
        this.BACKDROP_PATH = newBACKDROP_PATH;
        this.POSTER_PATH = newPOSTER_PATH;
        this.HIDDEN = newHIDDEN;
    }

    @JsonProperty(value = "title")
    public String getTITLE() { return TITLE; }
    @JsonProperty(value = "year")
    public Integer getYEAR() { return YEAR; }
    @JsonProperty(value = "director")
    public String getDIRECTOR() { return DIRECTOR; }
    @JsonProperty(value = "rating")
    public Float getRATING() { return RATING; }
    @JsonProperty(value = "num_votes")
    public Integer getNUM_VOTES() { return NUM_VOTES; }
    @JsonProperty(value = "budget")
    public String getBUDGET() { return BUDGET; }
    @JsonProperty(value = "revenue")
    public String getREVENUE() { return REVENUE; }
    @JsonProperty(value = "overview")
    public String getOVERVIEW() { return OVERVIEW; }
    @JsonProperty(value = "backdrop_path")
    public String getBACKDROP_PATH() { return BACKDROP_PATH; }
    @JsonProperty(value = "poster_path")
    public String getPOSTER_PATH() { return POSTER_PATH; }
    @JsonProperty(value = "hidden")
    public Boolean getHIDDEN() { return HIDDEN; }
}
