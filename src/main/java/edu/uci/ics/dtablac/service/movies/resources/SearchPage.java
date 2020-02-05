package edu.uci.ics.dtablac.service.movies.resources;

import edu.uci.ics.dtablac.service.movies.MoviesService;
import edu.uci.ics.dtablac.service.movies.configs.IdmConfigs;
import edu.uci.ics.dtablac.service.movies.logger.ServiceLogger;
import edu.uci.ics.dtablac.service.movies.models.MovieModel;
import edu.uci.ics.dtablac.service.movies.models.SearchResponseModel;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import static edu.uci.ics.dtablac.service.movies.core.SearchQuery.buildSearchQuery;
import static edu.uci.ics.dtablac.service.movies.util.Utility.*;

@Path("search")
public class SearchPage {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response search(@Context HttpHeaders headers,
                           @QueryParam("title") String TITLE,
                           @QueryParam("year") Integer YEAR,
                           @QueryParam("director") String DIRECTOR,
                           @QueryParam("genre") String GENRE,
                           @QueryParam("hidden") Boolean HIDDEN,
                           @QueryParam("limit") Integer LIMIT,
                           @QueryParam("offset") Integer OFFSET,
                           @QueryParam("orderby") String ORDERBY,
                           @QueryParam("direction") String DIRECTION){
        LIMIT = checkLimit(LIMIT);
        ORDERBY = checkOrderBy(ORDERBY);
        DIRECTION = checkDirection(DIRECTION);
        OFFSET = checkOffset(OFFSET, LIMIT);

        IdmConfigs idmConfigs = MoviesService.getIdmConfigs();
        String servicePath = idmConfigs.getScheme()+idmConfigs.getHostName()+":"
                            +idmConfigs.getPort()+idmConfigs.getPath(); // servicePath
        String endpointPath = idmConfigs.getPrivilegePath(); // endpointPath;

        // Get header strings
        String EMAIL = headers.getHeaderString("email");
        String SESSION_ID = headers.getHeaderString("session_id");
        String TRANSACTION_ID = headers.getHeaderString("transaction_id");

        SearchResponseModel responseModel = null;

        // Returns resultCode of privilege request. If RC = 140, plevel is sufficient to see hidden movies.
        int privilegeRC = getPrivilegeLevel(servicePath, endpointPath, EMAIL, 4);

        if (HIDDEN != null) {
            if (HIDDEN = true) { // User requests hidden movies shown.
                if (privilegeRC == 140) { // If privileged, show all movies.
                    HIDDEN = null; // Passing null means movies will not be restricted for 'hidden' attribute.
                }
                else {
                    HIDDEN = false; // Only show movies that have hidden:false.
                }
            }
            else {
                HIDDEN = false; // User set hidden to be false. NOTE: maybe this case doesn't check?
            }
        }
        else {
            HIDDEN = false; // User does not query for hidden, regardless of privilege, so don't show hidden.
        }



        // Query headers
        try {
            String query = buildSearchQuery(TITLE, YEAR, DIRECTOR, GENRE, HIDDEN, LIMIT,
                                            OFFSET, ORDERBY, DIRECTION, null);
            PreparedStatement ps = MoviesService.getCon().prepareStatement(query);
            ServiceLogger.LOGGER.info("Trying query: "+ps.toString());
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query succeeded.");

            ArrayList<MovieModel> movies = new ArrayList<MovieModel>();

            while (rs.next()) {
                MovieModel newMovie = new MovieModel();

                String movie_id = rs.getString("MOVIE_ID");
                newMovie.setMOVIE_ID(movie_id);
                String title = rs.getString("TITLE");
                newMovie.setTITLE(title);
                Integer year = Integer.parseInt(rs.getString("YEAR"));
                newMovie.setYEAR(year);
                String director = rs.getString("DIRECTOR");
                newMovie.setDIRECTOR(director);
                Float rating = Float.parseFloat(rs.getString("RATING"));
                newMovie.setRATING(rating);
                String backdrop_path = rs.getString("BACKDROP_PATH");
                newMovie.setBACKDROP_PATH(backdrop_path);
                String poster_path = rs.getString("POSTER_PATH");
                newMovie.setPOSTER_PATH(poster_path);
                Boolean hidden = Boolean.parseBoolean(rs.getString("HIDDEN"));
                newMovie.setHIDDEN(hidden);
                if (privilegeRC != 140) {
                    newMovie.setHIDDEN(null);
                    newMovie.setPOSTER_PATH(null);
                    newMovie.setBACKDROP_PATH(null);
                }
                movies.add(newMovie);
            }
            Object[] moviesArray = movies.toArray();
            if (moviesArray.length == 0) {
                responseModel = new SearchResponseModel(211,
                        "No movies found with search parameters.", null);
                ServiceLogger.LOGGER.warning("No movies found with search parameters.");
            }
            else {
                responseModel = new SearchResponseModel(210,
                        "Found movie(s) with search parameters.", moviesArray);
                ServiceLogger.LOGGER.info("Found movie(s) with search parameters.");
            }
        }
        catch (SQLException SQLE) {
            ServiceLogger.LOGGER.warning("Query failed: Unable to retrieve movies.");
            SQLE.printStackTrace();
        }

        // Return a response with same headers
        return headerResponse(responseModel, EMAIL, SESSION_ID, TRANSACTION_ID);
    }

}
