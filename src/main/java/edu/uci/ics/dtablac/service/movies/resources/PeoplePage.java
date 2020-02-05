package edu.uci.ics.dtablac.service.movies.resources;

import edu.uci.ics.dtablac.service.movies.MoviesService;
import edu.uci.ics.dtablac.service.movies.configs.IdmConfigs;
import edu.uci.ics.dtablac.service.movies.core.PersonQuery;
import edu.uci.ics.dtablac.service.movies.logger.ServiceLogger;
import edu.uci.ics.dtablac.service.movies.models.MovieModel;
import edu.uci.ics.dtablac.service.movies.models.SearchResponseModel;
import edu.uci.ics.dtablac.service.movies.util.Utility;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import static edu.uci.ics.dtablac.service.movies.core.SearchQuery.sendSearchQuery;

@Path("people")
public class PeoplePage {
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
        ORDERBY = Utility.checkOrderBy(ORDERBY);
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

        // Query for person
        ResultSet rsPerson = PersonQuery.sendPersonQuery(NAME);
        try {
            if (!rsPerson.next()) {
                responseModel = new SearchResponseModel(213,
                        "No people found with search parameters.", null);
                return Utility.headerResponse(responseModel, EMAIL, SESSION_ID, TRANSACTION_ID);
            }
        }
        catch (SQLException SQLE) {
            SQLE.printStackTrace();
            ServiceLogger.LOGGER.warning("Unable to validate if the person exists.");
        }

        // Query for movies
        ResultSet rs = sendSearchQuery(null,null,null,null,null,LIMIT,
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
            ServiceLogger.LOGGER.warning("Unable to retrieve movies associated with name.");
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

    /*@Path("search")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response searchPersonBasic(@Context HttpHeaders headers,
                                      @QueryParam())
*/



}
