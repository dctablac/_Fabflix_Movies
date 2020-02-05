package edu.uci.ics.dtablac.service.movies.core;

public class MovieIDQuery {

    public String buildMovieIDQuery() {
        String SELECT = "SELECT *\n";
        String FROM =   "FROM movie as m\n";
        String JOIN =   "";
        String WHERE =  "WHERE 1=1";

        return SELECT + FROM + JOIN + WHERE;
    }

    public static void main(String[] args) {
        MovieIDQuery MIDQ = new MovieIDQuery();
        String output = MIDQ.buildMovieIDQuery();
        System.out.println(output);
    }
}
