package dk.dmi.silla.service;

import dk.dmi.silla.model.Station;
import dk.dmi.silla.config.Config;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import java.util.List;

public class StationService {

    public JsonArray getStatIdList() {
        JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
        List<Integer> statIdList = Config.getStatIdList();
        for(Integer statId : statIdList) {
            jsonArrayBuilder.add(statId);
        }
        return jsonArrayBuilder.build();
    }

    public JsonArray getStationList() {
        JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
        List<Station> stationList = Config.getStationList();
        for(Station station : stationList) {
            jsonArrayBuilder.add(station.toJson());
        }
        return jsonArrayBuilder.build();
    }
}
