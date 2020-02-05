package edu.uci.ics.dtablac.service.movies.resources;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import edu.uci.ics.dtablac.service.movies.MoviesService;
import edu.uci.ics.dtablac.service.movies.configs.IdmConfigs;
import edu.uci.ics.dtablac.service.movies.models.data.MovieIDResponseModel;

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

        // Path to /idm/privilege
        IdmConfigs idmConfigs = MoviesService.getIdmConfigs();
        String servicePath = idmConfigs.getScheme()+idmConfigs.getHostName()+":"+
                             idmConfigs.getPort()+idmConfigs.getPath();
        String endpointPath = idmConfigs.getPrivilegePath();

        int privilegeRC = getPrivilegeLevel(servicePath, endpointPath, EMAIL, 4);

        // Run query


        return Response.status(Status.OK).entity(responseModel).build();
    }



}
