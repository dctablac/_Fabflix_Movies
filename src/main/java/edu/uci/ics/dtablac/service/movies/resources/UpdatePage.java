package edu.uci.ics.dtablac.service.movies.resources;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.dtablac.service.movies.MoviesService;
import edu.uci.ics.dtablac.service.movies.configs.IdmConfigs;
import edu.uci.ics.dtablac.service.movies.core.MovieIDQuery;
import edu.uci.ics.dtablac.service.movies.models.base.ResponseModel;
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

@Path("update")
public class UpdatePage {
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateMovie(@Context HttpHeaders headers, String jsonText) {
        ResponseModel responseModel = null;
        MovieUpdateRequestModel requestModel;
        ObjectMapper mapper = new ObjectMapper();

        IdmConfigs idmConfigs = MoviesService.getIdmConfigs();
        String servicePath = idmConfigs.getScheme() + idmConfigs.getHostName() + ":"
                + idmConfigs.getPort() + idmConfigs.getPath(); // servicePath
        String endpointPath = idmConfigs.getPrivilegePath(); // endpointPath;

        // Header values
        String EMAIL = headers.getHeaderString("email");
        String SESSION_ID = headers.getHeaderString("session_id");
        String TRANSACTION_ID = headers.getHeaderString("transaction_ID");

        // If the requester does not have plevel 3 or lower, return case 218.
        if (Utility.getPrivilegeLevel(servicePath, endpointPath, EMAIL, 3) != 140) {
            responseModel = new ResponseModel(218, "Could not update movie.");
        }
        else {
            try {
                // Make requestmodel
                requestModel = mapper.readValue(jsonText, MovieUpdateRequestModel.class);

                // Send a query to update movie information
                MovieIDQuery query = new MovieIDQuery();
                int successfully_updated = query.sendMovieUpdate(requestModel);

                // Evaluate if a movie was successfully updated
                if (successfully_updated == 0) { // Zero movies were updated.
                    responseModel = new ResponseModel(211,
                                                               "No movies found with search parameters.");
                } else {
                    responseModel = new ResponseModel(217, "Movie successfully updated.");
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
