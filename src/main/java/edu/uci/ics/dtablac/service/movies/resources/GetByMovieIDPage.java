package edu.uci.ics.dtablac.service.movies.resources;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import edu.uci.ics.dtablac.service.movies.MoviesService;
import edu.uci.ics.dtablac.service.movies.configs.IdmConfigs;
import edu.uci.ics.dtablac.service.movies.core.MovieIDQuery;
import edu.uci.ics.dtablac.service.movies.logger.ServiceLogger;
import edu.uci.ics.dtablac.service.movies.models.GenreModel;
import edu.uci.ics.dtablac.service.movies.models.MovieInfoModel;
import edu.uci.ics.dtablac.service.movies.models.PersonModel;
import edu.uci.ics.dtablac.service.movies.models.data.MovieIDResponseModel;
import edu.uci.ics.dtablac.service.movies.util.Utility;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import static edu.uci.ics.dtablac.service.movies.util.Utility.getPrivilegeLevel;

@Path("get/{movie_id: .*}")
public class GetByMovieIDPage {
    @GET
    @Produces(MediaType.APPLICATION_JSON)

    public Response getByMovieID(@Context HttpHeaders headers,
                                 @PathParam("movie_id") String MOVIE_ID) {
        MovieIDResponseModel responseModel = null;

        // Request header fields
        String EMAIL = headers.getHeaderString("email");
        String SESSION_ID = headers.getHeaderString("session_id");
        String TRANSACTION_ID = headers.getHeaderString("transaction_id");

        // Check if valid movie id.
        if (MOVIE_ID.isEmpty()) {
            responseModel = new MovieIDResponseModel(211,
                    "No movies found with search parameters.", null);
            return Utility.headerResponse(responseModel, EMAIL, SESSION_ID, TRANSACTION_ID);
        }


        // Path to /idm/privilege
        IdmConfigs idmConfigs = MoviesService.getIdmConfigs();
        String servicePath = idmConfigs.getScheme()+idmConfigs.getHostName()+":"+
                             idmConfigs.getPort()+idmConfigs.getPath();
        String endpointPath = idmConfigs.getPrivilegePath();

        // Store resultCode that determines whether a user is able
        // to view this movie and its information.
        int privilegeRC = getPrivilegeLevel(servicePath, endpointPath, EMAIL, 4);
        boolean privilegeRestricted = false;
        boolean movieInfoAcquired = false;

        //// Response Model fields
        // Movie information
        String movie_id = null;
        String title = null;
        Integer year = null;
        String director = null;
        Float rating = null;
        Integer num_votes = null;
        String budget = null;
        String revenue = null;
        String overview = null;
        String backdrop_path = null;
        String poster_path = null;
        Boolean hidden = null;

        // Stores genre info
        Integer genre_id;
        String gname;

        // Stores person info
        Integer person_id;
        String pname;

        // Return this in responseModel as Object[] arrays
        MovieInfoModel movie = null;
        ArrayList<GenreModel> genres = new ArrayList<GenreModel>(); // within movieInfo
        ArrayList<PersonModel> people = new ArrayList<PersonModel>(); // within movieInfo

        try { // TODO: Maybe not use a while loop for movie info. Only one movie should be returned.
            /// Run movie info query
            ResultSet rsMovieInfo = MovieIDQuery.sendMovieIDQuery(MOVIE_ID, true,
                    false, false);
            while (rsMovieInfo.next()) {
                boolean isHidden = rsMovieInfo.getBoolean("hidden");
                // Check if user can see this potentially hidden movie.
                if (isHidden) {
                    if (privilegeRC != 140) {
                        responseModel = new MovieIDResponseModel(211,
                                "No movies found with search parameters.", null);
                        return Utility.headerResponse(responseModel, EMAIL, SESSION_ID, TRANSACTION_ID);
                    }
                } else { // Movie is not hidden
                    movie_id = rsMovieInfo.getString("MOVIE_ID");
                    title = rsMovieInfo.getString("TITLE");
                    year = rsMovieInfo.getInt("YEAR");
                    director = rsMovieInfo.getString("DIRECTOR");
                    rating = rsMovieInfo.getFloat("RATING");
                    num_votes = rsMovieInfo.getInt("NUM_VOTES");
                    // move below items to test spot
                    budget = rsMovieInfo.getString("BUDGET");
                    revenue = rsMovieInfo.getString("REVENUE");
                    overview = rsMovieInfo.getString("OVERVIEW");
                    backdrop_path = rsMovieInfo.getString("BACKDROP_PATH");
                    poster_path = rsMovieInfo.getString("POSTER_PATH");

                    // If user not privileged, remain null
                    if (privilegeRC == 140) {
                        // test
                        hidden = rsMovieInfo.getBoolean("HIDDEN");
                    }
                }
                movieInfoAcquired = true;
            }


            // Check if movie was hidden and user could not access the movie.
            if (!movieInfoAcquired) {
                responseModel = new MovieIDResponseModel(211,
                        "No movies found with search parameters.", null);
                return Utility.headerResponse(responseModel, EMAIL, SESSION_ID, TRANSACTION_ID);
            }

            /// Run genre query
            if (!privilegeRestricted) {
                ResultSet rsGenre = MovieIDQuery.sendMovieIDQuery(MOVIE_ID, false,
                        true, false);
                while (rsGenre.next()) {
                    genre_id = rsGenre.getInt("GENRE_ID");
                    gname = rsGenre.getString("GNAME");
                    GenreModel genre = new GenreModel(genre_id, gname);
                    genres.add(genre);
                }
            }

            /// Run people query
            if (!privilegeRestricted) {
                ResultSet rsPerson = MovieIDQuery.sendMovieIDQuery(MOVIE_ID, false,
                        false, true);
                while (rsPerson.next()) {
                    person_id = rsPerson.getInt("PERSON_ID");
                    pname = rsPerson.getString("PNAME");
                    PersonModel person = new PersonModel(person_id, pname);
                    people.add(person);
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            ServiceLogger.LOGGER.warning("Unable to get result for movie.");
        }

        Object[] finalGenres = genres.toArray();
        Object[] finalPeople = people.toArray();

        movie = new MovieInfoModel(movie_id, title, year, director, rating, num_votes, budget, revenue,
                overview, backdrop_path, poster_path, hidden, finalGenres, finalPeople);
        responseModel = new MovieIDResponseModel(210,
                "Found movie(s) with search paramters.", movie);

        /// Return a response with the headers.
        return Utility.headerResponse(responseModel, EMAIL, SESSION_ID, TRANSACTION_ID);
    }
}
