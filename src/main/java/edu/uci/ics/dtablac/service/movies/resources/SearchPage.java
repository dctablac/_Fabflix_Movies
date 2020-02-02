package edu.uci.ics.dtablac.service.movies.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.dtablac.service.movies.MoviesService;
import edu.uci.ics.dtablac.service.movies.configs.IdmConfigs;
import edu.uci.ics.dtablac.service.movies.logger.ServiceLogger;
import edu.uci.ics.dtablac.service.movies.models.MovieModel;
import edu.uci.ics.dtablac.service.movies.models.PrivilegeRequestModel;
import edu.uci.ics.dtablac.service.movies.models.PrivilegeResponseModel;
import edu.uci.ics.dtablac.service.movies.models.SearchResponseModel;
import org.glassfish.jersey.jackson.JacksonFeature;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.client.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import static edu.uci.ics.dtablac.service.movies.core.SearchQuery.buildSearchQuery;

@Path("search")
public class SearchPage {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response search(@Context HttpHeaders headers,
                           @QueryParam("title") String TITLE,
                           @QueryParam("year") Integer YEAR, // Integer
                           @QueryParam("director") String DIRECTOR,
                           @QueryParam("genre") String GENRE,
                           @QueryParam("hidden") Boolean HIDDEN, // Boolean
                           @QueryParam("limit") Integer LIMIT, // Integer
                           @QueryParam("offset") Integer OFFSET, // Integer
                           @QueryParam("orderby") String ORDERBY,
                           @QueryParam("direction") String DIRECTION){
        if (!(LIMIT.equals(10) || LIMIT.equals(25) || LIMIT.equals(50) || LIMIT.equals(100))) {
            LIMIT = 10;
        }
        if (!(ORDERBY.equals("title") || ORDERBY.equals("rating") || ORDERBY.equals("year"))) {
            ORDERBY = "title";
        }
        if (!(DIRECTION.equals("asc") || DIRECTION.equals("desc"))) {
            DIRECTION = "asc";
        }
        if (!(OFFSET.equals(0) || OFFSET%LIMIT == 0)) {
            OFFSET = 0;
        }

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

        // Make ArrayList to store query headers
        ArrayList<String> queryHeaders = new ArrayList<String>();
        queryHeaders.add(TITLE);
        if (YEAR != null) {
            queryHeaders.add(YEAR.toString());
        }
        else {
            queryHeaders.add(null);
        }
        queryHeaders.add(DIRECTOR);
        queryHeaders.add(GENRE);
        if (HIDDEN != null) {
            queryHeaders.add(HIDDEN.toString());
        }
        else {
            queryHeaders.add(null);
        }
        if (LIMIT != null) {
            queryHeaders.add(LIMIT.toString());
        }
        else {
            queryHeaders.add(null);
        }
        if (OFFSET != null) {
            queryHeaders.add(OFFSET.toString());
        }
        else {
            queryHeaders.add(null);
        }
        queryHeaders.add(ORDERBY);
        queryHeaders.add(DIRECTION);
        //ServiceLogger.LOGGER.info("Check");

        // Query headers
        try {
            String query = buildSearchQuery(queryHeaders, privilegeRC);
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
                

                movies.add(newMovie);
            }
            Object[] moviesArray = movies.toArray();
            if (moviesArray.length == 0) {
                responseModel = new SearchResponseModel(211,
                        "No movies found with search parameters.", moviesArray);
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

    public static int getPrivilegeLevel(String newServicePath, String newEndpointPath, String email, Integer plvl) {
        PrivilegeRequestModel privRequestModel = new PrivilegeRequestModel(email, plvl);
        PrivilegeResponseModel privResponseModel = null;

        // Create a new client
        ServiceLogger.LOGGER.info("Building client...");
        Client client = ClientBuilder.newClient();
        client.register(JacksonFeature.class);

        // Create a WebTarget to send a request to
        ServiceLogger.LOGGER.info("Building WebTarget...");
        WebTarget webTarget = client.target(newServicePath).path(newEndpointPath);

        // Create an InvocationBuilder to create the HTTP request (bundle request)
        ServiceLogger.LOGGER.info("Starting invocation builder...");
        Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);

        // Send the request to /idm/privilege and save it to a Response
        ServiceLogger.LOGGER.info("Sending request...");
        Response response = invocationBuilder.post(Entity.entity(privRequestModel, MediaType.APPLICATION_JSON));
        ServiceLogger.LOGGER.info("Request sent.");

        ServiceLogger.LOGGER.info("Received status " + response.getStatus());
        try {
            ObjectMapper mapper = new ObjectMapper();
            String jsonText = response.readEntity(String.class);
            privResponseModel = mapper.readValue(jsonText, PrivilegeResponseModel.class);
            ServiceLogger.LOGGER.info("Successfully mapped response to POJO.");
        }
        catch (IOException e) {
            ServiceLogger.LOGGER.warning("Unable to map response to POJO.");
        }

        // Do work with data contained in response model
        ServiceLogger.LOGGER.info("priv resultCode: "+privResponseModel.getRESULTCODE());
        return privResponseModel.getRESULTCODE();
    }
}
