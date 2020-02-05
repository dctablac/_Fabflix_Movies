package edu.uci.ics.dtablac.service.movies.core;

import edu.uci.ics.dtablac.service.movies.MoviesService;
import edu.uci.ics.dtablac.service.movies.logger.ServiceLogger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PersonQuery {

    public static String buildPersonQuery() {
        String SELECT = "\nSELECT *\n";
        String FROM = "FROM person as p\n";
        String WHERE = "WHERE p.name = ?";

        return SELECT + FROM + WHERE + ";";
    }

    public static ResultSet sendPersonQuery(String name) {
        ResultSet rs = null;
        String query = buildPersonQuery();
        try {
            PreparedStatement ps = MoviesService.getCon().prepareStatement(query);
            ps.setString(1, name);
            rs = ps.executeQuery();
        }
        catch (SQLException SQLE) {
            SQLE.printStackTrace();
            ServiceLogger.LOGGER.warning("Query failed: Unable to retrieve the person record.");
        }
        return rs;
    }

}
