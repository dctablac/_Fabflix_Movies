package edu.uci.ics.dtablac.service.movies.core;

import edu.uci.ics.dtablac.service.movies.MoviesService;
import edu.uci.ics.dtablac.service.movies.logger.ServiceLogger;
import edu.uci.ics.dtablac.service.movies.models.data.PersonAddRequestModel;
import edu.uci.ics.dtablac.service.movies.models.data.PersonUpdateRequestModel;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;

public class PersonQuery {

    public static String buildPersonQuery(String name, Integer person_id) {
        String SELECT = "\nSELECT p.person_id AS PERSON_ID, p.name AS NAME, g.gender_name AS GENDER,\n" +
                        "p.birthday AS BIRTHDAY, p.deathday AS DEATHDAY, p.biography AS BIOGRAPHY,\n" +
                        "p.birthplace AS BIRTHPLACE, p.popularity AS POPULARITY, p.profile_path AS PROFILE_PATH\n";
        String FROM = "FROM person AS p\n";
        String JOIN = "JOIN gender AS g\n" +
                      "    ON g.gender_id = p.gender_id\n";
        String WHERE = "WHERE 1=1";

        if (name != null) { // For efficiency sake, could alter SELECT based on name or person_id used to search.
            WHERE += " && name = ?\n"; // if name isn't null, query is just verifying the person exists.
        }
        if (person_id != null) {
            WHERE += " && person_id = ?\n";
        }
        return SELECT + FROM + JOIN + WHERE + ";";
    }

    public static PreparedStatement buildPersonAddUpdate(PersonAddRequestModel requestModel) {
        Date today = Calendar.getInstance().getTime();

        Integer person_id = requestModel.getPERSON_ID();
        String name = requestModel.getNAME();
        Integer gender_id = requestModel.getGENDER_ID();
        Date birthday = requestModel.getBIRTHDAY();
        if (birthday != null && birthday.after(today)) {
            birthday = null;
            ServiceLogger.LOGGER.warning("Invalid birthday update requested: Birthday is after today.");
        }
        Date deathday = requestModel.getDEATHDAY();
        if (deathday != null && deathday.after(today)) {
            deathday = null;
            ServiceLogger.LOGGER.warning("Invalid deathday update requested: Deathday is after today.");
        }
        String biography = requestModel.getBIOGRAPHY();
        String birthplace = requestModel.getBIRTHPLACE();
        Float popularity = requestModel.getPOPULARITY();
        String profile_path = requestModel.getPROFILE_PATH();


        String INSERT = "\nINSERT INTO person(name";
        String VALUES = "VALUES (?";

        if (person_id != null) {
            INSERT += ", person_id";
            VALUES += String.format(", %d", person_id);
        }
        if (gender_id != null) {
            INSERT += ", gender_id";
            VALUES += String.format(", %d", gender_id);
        }
        if (birthday != null) {
            INSERT += ", birthday";
            VALUES += String.format(", '%s'", birthday.toInstant().toString().split("T")[0]);
        }
        if (deathday != null) {
            INSERT += ", deathday";
            VALUES += String.format(", '%s'", deathday.toInstant().toString().split("T")[0]);
        }
        if (biography != null) {
            INSERT += ", biography";
            VALUES += String.format(", '%s'", biography);
        }
        if (birthplace != null) {
            INSERT += ", birthplace";
            VALUES += String.format(", '%s'", birthplace);
        }
        if (popularity != null) {
            INSERT += ", popularity";
            VALUES += String.format(", %f", popularity);
        }
        if (profile_path != null) {
            INSERT += ", profile_path";
            VALUES += String.format(", '%s'", profile_path);
        }

        INSERT += ")\n";
        VALUES += ");";

        String query = INSERT + VALUES;

        PreparedStatement ps = null;
        try {
            ps = MoviesService.getCon().prepareStatement(query);
            ps.setString(1, name);
        } catch (SQLException e) {
            e.printStackTrace();
            ServiceLogger.LOGGER.warning("Unable to build query to add a new person.");
        }
        return ps;
    }

    public static PreparedStatement buildPersonUpdate(PersonUpdateRequestModel requestModel) {
        String UPDATE = "\nUPDATE person\n";
        String SET = "SET person_id = ?, name = ?";
        String WHERE = "WHERE person_id = ?";

        Integer person_id = requestModel.getPERSON_ID();
        String name = requestModel.getNAME();
        Integer gender_id = requestModel.getGENDER_ID();
        Date birthday = requestModel.getBIRTHDAY();
        Date deathday = requestModel.getDEATHDAY();
        String biography = requestModel.getBIOGRAPHY();
        String birthplace = requestModel.getBIRTHPLACE();
        Float popularity = requestModel.getPOPULARITY();
        String profile_path = requestModel.getPROFILE_PATH();

        if (gender_id != null) {
            SET += String.format(", gender_id = %d", gender_id);
        }
        if (birthday != null) { // TODO: Check if using this right on SQL table
            Date today = Calendar.getInstance().getTime();
            if (birthday.after(today)) {
                ServiceLogger.LOGGER.warning("Invalid birthday update requested: Birthday is after today.");
                birthday = null;
            }
            else {
                SET += String.format(", birthday = '%s'", birthday.toInstant().toString().split("T")[0]);
            }
        }
        if (deathday != null) {
            Date today = Calendar.getInstance().getTime();
            if (deathday.after(today)) {
                ServiceLogger.LOGGER.warning("Invalid deathday update requested: Deathday is after today.");
                deathday = null;
            }
            else {
                SET += String.format(", deathday  '%s'", deathday.toInstant().toString().split("T")[0]);
            }
        }
        if (biography != null) {
            SET += String.format(", biography = %s", biography);
        }
        if (birthplace != null) {
            SET += String.format(", birthplace = %s", birthplace);
        }
        if (popularity != null) {
            SET += String.format(", popularity = %f", popularity);
        }
        if (profile_path != null) {
            SET += String.format(", profile_path = %s", profile_path);
        }

        SET += "\n";
        WHERE += ";";

        String query = UPDATE + SET + WHERE;

        PreparedStatement ps = null;
        try {
            ps = MoviesService.getCon().prepareStatement(query);
            ps.setInt(1, person_id);
            ps.setString(2, name);
            ps.setInt(3, person_id);
        }
        catch (SQLException e) {
            e.printStackTrace();
            ServiceLogger.LOGGER.warning("Unable to build update to a person entry.");
        }
        return ps;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public int sendPersonAddUpdate(PersonAddRequestModel requestModel) {
        int affected_people = 0;
        PreparedStatement ps = buildPersonAddUpdate(requestModel);
        try {
            ServiceLogger.LOGGER.info("Trying update on a person: "+ps.toString());
            affected_people = ps.executeUpdate();
            ServiceLogger.LOGGER.info("Person information updated.");
        }
        catch (SQLException e) {
            e.printStackTrace();
            ServiceLogger.LOGGER.warning("Update failed: Unable to update a person record.");
        }
        return affected_people;
    }

    public int sendPersonUpdate(PersonUpdateRequestModel requestModel) {
        int affected_people = 0;
        PreparedStatement ps = buildPersonUpdate(requestModel);
        try {
            ServiceLogger.LOGGER.info("Trying update on a person: "+ps.toString());
            affected_people = ps.executeUpdate();
            ServiceLogger.LOGGER.info("Person information updated.");
        }
        catch (SQLException e) {
            e.printStackTrace();
            ServiceLogger.LOGGER.warning("Update failed: Unable to update a person record.");
        }
        return affected_people;
    }

    public static ResultSet sendPersonQuery(String name, Integer person_id) {
        ResultSet rs = null;
        String query = buildPersonQuery(name, person_id);
        try {
            PreparedStatement ps = MoviesService.getCon().prepareStatement(query);
            int param_count = 0;
            if (name != null) {
                ps.setString(++param_count, name);
            }
            if (person_id != null) {
                ps.setInt(++param_count,person_id);
            }
            rs = ps.executeQuery();
        }
        catch (SQLException SQLE) {
            SQLE.printStackTrace();
            ServiceLogger.LOGGER.warning("Query failed: Unable to retrieve the person record.");
        }
        return rs;
    }

}
