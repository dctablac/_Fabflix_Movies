package edu.uci.ics.dtablac.service.movies.resources;

import edu.uci.ics.dtablac.service.movies.MoviesService;
import edu.uci.ics.dtablac.service.movies.configs.IdmConfigs;
import edu.uci.ics.dtablac.service.movies.logger.ServiceLogger;
import edu.uci.ics.dtablac.service.movies.models.BrowseResponseModel;
import edu.uci.ics.dtablac.service.movies.models.MovieModel;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import static edu.uci.ics.dtablac.service.movies.core.BrowseQuery.buildBrowseQuery;
import static edu.uci.ics.dtablac.service.movies.util.Utility.*;

@Path("browse/{phrase: .*}")
public class BrowsePage {
    // @Path("")
    @GET
    @Produces(MediaType.APPLICATION_JSON)

    public Response browse(@Context HttpHeaders headers,
                           @PathParam("phrase") String PHRASE,
                           @QueryParam("limit") Integer LIMIT,
                           @QueryParam("offset") Integer OFFSET,
                           @QueryParam("orderby") String ORDERBY,
                           @QueryParam("direction") String DIRECTION) {
        LIMIT = checkLimit(LIMIT);
        ORDERBY = checkOrderBy(ORDERBY);
        DIRECTION = checkDirection(DIRECTION);
        OFFSET = checkOffset(OFFSET, LIMIT);

        IdmConfigs idmConfigs = MoviesService.getIdmConfigs();
        String servicePath = idmConfigs.getScheme() + idmConfigs.getHostName() + ":"
                + idmConfigs.getPort() + idmConfigs.getPath(); // servicePath
        String endpointPath = idmConfigs.getPrivilegePath(); // endpointPath;

        // Get header strings
        String EMAIL = headers.getHeaderString("email");
        String SESSION_ID = headers.getHeaderString("session_id");
        String TRANSACTION_ID = headers.getHeaderString("transaction_id");

        BrowseResponseModel responseModel = null;

        // Returns resultCode of privilege request. If RC = 140, plevel is sufficient to see hidden movies.
        int privilegeRC = getPrivilegeLevel(servicePath, endpointPath, EMAIL, 4);

        // Make ArrayList to store query headers
        ArrayList<String> queryHeaders = new ArrayList<String>();
        queryHeaders.add(LIMIT.toString());
        queryHeaders.add(OFFSET.toString());
        queryHeaders.add(ORDERBY);
        queryHeaders.add(DIRECTION);

        try {
            String query = buildBrowseQuery(queryHeaders, PHRASE, privilegeRC);
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
                    if (!hidden) {
                        movies.add(newMovie);
                    }
                }
            }
            Object[] moviesArray = movies.toArray();
            if (moviesArray.length == 0) {
                responseModel = new BrowseResponseModel(211,
                        "No movies found with search parameters.", null);
                ServiceLogger.LOGGER.warning("No movies found with search parameters.");
            }
            else {
                responseModel = new BrowseResponseModel(210,
                        "Found movie(s) with search parameters.", moviesArray);
                ServiceLogger.LOGGER.info("Found movie(s) with search parameters.");
            }
        }
        catch (SQLException SQLE) {
            ServiceLogger.LOGGER.warning("Query failed: Unable to retrieve movies by browse.");
            SQLE.printStackTrace();
        }
        // Return a response with same headers
        Response.ResponseBuilder builder;
        if (responseModel == null)
            builder = Response.status(Status.BAD_REQUEST);
        else
            builder = Response.status(Status.OK).entity(responseModel);

        // Pass along headers
        builder.header("email", EMAIL);
        builder.header("session_id", SESSION_ID);
        builder.header("transaction_id", TRANSACTION_ID);

        // Return the response
        return builder.build();
    }
}
