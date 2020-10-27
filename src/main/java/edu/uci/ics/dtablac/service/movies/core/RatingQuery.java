package edu.uci.ics.dtablac.service.movies.core;

import edu.uci.ics.dtablac.service.movies.MoviesService;
import edu.uci.ics.dtablac.service.movies.logger.ServiceLogger;
import edu.uci.ics.dtablac.service.movies.models.data.RatingRequestModel;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RatingQuery {

    // TODO: May want to move the case 211 checking out of here.
    public static PreparedStatement buildRatingUpdate(RatingRequestModel requestModel) {
        Float user_rating = requestModel.getRATING();
        String movie_id = requestModel.getMOVIE_ID();

        Integer num_votes = null;
        Float old_rating = null;

        // Get the old rating.
        ResultSet rs = MovieIDQuery.sendMovieIDQuery(movie_id,true, false, false);
        try {
            if (rs.next()) {
                old_rating = rs.getFloat("rating");
                num_votes = rs.getInt("num_votes");
            }
            else {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            ServiceLogger.LOGGER.warning("Unable to get num_votes and rating by movie ID");
        }

        // Calculate the new rating
        Float sum = old_rating * num_votes;
        Float new_rating = (sum + user_rating) / (num_votes + 1);

        // Increase number of votes
        num_votes += 1;

        String UPDATE = "\nUPDATE movie\n";
        String SET = "SET rating = ?, num_votes = ?\n";
        String WHERE = "WHERE movie_id = ?;";

        String query = UPDATE + SET + WHERE;

        PreparedStatement ps = null;
        try {
            ps = MoviesService.getCon().prepareStatement(query);
            ps.setFloat(1, new_rating);
            ps.setInt(2, num_votes);
            ps.setString(3, movie_id);
        } catch (SQLException e) {
            e.printStackTrace();
            ServiceLogger.LOGGER.warning("Unable to build update to a movie's rating.");
        }
        return ps;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public int sendRatingUpdate(RatingRequestModel requestModel) {
        PreparedStatement ps = buildRatingUpdate(requestModel);
        if (ps == null) { // for checking movie existence, refer above function
            return 0; // no movies found
        }
        int affected_rows = 0;
        try {
            ServiceLogger.LOGGER.info("Trying to update a movie's rating: "+ps.toString());
            affected_rows = ps.executeUpdate();
            ServiceLogger.LOGGER.info("Query succeeded. Rating updated.");
        } catch (SQLException e) {
            e.printStackTrace();
            ServiceLogger.LOGGER.warning("Query failed: Unable to update the rating of a movie.");
        }
        return affected_rows;
    }

}
