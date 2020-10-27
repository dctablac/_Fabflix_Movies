package edu.uci.ics.dtablac.service.movies.core;

import edu.uci.ics.dtablac.service.movies.MoviesService;
import edu.uci.ics.dtablac.service.movies.logger.ServiceLogger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PeopleSearchQuery {

    public static ResultSet sendPersonBasicQuery(String name, String birthday, String title,
                                               Integer limit, Integer offset, String orderby,
                                               String direction) {

        String SELECT = "\nSELECT DISTINCT p.person_id AS PERSON_ID, p.name AS NAME, p.birthday AS BIRTHDAY,\n" +
                                " p.popularity AS POPULARITY, p.profile_path AS PROFILE_PATH\n";
        String FROM = "FROM person as p\n";
        String JOIN = "";
        String WHERE = "WHERE 1=1";
        String LIMIT = "LIMIT "+limit+"\n";
        String OFFSET = "OFFSET "+offset+"\n";
        String ORDERBY = "";

        if (name != null) {
            WHERE += " && p.name LIKE ?\n";
        }
        if (birthday != null) {
            WHERE += " && p.birthday = ?\n";
        }
        if (title != null) {
            JOIN += "INNER JOIN person_in_movie pim\n" +
                    "    ON pim.person_id = p.person_id\n" +
                    "INNER JOIN movie as m\n" +
                    "    ON pim.movie_id = m.movie_id\n";
            WHERE += " && m.title LIKE ?\n";
        }
        if (orderby != null) {
            if (orderby.equals("name")) {
                ORDERBY = "ORDER BY " + orderby + " " + direction + ", popularity desc\n";
            }
            if (orderby.equals("birthday")) {
                ORDERBY = "ORDER BY " + orderby + " " + direction + ", popularity desc\n";
            }
            if (orderby.equals("popularity")) {
                ORDERBY = "ORDER BY " + orderby + " " + direction + ", name asc\n";
            }
        }

        String query = SELECT + FROM + JOIN + WHERE + ORDERBY + LIMIT + OFFSET + ";";
        ResultSet rs = null;
        try {
            PreparedStatement ps = MoviesService.getCon().prepareStatement(query);
            int paramCount = 0;
            if (name != null) {
                ps.setString(++paramCount, "%"+name+"%");
                //paramCount++;
            }
            if (birthday != null) {
                ps.setString(++paramCount, birthday);
                //paramCount++;
            }
            if (title != null) {
                ps.setString(++paramCount, "%"+title+"%");
                //paramCount++;
            }
            ServiceLogger.LOGGER.info("Trying query: "+ps.toString());
            rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query succeeded.");
        }
        catch(SQLException e) {
            e.printStackTrace();
            ServiceLogger.LOGGER.warning("Query failed: Unable to retrieve basic person information.");
        }
        return rs;
    }
}
