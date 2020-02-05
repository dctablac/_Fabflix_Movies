package edu.uci.ics.dtablac.service.movies.core;

import edu.uci.ics.dtablac.service.movies.MoviesService;
import edu.uci.ics.dtablac.service.movies.logger.ServiceLogger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MovieIDQuery {
    // Helper only to be used to select whether the query is getting movie info, genre, or people.
    public static String buildMovieIDQuery(boolean get_movie_info, boolean get_genres, boolean get_people) {
        String SELECT = null;
        String FROM = "FROM movie as m\n";
        String JOIN = null;
        String WHERE =  "WHERE 1=1 && m.movie_id = ?;";

        if (get_movie_info) {
            SELECT = "\nSELECT m.movie_id AS MOVIE_ID, m.title AS TITLE,\n" +
                      "m.year AS YEAR, p.name AS DIRECTOR,\n" +
                      "m.rating AS RATING, m.num_votes AS NUM_VOTES,\n" +
                      "m.budget AS BUDGET, m.revenue AS REVENUE,\n" +
                      "m.overview AS OVERVIEW, m.backdrop_path AS BACKDROP_PATH,\n" +
                      "m.poster_path AS POSTER_PATH, m.hidden AS HIDDEN\n";
            JOIN =  "INNER JOIN person AS p\n" +
                    "    ON m.director_id = p.person_id\n";
        }
        if (get_genres) {
            SELECT = "\nSELECT g.genre_id AS GENRE_ID, g.name AS GNAME\n";
            JOIN = "INNER JOIN genre_in_movie AS gim\n" +
                    "    ON m.movie_id = gim.movie_id\n" +
                    "INNER JOIN genre as g\n" +
                    "    ON gim.genre_id = g.genre_id\n";
        }
        if (get_people) {
            SELECT = "\nSELECT p.person_id AS PERSON_ID, p.name AS PNAME\n";
            JOIN =  "INNER JOIN person_in_movie AS pim\n" +
                    "    ON pim.movie_id = m.movie_id\n" +
                    "INNER JOIN person AS p\n" +
                    "    ON pim.person_id = p.person_id\n";
        }

        return SELECT + FROM + JOIN + WHERE;
    }

    public static ResultSet sendMovieIDQuery(String movie_id, boolean get_movie_info,
                                             boolean get_genres, boolean get_people) {
        String query = buildMovieIDQuery(get_movie_info, get_genres, get_people);
        ResultSet rs = null;
        try {
            PreparedStatement ps = MoviesService.getCon().prepareStatement(query);
            System.out.println(ps.toString());
            ps.setString(1, movie_id);
            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query succeeded.");
        }
        catch (SQLException SQLE) {
            SQLE.printStackTrace();
            ServiceLogger.LOGGER.warning("Query failed: Unable to retrieve movie record.");
        }
        return rs;
    }
}
