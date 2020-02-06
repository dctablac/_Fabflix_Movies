package edu.uci.ics.dtablac.service.movies.resources;

import edu.uci.ics.dtablac.service.movies.MoviesService;
import edu.uci.ics.dtablac.service.movies.configs.IdmConfigs;
import edu.uci.ics.dtablac.service.movies.core.PeopleSearchQuery;
import edu.uci.ics.dtablac.service.movies.core.PersonQuery;
import edu.uci.ics.dtablac.service.movies.core.SearchQuery;
import edu.uci.ics.dtablac.service.movies.logger.ServiceLogger;
import edu.uci.ics.dtablac.service.movies.models.MovieModel;
import edu.uci.ics.dtablac.service.movies.models.SearchResponseModel;
import edu.uci.ics.dtablac.service.movies.models.PeopleSearchModel;
import edu.uci.ics.dtablac.service.movies.models.data.PeopleSearchResponseModel;
import edu.uci.ics.dtablac.service.movies.models.data.PersonDetailModel;
import edu.uci.ics.dtablac.service.movies.models.data.PersonDetailResponseModel;
import edu.uci.ics.dtablac.service.movies.util.Utility;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

@Path("people")
public class PeoplePage {

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response searchMoviesByPerson(@Context HttpHeaders headers,
                           @QueryParam("name") String NAME,
                           @QueryParam("limit") Integer LIMIT,
                           @QueryParam("offset") Integer OFFSET,
                           @QueryParam("orderby") String ORDERBY,
                           @QueryParam("direction") String DIRECTION) {
        // Verify query params and default if needed
        LIMIT = Utility.checkLimit(LIMIT);
        OFFSET = Utility.checkOffset(OFFSET, LIMIT);
        ORDERBY = Utility.checkOrderBy(ORDERBY); // Created separate utility function for order by for people.
        DIRECTION = Utility.checkDirection(DIRECTION);

        // Header fields
        String EMAIL = headers.getHeaderString("email");
        String SESSION_ID = headers.getHeaderString("session_ID");
        String TRANSACTION_ID = headers.getHeaderString("transaction_id");

        // Path to privilege
        IdmConfigs idmConfigs = MoviesService.getIdmConfigs();
        String servicePath = Utility.getServicePath(idmConfigs);
        String endpointPath = idmConfigs.getPrivilegePath();

        // Check privilege resultcode
        int privilegeRC = Utility.getPrivilegeLevel(servicePath, endpointPath, EMAIL, 4);

        // Declare response model and fields
        SearchResponseModel responseModel = null;
        ArrayList<MovieModel> movies = new ArrayList<MovieModel>();
        MovieModel movie;
        String movie_id;
        String title;
        Integer year;
        String director;
        Float rating;
        String backdrop_path;
        String poster_path;
        Boolean hidden;

        // Query for existence of person
        ResultSet rsPerson = PersonQuery.sendPersonQuery(NAME, null);
        try {
            if (!rsPerson.next()) {
                responseModel = new SearchResponseModel(211,
                        "No movies found with search parameters.", null);
                return Utility.headerResponse(responseModel, EMAIL, SESSION_ID, TRANSACTION_ID);
            }
        }
        catch (SQLException SQLE) {
            SQLE.printStackTrace();
            ServiceLogger.LOGGER.warning(String.format("Unable to retrieve movies associated with %s.",NAME));
        }

        // Query for movies
        ResultSet rs = SearchQuery.sendSearchQuery(null,null,null,null,null,LIMIT,
                OFFSET,ORDERBY,DIRECTION,NAME);
        try {
            boolean isHidden;
            boolean notPrivileged = privilegeRC != 140;
            while (rs.next()) {
                isHidden = rs.getBoolean("hidden");
                if (isHidden && notPrivileged) {
                    // skip to check next movie
                    continue;
                }
                else if (!isHidden && notPrivileged) {
                    movie_id = rs.getString("MOVIE_ID");
                    title = rs.getString("TITLE");
                    year = rs.getInt("YEAR");
                    director = rs.getString("DIRECTOR");
                    rating = rs.getFloat("RATING");
                    backdrop_path = null;
                    poster_path = null;
                    hidden = null;
                    movie = new MovieModel(movie_id, title, year, director, rating,
                            backdrop_path, poster_path, hidden);
                    movies.add(movie);
                }
                else {
                    movie_id = rs.getString("MOVIE_ID");
                    title = rs.getString("TITLE");
                    year = rs.getInt("YEAR");
                    director = rs.getString("DIRECTOR");
                    rating = rs.getFloat("RATING");
                    backdrop_path = rs.getString("BACKDROP_PATH");
                    poster_path = rs.getString("POSTER_PATH");
                    hidden = rs.getBoolean("HIDDEN");
                    movie = new MovieModel(movie_id, title, year, director, rating,
                            backdrop_path, poster_path, hidden);
                    movies.add(movie);
                }
            }
        }
        catch (SQLException SQLE) {
            SQLE.printStackTrace();
            ServiceLogger.LOGGER.warning(String.format("Unable to retrieve movies associated with %s.",NAME));
        }

        // Convert ArrayList to array to pass in responseModel
        Object[] finalMovies = movies.toArray();

        // Build response with headers and send
        if (finalMovies.length == 0) {
            responseModel = new SearchResponseModel(211,
                    "No movies found with search parameters.", null);
            return Utility.headerResponse(responseModel,EMAIL,SESSION_ID,TRANSACTION_ID);
        }
        responseModel = new SearchResponseModel(210,
                "Found movie(s) with search parameters.", finalMovies);
        return Utility.headerResponse(responseModel,EMAIL,SESSION_ID,TRANSACTION_ID);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Path("search")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response searchPersonBasic(@Context HttpHeaders headers,
                                      @QueryParam("name") String NAME,
                                      @QueryParam("birthday") String BIRTHDAY,
                                      @QueryParam("movie_title") String MOVIE_TITLE,
                                      @QueryParam("limit") Integer LIMIT,
                                      @QueryParam("offset") Integer OFFSET,
                                      @QueryParam("orderby") String ORDERBY,
                                      @QueryParam("direction") String DIRECTION) {
        // Verify query params and set defaults if needed
        LIMIT = Utility.checkLimit(LIMIT);
        OFFSET = Utility.checkOffset(OFFSET, LIMIT);
        ORDERBY = Utility.checkPeopleOrderBy(ORDERBY);
        DIRECTION = Utility.checkDirection(DIRECTION);

        // Header fields
        String EMAIL = headers.getHeaderString("email");
        String SESSION_ID = headers.getHeaderString("session_id");
        String TRANSACTION_ID = headers.getHeaderString("transaction_id");

        // Declare responseModel fields
        PeopleSearchResponseModel responseModel = null;
        ArrayList<PeopleSearchModel> peopleBasic = new ArrayList<PeopleSearchModel>();
        PeopleSearchModel person = null;
        Integer person_id;
        String name;
        String birthday;
        Float popularity;
        String profile_path;

        // Query
        try {
            ResultSet rs = PeopleSearchQuery.sendPersonBasicQuery(NAME, BIRTHDAY, MOVIE_TITLE, LIMIT,
                    OFFSET, ORDERBY, DIRECTION);
            while (rs.next()) {
                person_id = rs.getInt("PERSON_ID");
                name = rs.getString("NAME");
                birthday = rs.getString("BIRTHDAY");
                popularity = rs.getFloat("POPULARITY");
                profile_path = rs.getString("PROFILE_PATH");
                person = new PeopleSearchModel(person_id, name, birthday, popularity, profile_path);
                peopleBasic.add(person);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            ServiceLogger.LOGGER.warning("Unable to retrieve basic information for this person.");
        }
        // Convert to regular array
        Object[] finalPeopleBasic = peopleBasic.toArray();

        // Form responseModel
        if (finalPeopleBasic.length == 0) {
            responseModel = new PeopleSearchResponseModel(213,
                    "No people found with search parameters.", null);
            return Utility.headerResponse(responseModel, EMAIL, SESSION_ID, TRANSACTION_ID);
        }
        responseModel = new PeopleSearchResponseModel(212,
                "Found people with search parameters.", finalPeopleBasic);
        return Utility.headerResponse(responseModel, EMAIL, SESSION_ID, TRANSACTION_ID);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Path("get/{person_id: .*}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getByPersonID(@Context HttpHeaders headers,
                                  @PathParam("person_id") Integer PERSON_ID) {

        PersonDetailResponseModel responseModel = null;
        PersonDetailModel person = null;

        // Header fields
        String EMAIL = headers.getHeaderString("email");
        String SESSION_ID = headers.getHeaderString("session_id");
        String TRANSACTION_ID = headers.getHeaderString("transaction_id");

        // Verify person_id input
        if (PERSON_ID == null) {
            responseModel = new PersonDetailResponseModel(213,
                    "No people found with search parameters.", null);
            return Utility.headerResponse(responseModel, EMAIL, SESSION_ID, TRANSACTION_ID);
        }

        // Declare person fields
        Integer person_id;
        String name;
        String gender;
        String birthday;
        String deathday;
        String biography;
        String birthplace;
        Float popularity;
        String profile_path;

        // Query
        ResultSet rs = PersonQuery.sendPersonQuery(null, PERSON_ID);
        try {
            while (rs.next()) {
                person_id = rs.getInt("PERSON_ID");
                name = rs.getString("NAME");
                gender = rs.getString("GENDER");
                birthday = rs.getString("BIRTHDAY");
                deathday = rs.getString("DEATHDAY");
                biography = rs.getString("BIOGRAPHY");
                birthplace = rs.getString("BIRTHPLACE");
                popularity = rs.getFloat("POPULARITY");
                profile_path = rs.getString("PROFILE_PATH");
                person = new PersonDetailModel(person_id, name, gender, birthday,
                        deathday, biography, birthplace, popularity, profile_path);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            ServiceLogger.LOGGER.warning(String.format("Query fail: Unable to get info on person. ID - %s",PERSON_ID));
        }

        // Construct response model
        if (person == null) {
            responseModel = new PersonDetailResponseModel(213,
                    "No people found with search parameters.", null);
            return Utility.headerResponse(responseModel, EMAIL, SESSION_ID, TRANSACTION_ID);
        }

        // Send response with headers.
        responseModel = new PersonDetailResponseModel(212,
                "Found people with search parameters.", person);
        return Utility.headerResponse(responseModel, EMAIL, SESSION_ID, TRANSACTION_ID);
    }

}
