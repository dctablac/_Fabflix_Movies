package edu.uci.ics.dtablac.service.movies.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.dtablac.service.movies.MoviesService;
import edu.uci.ics.dtablac.service.movies.configs.IdmConfigs;
import edu.uci.ics.dtablac.service.movies.core.ThumbnailQuery;
import edu.uci.ics.dtablac.service.movies.logger.ServiceLogger;
import edu.uci.ics.dtablac.service.movies.models.ThumbnailModel;
import edu.uci.ics.dtablac.service.movies.models.base.RequestModel;
import edu.uci.ics.dtablac.service.movies.models.data.ThumbnailResponseModel;
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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

@Path("thumbnail")
public class ThumbnailPage {
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)

    public Response thumbnail(@Context HttpHeaders headers, String jsonText) {
        // Get header fields
        String EMAIL = headers.getHeaderString("email");
        String SESSION_ID = headers.getHeaderString("session_id");
        String TRANSACTION_ID = headers.getHeaderString("transaction_id");

        // Get path to privilege
        IdmConfigs idmConfigs = MoviesService.getIdmConfigs();
        String servicePath = Utility.getServicePath(idmConfigs);
        String endpointPath = idmConfigs.getPrivilegePath();

        // Declare request and response models
        ObjectMapper mapper = new ObjectMapper();
        RequestModel requestModel;
        ThumbnailResponseModel responseModel = null;

        // Declare request and response model fields
        String[] movie_ids;
        String movie_id;
        String title;
        String backdrop_path;
        String poster_path;

        // Declare storage for Thumbnails
        ArrayList<ThumbnailModel> thumbnails = new ArrayList<ThumbnailModel>();

        try {
            // Map values from json request to our model
            requestModel = mapper.readValue(jsonText, RequestModel.class);
            movie_ids = requestModel.getMOVIE_IDS();
            if (movie_ids == null || movie_ids.length == 0) {
                responseModel = new ThumbnailResponseModel(211,
                        "No movies found with search parameters.", null);
                ServiceLogger.LOGGER.warning("No movies found with search parameters.");
                return Utility.headerResponse(responseModel, EMAIL, SESSION_ID, TRANSACTION_ID);
            }

            // Get privilege resultCode
            int privilegeRC = Utility.getPrivilegeLevel(servicePath, endpointPath, EMAIL, 4);

            // Send query for thumbnails
            ResultSet rs = ThumbnailQuery.sendThumbnailQuery(movie_ids);
            while (rs.next()) {
                movie_id = rs.getString("MOVIE_ID");
                title = rs.getString("TITLE");
                backdrop_path = rs.getString("BACKDROP_PATH");
                poster_path = rs.getString("POSTER_PATH");
                ThumbnailModel thumbnail = new ThumbnailModel(movie_id, title, backdrop_path, poster_path);
                thumbnails.add(thumbnail);
            }
            // Convert thumbnail ArrayList to array
            Object[] finalThumbnails = thumbnails.toArray();

            // Build response with headers and return
            if (finalThumbnails.length == 0) {
                responseModel = new ThumbnailResponseModel(211,
                        "No movies found with search parameters.", null);
                ServiceLogger.LOGGER.warning("No movies found with search parameters.");
                return Utility.headerResponse(responseModel, EMAIL, SESSION_ID, TRANSACTION_ID);
            }
            responseModel = new ThumbnailResponseModel(210,
                    "Found movies with search parameters.", finalThumbnails);
            ServiceLogger.LOGGER.info("Found movies with serach parameters.");
            return Utility.headerResponse(responseModel, EMAIL, SESSION_ID, TRANSACTION_ID);
        }
        catch (IOException e) {
            e.printStackTrace();
            ServiceLogger.LOGGER.warning("Unable to map JSON to POJO.");
        }
        catch (SQLException SQLE) {
            SQLE.printStackTrace();
            ServiceLogger.LOGGER.warning("Unable to retrieve result for thumbnail.");
        }
        return Utility.headerResponse(responseModel, EMAIL, SESSION_ID, TRANSACTION_ID); // shouldn't be reached
    }
}
