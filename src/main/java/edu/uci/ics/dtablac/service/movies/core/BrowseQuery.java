package edu.uci.ics.dtablac.service.movies.core;

import java.util.ArrayList;

public class BrowseQuery {

    public static String buildBrowseQuery(ArrayList<String> requestQueryFields, String phrase, Integer privilegeRC) {
        String SELECT = "\nSELECT m.movie_id AS MOVIE_ID, m.title AS TITLE, m.year AS YEAR, p.name AS DIRECTOR,\n" +
                        "m.rating AS RATING, m.poster_path AS POSTER_PATH, m.backdrop_path AS BACKDROP_PATH,\n" +
                        "m.hidden AS HIDDEN\n";
        String FROM = " FROM movie AS m\n";
        String WHERE = " WHERE 1=1";
        String JOIN = "INNER JOIN person AS p\n" +
                      "    ON m.director_id = p.person_id\n";
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
            JOIN += String.format( "INNER JOIN keyword_in_movie AS kim%d \nON m.movie_id = kim%d.movie_id \nINNER JOIN keyword AS k%d \nON kim%d.keyword_id = k%d.keyword_id\n",i,i,i,i,i);
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
