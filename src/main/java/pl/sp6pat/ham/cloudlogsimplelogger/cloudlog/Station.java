package pl.sp6pat.ham.cloudlogsimplelogger.cloudlog;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.ToString;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Station {

    @JsonProperty("station_id")
    private String stationId;

    @JsonProperty("station_profile_name")
    private String stationProfileName;

    @JsonProperty("station_gridsquare")
    private String stationGridsquare;

    @JsonProperty("station_callsign")
    private String stationCallsign;

    @JsonProperty("station_active")
    private Integer stationActive;

    @Override
    public String toString () {
        return stationCallsign + " " + stationProfileName;
    }

}
