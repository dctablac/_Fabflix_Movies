package edu.uci.ics.dtablac.service.movies.models.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.dtablac.service.movies.models.base.ResponseModel;

public class ThumbnailResponseModel extends ResponseModel {
    @JsonProperty(value = "thumbnails", required = true)
    private Object[] THUMBNAILS;

    @JsonCreator
    public ThumbnailResponseModel(@JsonProperty(value = "resultCode", required = true) Integer newRESULTCODE,
                                  @JsonProperty(value = "message", required = true) String newMessage,
                                  @JsonProperty(value = "thumbnails", required = true) Object[] newTHUMBNAILS) {
        super(newRESULTCODE, newMessage);
        this.THUMBNAILS = newTHUMBNAILS;
    }

    @JsonProperty(value = "thumbnails")
    public Object[] getTHUMBNAILS() { return THUMBNAILS; }
}
