package edu.uci.ics.dtablac.service.movies.core;

import edu.uci.ics.dtablac.service.movies.MoviesService;
import edu.uci.ics.dtablac.service.movies.logger.ServiceLogger;
import edu.uci.ics.dtablac.service.movies.models.data.MovieAddRequestModel;
import edu.uci.ics.dtablac.service.movies.models.data.PersonAddRequestModel;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;

public class MovieAddQuery { // TODO: Add director to person

    private static int generateID(int min, int max) {
        if (min >= max) {
            throw new IllegalArgumentException("max must be greater than min");
        }
        Random r = new Random();
        return r.nextInt((max-min)+1)+min;
    }

    public static PreparedStatement buildDirectorQuery(String director) {
        String SELECT = "\nSELECT *";
        String FROM = "FROM person\n";
        String WHERE = "WHERE name = ?";

        String query = SELECT + FROM + WHERE;

        PreparedStatement ps = null;
        try {
            ps = MoviesService.getCon().prepareStatement(query);
            ps.setString(1, director);
        }
        catch (SQLException e) {
            e.printStackTrace();
            ServiceLogger.LOGGER.warning("Unable to build query to check person existence.");
        }
        return ps;
    }

    public static PreparedStatement buildMovieAddUpdate(MovieAddRequestModel requestModel) {
        String title = requestModel.getTITLE();
        Integer year = requestModel.getYEAR();
        String director = requestModel.getDIRECTOR();
        Integer director_id = 0;

        ResultSet rs = MovieAddQuery.sendDirectorQuery(director);
        try {
            if (!rs.next()) { // If director name does not exist in person table yet.
                PersonQuery PQ = new PersonQuery();
                PersonAddRequestModel personAddRequestModel = new PersonAddRequestModel(null,
                        director, null, null, null, null,
                        null, null, null);
                PQ.sendPersonAddUpdate(personAddRequestModel);

                // Get person_id to set in movie as director_id
                rs = MovieAddQuery.sendDirectorQuery(director);
                rs.next();
                director_id = rs.getInt("person_id");
            }
            else {
                director_id = rs.getInt("person_id");
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            ServiceLogger.LOGGER.warning("Could not add the director as a new person to the table.");
        }

        Float rating = requestModel.getRATING();
        Integer num_votes = requestModel.getNUM_VOTES();
        String budget = requestModel.getBUDGET();
        String revenue = requestModel.getREVENUE();
        String overview = requestModel.getOVERVIEW();
        String backdrop_path = requestModel.getBACKDROP_PATH();
        String poster_path = requestModel.getPOSTER_PATH();
        Boolean hidden = requestModel.getHIDDEN();

        Integer id_number = generateID(1111111, 9999999);
        String movie_id = "cs"+id_number.toString();



        String INSERT = "\nINSERT INTO movie(movie_id, title, year, director_id";
        String VALUES = "VALUES(?, ?, ?, ?";

        if (rating != null && rating > 0.0 && rating < 10.0) {
            INSERT += ", rating";
            VALUES += String.format(", %f", rating);
        }
        else {
            INSERT += ", rating";
            VALUES += String.format(", %f", 0.0);
        }
        if (num_votes != null) {
            INSERT += ", num_votes";
            VALUES += String.format(", %d", num_votes);
        }
        else {
            INSERT += ", num_votes";
            VALUES += String.format(", %d", 0);
        }
        if (budget != null) {
            INSERT += ", budget";
            VALUES += String.format(", %s", budget);
        }
        if (revenue != null) {
            INSERT += ", revenue";
            VALUES += String.format(", %s", revenue);
        }
        if (overview != null) {
            INSERT += ", overview";
            VALUES += String.format(", %s", overview);
        }
        if (backdrop_path != null) {
            INSERT += ", backdrop_path";
            VALUES += String.format(", %s", backdrop_path);
        }
        if (poster_path != null) {
            INSERT += ", poster_path";
            VALUES += String.format(", %s", poster_path);
        }
        if (hidden != null) {
            INSERT += ", hidden";
            VALUES += String.format(", %b", hidden);
        }

        INSERT += ")\n";
        VALUES += ");";

        String query = INSERT + VALUES;

        PreparedStatement ps = null;
        try {
            ps = MoviesService.getCon().prepareStatement(query);
            ps.setString(1, movie_id);
            ps.setString(2, title);
            ps.setInt(3, year);
            ps.setInt(4, director_id);
        }
        catch (SQLException e) {
            e.printStackTrace();
            ServiceLogger.LOGGER.warning("Unable to build update to add a new movie entry.");
        }
        return ps;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    // return null if director doesn't exist. returns name if already in the table.
    public static ResultSet sendDirectorQuery(String director) {
        PreparedStatement ps = buildDirectorQuery(director);
        ResultSet rs = null;
        try {
            rs = ps.executeQuery();
        }
        catch (SQLException e) {
            e.printStackTrace();
            ServiceLogger.LOGGER.warning("Query failed: Unable to query if the person is valid.");
        }
        return rs;
    }

    public int sendMovieAddUpdate(MovieAddRequestModel requestModel) {
        int added_movies = 0;
        PreparedStatement ps = buildMovieAddUpdate(requestModel);
        try {
            ServiceLogger.LOGGER.info("Trying to add a movie entry: "+ps.toString());
            added_movies = ps.executeUpdate();
            ServiceLogger.LOGGER.info("Query succeeded. New movie added.");
        }
        catch (SQLException e) {
            e.printStackTrace();
            ServiceLogger.LOGGER.warning("Query failed: Unable to add a new movie entry.");
        }

        return added_movies;
    }
}
