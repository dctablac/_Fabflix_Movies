package edu.uci.ics.dtablac.service.movies.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.dtablac.service.movies.MoviesService;
import edu.uci.ics.dtablac.service.movies.configs.IdmConfigs;
import edu.uci.ics.dtablac.service.movies.logger.ServiceLogger;
import edu.uci.ics.dtablac.service.movies.models.PrivilegeRequestModel;
import edu.uci.ics.dtablac.service.movies.models.PrivilegeResponseModel;
import edu.uci.ics.dtablac.service.movies.models.base.ResponseModel;
import org.glassfish.jersey.jackson.JacksonFeature;

import javax.ws.rs.client.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Utility {

    public static int getPrivilegeLevel(String newServicePath, String newEndpointPath, String email, Integer plvl) {
        PrivilegeRequestModel privRequestModel = new PrivilegeRequestModel(email, plvl);
        PrivilegeResponseModel privResponseModel = null;

        ServiceLogger.LOGGER.info("EMAIL: "+email);

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
        // TODO: Maybe get rid of this logger?
        ServiceLogger.LOGGER.info("Privilege resultCode: "+privResponseModel.getRESULTCODE());
        return privResponseModel.getRESULTCODE();
    }

    public static Response headerResponse(ResponseModel responseModel, String EMAIL,
                                          String SESSION_ID, String TRANSACTION_ID) {
        // Return a response with same headers
        Response.ResponseBuilder builder;
        if (responseModel == null)
            builder = Response.status(Response.Status.BAD_REQUEST);
        else
            builder = Response.status(Response.Status.OK).entity(responseModel);

        // Pass along headers
        builder.header("email", EMAIL);
        builder.header("session_id", SESSION_ID);
        builder.header("transaction_id", TRANSACTION_ID);

        // Return the response
        return builder.build();
    }

    public static ResultSet preparePeopleQuery(String query, String name) throws SQLException{
        PreparedStatement ps = MoviesService.getCon().prepareStatement(query);
        ps.setString(1, name);
        ServiceLogger.LOGGER.info("Trying query: "+ps.toString());
        ResultSet rs = ps.executeQuery();
        ServiceLogger.LOGGER.info("Query succeeded.");
        return rs;
    }

    public static ResultSet prepareThumbnailQuery (String query, String[] IDs, int ID_count) throws SQLException {
        PreparedStatement ps = MoviesService.getCon().prepareStatement(query);
        for (int i = 0; i < ID_count; i++) {
            ps.setString(i+1, IDs[i]);
        }
        ServiceLogger.LOGGER.info("Trying query: "+ps.toString());
        ResultSet rs = ps.executeQuery();
        ServiceLogger.LOGGER.info("Query succeeded.");
        return rs;
    }

    public static Integer checkLimit(Integer LIMIT) {
        if (LIMIT == null || !(LIMIT == 10 || LIMIT.equals(25) || LIMIT.equals(50) || LIMIT.equals(100))) {
            return 10;
        }
        return LIMIT;
    }

    public static String checkOrderBy(String ORDERBY) {
        if (ORDERBY == null || !(ORDERBY.equals("title") || ORDERBY.equals("rating") || ORDERBY.equals("year"))) {
            return "title";
        }
        return ORDERBY;
    }

    public static String checkPeopleOrderBy(String ORDERBY) {
        if (ORDERBY == null || !(ORDERBY.equals("name") || ORDERBY.equals("birthday") || ORDERBY.equals("popularity"))) {
            return "name";
        }
        return ORDERBY;
    }

    public static String checkDirection(String DIRECTION) {
        if (DIRECTION == null || !(DIRECTION.equals("asc") || DIRECTION.equals("desc"))) {
            return "asc";
        }
        return DIRECTION;
    }

    public static Integer checkOffset(Integer OFFSET, Integer LIMIT) {
        if (OFFSET == null) {
            return 0;
        }
        return OFFSET*LIMIT;
    }

    public static String getServicePath(IdmConfigs idmConfigs) {
        String servicePath = idmConfigs.getScheme()+idmConfigs.getHostName()+":"+
                idmConfigs.getPort()+idmConfigs.getPath();
        return servicePath;
    }


}
