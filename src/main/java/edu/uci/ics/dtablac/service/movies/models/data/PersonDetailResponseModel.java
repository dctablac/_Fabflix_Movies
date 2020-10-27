package edu.uci.ics.dtablac.service.movies.models.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.dtablac.service.movies.models.base.ResponseModel;

public class PersonDetailResponseModel extends ResponseModel {
    @JsonProperty(value = "person", required = true)
    private PersonDetailModel PERSON;

    @JsonCreator
    public PersonDetailResponseModel(@JsonProperty(value = "resultCode", required = true) Integer newRESULTCODE,
                                     @JsonProperty(value = "message", required = true) String newMESSAGE,
                                     @JsonProperty(value = "person", required = true) PersonDetailModel newPERSON) {
        super(newRESULTCODE, newMESSAGE);
        this.PERSON = newPERSON;
    }

    @JsonProperty(value = "person")
    public PersonDetailModel getPerson() { return PERSON; }
}
