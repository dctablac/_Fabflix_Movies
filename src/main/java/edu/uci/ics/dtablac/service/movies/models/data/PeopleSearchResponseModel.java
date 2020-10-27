package edu.uci.ics.dtablac.service.movies.models.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.dtablac.service.movies.models.base.ResponseModel;

public class PeopleSearchResponseModel extends ResponseModel {
    @JsonProperty(value = "people", required = true)
    private Object[] PEOPLE; // PersonModel[]

    @JsonCreator
    public PeopleSearchResponseModel(@JsonProperty(value = "resultCode", required = true) Integer newRESULTCODE,
                                     @JsonProperty(value = "message", required = true) String newMESSAGE,
                                     @JsonProperty(value = "people", required = true) Object[] newPEOPLE) {
        super(newRESULTCODE, newMESSAGE);
        this.PEOPLE = newPEOPLE;
    }

    @JsonProperty(value = "people")
    public Object[] getPEOPLE() { return PEOPLE; }
}
