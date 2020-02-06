package edu.uci.ics.dtablac.service.movies.core;

import edu.uci.ics.dtablac.service.movies.logger.ServiceLogger;
import edu.uci.ics.dtablac.service.movies.models.MovieModel;
import edu.uci.ics.dtablac.service.movies.util.Utility;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class SearchQuery {

    public static String buildSearchQuery(String title, Integer year, String director, String genre, Boolean hidden,
                                          Integer limit, Integer offset, String orderby, String direction,
                                          String person_name) {

        String SELECT = "\nSELECT DISTINCT m.movie_id AS MOVIE_ID, m.title AS TITLE, m.year AS YEAR,\n" +
                        "                  p.name AS DIRECTOR," + "m.rating AS RATING,\n" +
                        "                  m.backdrop_path AS BACKDROP_PATH, m.poster_path AS POSTER_PATH,\n" +
                        "                  m.hidden AS HIDDEN\n";
        String FROM = "FROM movie as m\n";
        String JOIN = "INNER JOIN genre_in_movie as gim\n"+
                      "    ON m.movie_id = gim.movie_id\n"+
                      "INNER JOIN genre as g\n"+
                      "    ON gim.genre_id = g.genre_id\n"+
                      "INNER JOIN person as p\n"+
                      "    ON m.director_id = p.person_id\n";
        String WHERE = "WHERE 1=1";
        String LIMIT = "";
        String OFFSET = "";
        String ORDERBY = "";

        if (title != null) {
            WHERE += " && title LIKE '%" + title + "%'\n";
        }
        if (year != null) {
            WHERE += " && year = \n" + year;
        }
        if (director != null) {
            WHERE += " && p.name LIKE '%" + director + "%'\n";
        }
        if (genre != null) {
            WHERE += " && g.name LIKE '%" + genre + "%'\n";
        }
        if (hidden != null) { // if movies need to be hidden
            WHERE += " && hidden = 0\n";
        }
        if (person_name != null) {
            JOIN +=  "INNER JOIN person_in_movie AS pim\n" +
                     "    ON pim.movie_id = m.movie_id\n" +
                     "INNER JOIN person AS p2\n" +
                     "    ON pim.person_id = p2.person_id\n";
            WHERE += " && p2.name = ?";
        }

        if (limit != null) {
            LIMIT = " LIMIT "+limit+"\n";
        }
        if (offset != null) {
            OFFSET = " OFFSET "+offset+"\n";
        }
        if (orderby != null) {
            if (orderby.equals("title")) {
                ORDERBY = " ORDER BY "+orderby+" "+direction+", rating desc\n";
            }
            if (orderby.equals("rating")) {
                ORDERBY = " ORDER BY "+orderby+" "+direction+", title asc\n";
            }
            if (orderby.equals("year")) {
                ORDERBY = " ORDER BY "+orderby+" "+direction+", rating desc\n";
            }
        }
        return SELECT + FROM + JOIN + WHERE + ORDERBY + LIMIT + OFFSET + ";";
    }

    public static ResultSet sendSearchQuery(String title, Integer year, String director, String genre,
                                            Boolean hidden, Integer limit, Integer offset, String orderby,
                                            String direction, String name){
        ResultSet rs = null;
        String query = buildSearchQuery(title, year, director, genre, hidden,
                limit, offset, orderby, direction, name);
        try {
            rs = Utility.preparePeopleQuery(query, name);
        }
        catch (SQLException SQLE) {
            SQLE.printStackTrace();
            ServiceLogger.LOGGER.warning("Query failed: Unable to search for movies.");
        }
        return rs;
    }
}