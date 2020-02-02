package edu.uci.ics.dtablac.service.movies.core;

import edu.uci.ics.dtablac.service.movies.models.MovieModel;

import java.util.ArrayList;

public class SearchQuery {

    public static String buildSearchQuery(ArrayList<String> requestQueryFields, Integer privilegeRC) {

        String SELECT = "SELECT m.movie_id AS MOVIE_ID, m.title AS TITLE, m.year AS YEAR, p.name AS DIRECTOR," +
                        " m.rating AS RATING, m.backdrop_path AS BACKDROP_PATH, m.poster_path AS POSTER_PATH," +
                        " m.hidden AS HIDDEN";
        String FROM = " FROM movie as m";
        String JOIN = " INNER JOIN genre_in_movie as gim"+
                      " ON m.movie_id = gim.movie_id"+
                      " INNER JOIN genre as g"+
                      " ON gim.genre_id = g.genre_id"+
                      " INNER JOIN person as p"+
                      " ON m.director_id = p.person_id";
        String WHERE = " WHERE 1=1";
        String LIMIT = "";
        String OFFSET = "";
        String ORDERBY = "";

        String title = requestQueryFields.get(0);
        String year = requestQueryFields.get(1);
        String director = requestQueryFields.get(2);
        String genre = requestQueryFields.get(3);
        String hidden = requestQueryFields.get(4);
        String limit = requestQueryFields.get(5);
        String offset = requestQueryFields.get(6);
        String orderby = requestQueryFields.get(7);
        String direction = requestQueryFields.get(8);


        if (title != null) {
            WHERE += " && title LIKE '%" + title + "%'";
        }
        if (year != null) {
            WHERE += " && year = " + year;
        }
        if (director != null) {
            WHERE += " && p.name LIKE '%" + director + "%'";
        }
        if (genre != null) {
            WHERE += " && g.name LIKE '%" + genre + "%'";
        }
        if (hidden != null && privilegeRC == 140) {
            WHERE += " && hidden = " + hidden;
        }

        if (limit != null) {
            LIMIT = " LIMIT "+limit;
        }
        if (offset != null) {
            OFFSET = " OFFSET "+offset; // TODO: Equation for offset
        }
        if (orderby != null) {
            ORDERBY = " ORDER BY "+orderby;
        }
        if (direction != null) {
            ORDERBY += " "+direction;
        }
        return SELECT + FROM + JOIN + WHERE + ORDERBY + LIMIT + OFFSET + ";";
    }
}
