package edu.uci.ics.dtablac.service.movies.core;

import java.util.ArrayList;

public class BrowseQuery {

    public static String buildBrowseQuery(ArrayList<String> requestQueryFields, String phrase, Integer privilegeRC) {
        String SELECT = "SELECT m.movie_id AS MOVIE_ID, m.title AS TITLE, m.year AS YEAR, p.name AS DIRECTOR," +
                        " m.rating AS RATING, m.backdrop_path AS BACKDROP_PATH, m.poster_path AS POSTER_PATH," +
                        " m.hidden AS HIDDEN";
        String FROM = " FROM movie AS m";
        String WHERE = " WHERE 1=1";
        String JOIN = " INNER JOIN person AS p" +
                      " ON m.director_id = p.person_id";
        String ORDERBY = "";
        String LIMIT = "";
        String OFFSET = "";

        String limit = requestQueryFields.get(0);
        String offset = requestQueryFields.get(1);
        String orderby = requestQueryFields.get(2);
        String direction = requestQueryFields.get(3);

        String[] parsedPhrases = phrase.split(",",-1);
        int count = parsedPhrases.length;
        for (int i = 0; i < count; i++) {
            JOIN += String.format( " INNER JOIN keyword_in_movie AS kim%d ON m.movie_id = kim%d.movie_id INNER JOIN keyword AS k%d ON kim%d.keyword_id = k%d.keyword_id",i,i,i,i,i);
            WHERE += String.format(" && k%d.name = '%s'", i, parsedPhrases[i]);
        }

        if (limit != null) {
            LIMIT = " LIMIT " + limit;
        }
        if (offset != null) {
            OFFSET = " OFFSET " + offset;
        }
        if (orderby != null) {
            if (orderby.equals("title")) {
                ORDERBY = " ORDER BY "+orderby+" "+direction+", rating desc";
            }
            if (orderby.equals("rating")) {
                ORDERBY = " ORDER BY "+orderby+" "+direction+", title asc";
            }
            if (orderby.equals("year")) {
                ORDERBY = " ORDER BY "+orderby+" "+direction+", rating desc";
            }
        }
         return SELECT + FROM + JOIN + WHERE  + ORDERBY + LIMIT + OFFSET +";";
    }
}
