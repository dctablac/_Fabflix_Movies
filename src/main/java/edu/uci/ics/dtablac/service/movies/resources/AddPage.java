package edu.uci.ics.dtablac.service.movies.resources;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.dtablac.service.movies.MoviesService;
import edu.uci.ics.dtablac.service.movies.configs.IdmConfigs;
import edu.uci.ics.dtablac.service.movies.core.MovieAddQuery;
import edu.uci.ics.dtablac.service.movies.core.MovieIDQuery;
import edu.uci.ics.dtablac.service.movies.models.base.ResponseModel;
import edu.uci.ics.dtablac.service.movies.models.data.MovieAddRequestModel;
import edu.uci.ics.dtablac.service.movies.models.data.MovieUpdateRequestModel;
import edu.uci.ics.dtablac.service.movies.util.Utility;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;

@Path("add")
public class AddPage { // TODO: NOTE: Append cs prefix to IDs
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addMovie(@Context HttpHeaders headers, String jsonText) {
        // Headers
        String EMAIL = headers.getHeaderString("email");
        String SESSION_ID = headers.getHeaderString("session_id");
        String TRANSACTION_ID = headers.getHeaderString("transaction_id");

        // Declare mappers and models
        ObjectMapper mapper = new ObjectMapper();
        ResponseModel responseModel = null;
        MovieAddRequestModel requestModel;

        IdmConfigs idmConfigs = MoviesService.getIdmConfigs();
        String servicePath = idmConfigs.getScheme() + idmConfigs.getHostName() + ":"
                + idmConfigs.getPort() + idmConfigs.getPath(); // servicePath
        String endpointPath = idmConfigs.getPrivilegePath(); // endpointPath;

        // If the requester does not have plevel 3 or lower, return case 218.
        if (Utility.getPrivilegeLevel(servicePath, endpointPath, EMAIL, 3) != 140) {
            responseModel = new ResponseModel(215, "Could not add movie.");
        }
        else {
            try {
                requestModel = mapper.readValue(jsonText, MovieAddRequestModel.class);

                MovieAddQuery query = new MovieAddQuery();
                int add_movie_success = query.sendMovieAddUpdate(requestModel);
                if (add_movie_success == 0) { // Movie could not be added.
                    responseModel = new ResponseModel(216, "Movie already exists.");
                }
                else { // Movie was added.
                    responseModel = new ResponseModel(214, "Movie successfully added.");
                }
            } catch (IOException e) {
                if (e instanceof JsonParseException) {
                    responseModel = new ResponseModel(-3, "JSON parse exception.");
                } else if (e instanceof JsonMappingException) {
                    responseModel = new ResponseModel(-2, "JSON mapping exception.");
                }
            }
        }
        return Utility.headerResponse(responseModel, EMAIL, SESSION_ID, TRANSACTION_ID);
    }
}
