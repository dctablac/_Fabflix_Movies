package edu.uci.ics.dtablac.service.movies.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

public class MovieInfoModel extends MovieModel {
    private Integer NUM_VOTES;
    private String BUDGET;
    private String REVENUE;
    private String OVERVIEW;
    private Object[] GENRES; // GenreModel[]
    private Object[] PEOPLE; // PersonModel[]

    @JsonCreator
    public MovieInfoModel(@JsonProperty(value = "movie_id", required = true) String newMOVIE_ID,
                          @JsonProperty(value = "title", required = true) String newTITLE,
                          @JsonProperty(value = "year", required = true) Integer newYEAR,
                          @JsonProperty(value = "director", required = true) String newDIRECTOR,
                          @JsonProperty(value = "rating", required = true) Float newRATING,
                          @JsonProperty(value = "num_votes", required = true) Integer newNUM_VOTES,
                          @JsonProperty(value = "budget") String newBUDGET,
                          @JsonProperty(value = "revenue") String newREVENUE,
                          @JsonProperty(value = "overview") String newOVERVIEW,
                          @JsonProperty(value = "backdrop_path") String newBACKDROP_PATH,
                          @JsonProperty(value = "poster_path") String newPOSTER_PATH,
                          @JsonProperty(value = "hidden") Boolean newHIDDEN,
                          @JsonProperty(value = "genres", required = true) Object[] newGENRES,
                          @JsonProperty(value  ="people", required = true) Object[] newPEOPLE) {
        super(newMOVIE_ID, newTITLE, newYEAR, newDIRECTOR, newRATING, newBACKDROP_PATH, newPOSTER_PATH, newHIDDEN);
        this.NUM_VOTES = newNUM_VOTES;
        this.BUDGET = newBUDGET;
        this.REVENUE = newREVENUE;
        this.OVERVIEW = newOVERVIEW;
        this.GENRES = newGENRES;
        this.PEOPLE = newPEOPLE;
    }
    @JsonProperty(value = "num_votes")
    public Integer getNUM_VOTES() { return NUM_VOTES; }
    @JsonProperty(value = "budget")
    public String getBUDGET() { return BUDGET; }
    @JsonProperty(value = "revenue")
    public String getREVENUE() { return REVENUE; }
    @JsonProperty(value = "overview")
    public String getOVERVIEW() { return OVERVIEW; }
    @JsonProperty(value = "genres")
    public Object[] getGENRES() { return GENRES; }
    @JsonProperty(value = "people")
    public Object[] getPEOPLE() { return PEOPLE; }

    public static void main(String[] args) {
        System.out.println("MovieInfoModel Testing");

        String movie_id = "tt4154796";
        String title = "Avengers: Endgame";
        Integer year = 2019;
        String director = "Joe Russo";
        Float rating = Float.parseFloat("8.6");
        Integer num_votes = 5269;
        String budget = "356000000";
        String revenue = "2485499739";
        String overview = "A cool movie";
        String backdrop_path = "backdrop";
        String poster_path = "poster";
        Boolean hidden = false;

        ArrayList<GenreModel> GMAL = new ArrayList<GenreModel>();
        GenreModel GM1 = new GenreModel(420, "Action");
        GMAL.add(GM1);
        GenreModel GM2 = new GenreModel(421, "Romance");
        GMAL.add(GM2);
        Object[] GMList = GMAL.toArray();

        ArrayList<PersonModel> PMAL = new ArrayList<PersonModel>();
        PersonModel PM1 = new PersonModel(1, "Dre");
        PMAL.add(PM1);
        PersonModel PM2 = new PersonModel(2, "Dan");
        PMAL.add(PM2);
        Object[] PMList = PMAL.toArray();

        MovieInfoModel MIM = new MovieInfoModel(
                movie_id, title, year, director, rating, num_votes, budget,
                revenue, overview, backdrop_path, poster_path, hidden, GMList, PMList);

        System.out.println(MIM.getTITLE());
    }
}
