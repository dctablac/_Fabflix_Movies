package edu.uci.ics.dtablac.service.movies.core;

import edu.uci.ics.dtablac.service.movies.MoviesService;
import edu.uci.ics.dtablac.service.movies.logger.ServiceLogger;
import edu.uci.ics.dtablac.service.movies.util.Utility;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ThumbnailQuery {

    public static String buildThumbnailQuery(String[] IDs) {
        String SELECT = "\nSELECT m.movie_id AS MOVIE_ID, m.title AS TITLE,\n" +
                        "       m.backdrop_path AS BACKDROP_PATH, m.poster_path AS POSTER_PATH\n";
        String FROM = "FROM movie as m\n";
        String WHERE = "WHERE m.movie_id = ?\n";

        int len = IDs.length;
        for (int i = 1; i < len; i++) {
            WHERE += " || m.movie_id = ?\n";
        }

        return SELECT + FROM + WHERE + ";";
    }

    public static ResultSet sendThumbnailQuery(String[] IDs) {
        ResultSet rs = null;
        String query = buildThumbnailQuery(IDs);
        try {
            rs = Utility.prepareThumbnailQuery(query, IDs, IDs.length);
        }
        catch (SQLException SQLE) {
            SQLE.printStackTrace();
            ServiceLogger.LOGGER.warning("Query failed: Unable to retrieve thumbnails.");
        }
        return rs;
    }
}
