package edu.uci.ics.dtablac.service.movies.core;

import edu.uci.ics.dtablac.service.movies.MoviesService;
import edu.uci.ics.dtablac.service.movies.logger.ServiceLogger;
import edu.uci.ics.dtablac.service.movies.models.data.MovieUpdateRequestModel;
import edu.uci.ics.dtablac.service.movies.models.data.PersonAddRequestModel;

import javax.xml.transform.Result;
import javax.xml.ws.Service;
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

    // TODO: Match name with person table, add new person if not found in db.
    public static PreparedStatement buildUpdateMovieQuery(MovieUpdateRequestModel requestModel) {
        String UPDATE = "\nUPDATE movie\n";
        String SET = "SET movie_id = ?";
        String WHERE = "WHERE movie_id = ?";

        String movie_id = requestModel.getMOVIE_ID();
        String title = requestModel.getTITLE();
        Integer year = requestModel.getYEAR();
        String director = requestModel.getDIRECTOR();
        Float rating = requestModel.getRATING();
        Integer num_votes = requestModel.getNUM_VOTES();
        String budget = requestModel.getBUDGET();
        String revenue = requestModel.getREVENUE();
        String overview = requestModel.getOVERVIEW();
        String backdrop_path = requestModel.getBACKDROP_PATH();
        String poster_path = requestModel.getPOSTER_PATH();
        Boolean hidden = requestModel.isHIDDEN();

        if (title != null) {
            SET += String.format(", title = '%s'", title);
        }
        if (year != null) {
            SET += String.format(", year = %d", year);
        }
        if (director != null) { // TODO: set director_id
            ResultSet rs = MovieAddQuery.sendDirectorQuery(director);
            try {
                if (!rs.next()) {
                    PersonQuery PQ = new PersonQuery();
                    PersonAddRequestModel personAddRequestModel = new PersonAddRequestModel(null,
                            director, null, null, null, null,
                            null, null, null);
                    PQ.sendPersonAddUpdate(personAddRequestModel);

                    // Get person_id to set in movie as director_id
                    rs = MovieAddQuery.sendDirectorQuery(director);
                    rs.next();
                    Integer director_id = rs.getInt("person_id");

                    SET += String.format(", director_id = %d", director_id);
                }
                else { // person found
                    SET += String.format(", director_id = %d", rs.getInt("person_id"));
                }
            }
            catch (SQLException e) {
                e.printStackTrace();
                ServiceLogger.LOGGER.warning("Could not add the director as a new person to the table.");
            }
        }
        if (rating != null) {
            SET += String.format(", num_votes = %d", num_votes);
        }
        if (budget != null) {
            SET += String.format(", budget = '%s'", budget);
        }
        if (revenue != null) {
            SET += String.format(", revenue = '%s'", revenue);
        }
        if (overview != null) {
            SET += String.format(", overview = '%s'", overview);
        }
        if (backdrop_path != null) {
            SET += String.format(", backdrop_path = '%s'", backdrop_path);
        }
        if (poster_path != null) {
            SET += String.format(", poster_path = '%s'", poster_path);
        }
        if (hidden != null) {
            SET += String.format(", hidden = %b", hidden);
        }

        SET += "\n";
        WHERE += ";";
        String query = UPDATE + SET + WHERE;

        PreparedStatement ps = null;
        try {
            ps = MoviesService.getCon().prepareStatement(query);
            ps.setString(1, movie_id);
            ps.setString(2, movie_id);
        }
        catch (SQLException e) {
            e.printStackTrace();
            ServiceLogger.LOGGER.warning("Unable to build an update query for movie: "+movie_id);
        }

        return ps;
    }
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public int sendMovieUpdate(MovieUpdateRequestModel requestModel) {
        PreparedStatement ps = buildUpdateMovieQuery(requestModel);
        int affected_entries = 0;
        try {
            ServiceLogger.LOGGER.info("Trying update: "+ps.toString());
            affected_entries = ps.executeUpdate();
            ServiceLogger.LOGGER.info("Query succeeded.");
        }
        catch (SQLException e) {
            e.printStackTrace();
            ServiceLogger.LOGGER.warning("Update failed: Unable to update movie information.");
        }
        return affected_entries;
    }

    public static ResultSet sendMovieIDQuery(String movie_id, boolean get_movie_info,
                                             boolean get_genres, boolean get_people) {
        String query = buildMovieIDQuery(get_movie_info, get_genres, get_people);
        ResultSet rs = null;
        try {
            PreparedStatement ps = MoviesService.getCon().prepareStatement(query);
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
