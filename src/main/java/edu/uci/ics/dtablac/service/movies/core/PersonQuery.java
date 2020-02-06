package edu.uci.ics.dtablac.service.movies.core;

import edu.uci.ics.dtablac.service.movies.MoviesService;
import edu.uci.ics.dtablac.service.movies.logger.ServiceLogger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PersonQuery {

    public static String buildPersonQuery(String name, Integer person_id) {
        String SELECT = "\nSELECT p.person_id AS PERSON_ID, p.name AS NAME, g.gender_name AS GENDER,\n" +
                        "p.birthday AS BIRTHDAY, p.deathday AS DEATHDAY, p.biography AS BIOGRAPHY,\n" +
                        "p.birthplace AS BIRTHPLACE, p.popularity AS POPULARITY, p.profile_path AS PROFILE_PATH\n";
        String FROM = "FROM person AS p\n";
        String JOIN = "JOIN gender AS g\n" +
                      "    ON g.gender_id = p.gender_id\n";
        String WHERE = "WHERE 1=1";

        if (name != null) { // For efficiency sake, could alter SELECT based on name or person_id used to search.
            WHERE += " && name = ?\n"; // if name isn't null, query is just verifying the person exists.
        }
        if (person_id != null) {
            WHERE += " && person_id = ?\n";
        }
        return SELECT + FROM + JOIN + WHERE + ";";
    }

    public static ResultSet sendPersonQuery(String name, Integer person_id) {
        ResultSet rs = null;
        String query = buildPersonQuery(name, person_id);
        try {
            PreparedStatement ps = MoviesService.getCon().prepareStatement(query);
            int param_count = 0;
            if (name != null) {
                ps.setString(++param_count, name);
            }
            if (person_id != null) {
                ps.setInt(++param_count,person_id);
            }
            rs = ps.executeQuery();
        }
        catch (SQLException SQLE) {
            SQLE.printStackTrace();
            ServiceLogger.LOGGER.warning("Query failed: Unable to retrieve the person record.");
        }
        return rs;
    }

}
