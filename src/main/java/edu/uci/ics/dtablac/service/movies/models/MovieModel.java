package edu.uci.ics.dtablac.service.movies.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class MovieModel {
    @JsonProperty(value = "movie_id", required = true)
    private String MOVIE_ID;
    @JsonProperty(value = "title", required = true)
    private String TITLE;
    @JsonProperty(value = "year", required = true)
    private Integer YEAR;
    @JsonProperty(value = "director", required = true)
    private String DIRECTOR;
    @JsonProperty(value = "rating", required = true)
    private Float RATING;
    @JsonProperty(value = "backdrop_path")
    private String BACKDROP_PATH;
    @JsonProperty(value = "poster_path")
    private String POSTER_PATH;
    @JsonProperty(value = "hidden")
    private Boolean HIDDEN;

    public MovieModel() {}

    @JsonCreator
    public MovieModel(@JsonProperty(value = "movie_id", required = true) String newMOVIE_ID,
                      @JsonProperty(value = "title", required = true) String newTITLE,
                      @JsonProperty(value = "year", required = true) Integer newYEAR,
                      @JsonProperty(value = "director", required = true) String newDIRECTOR,
                      @JsonProperty(value = "rating", required = true) Float newRATING,
                      @JsonProperty(value = "backdrop_path") String newBACKDROP_PATH,
                      @JsonProperty(value = "poster_path") String newPOSTER_PATH,
                      @JsonProperty(value = "hidden") Boolean newHIDDEN) {
        this.MOVIE_ID = newMOVIE_ID;
        this.TITLE = newTITLE;
        this.YEAR = newYEAR;
        this.DIRECTOR = newDIRECTOR;
        this.RATING = newRATING;
        this.BACKDROP_PATH = newBACKDROP_PATH;
        this.POSTER_PATH = newPOSTER_PATH;
        this.HIDDEN = newHIDDEN;
    }

    @JsonProperty(value = "movie_id")
    public String getMOVIE_ID() {
        return MOVIE_ID;
    }
    @JsonProperty(value = "title")
    public String getTITLE() {
        return TITLE;
    }
    @JsonProperty(value = "year")
    public Integer getYEAR() {
        return YEAR;
    }
    @JsonProperty(value = "director")
    public String getDIRECTOR() {
        return DIRECTOR;
    }
    @JsonProperty(value = "rating")
    public Float getRATING() { return RATING; }
    @JsonProperty(value = "backdrop_path")
    public String getBACKDROP_PATH() {
        return BACKDROP_PATH;
    }
    @JsonProperty(value = "poster_path")
    public String getPOSTER_PATH() {
        return POSTER_PATH;
    }
    @JsonProperty(value = "hidden")
    public Boolean isHIDDEN() {
        return HIDDEN;
    }
    @JsonProperty(value = "movie_id")
    public void setMOVIE_ID(String MOVIE_ID) {
        this.MOVIE_ID = MOVIE_ID;
    }
    @JsonProperty(value = "title")
    public void setTITLE(String TITLE) {
        this.TITLE = TITLE;
    }
    @JsonProperty(value = "year")
    public void setYEAR(Integer YEAR) {
        this.YEAR = YEAR;
    }
    @JsonProperty(value = "director")
    public void setDIRECTOR(String DIRECTOR) {
        this.DIRECTOR = DIRECTOR;
    }
    @JsonProperty(value = "rating")
    public void setRATING(Float RATING) {
        this.RATING = RATING;
    }
    @JsonProperty(value = "backdrop_path")
    public void setBACKDROP_PATH(String BACKDROP_PATH) {
        this.BACKDROP_PATH = BACKDROP_PATH;
    }
    @JsonProperty(value = "poster_path")
    public void setPOSTER_PATH(String POSTER_PATH) {
        this.POSTER_PATH = POSTER_PATH;
    }
    @JsonProperty(value = "hidden")
    public void setHIDDEN(Boolean HIDDEN) {
        this.HIDDEN = HIDDEN;
    }
}
