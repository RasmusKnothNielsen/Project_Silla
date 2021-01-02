package dk.dmi.silla.model;

import dk.dmi.silla.config.Config;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import java.util.*;

public class SynopMatrix {

    private final Hashtable<String, Synop> synopHashtable = new Hashtable<>();

    public SynopMatrix() {
    }

    public void addMeasurements(List<Measurement> measurementList) {
        for (Measurement measurement : measurementList) {
            addMeasurement(measurement);
        }
    }

    public void addMeasurement(Measurement measurement) {
        String statidtimeobs = measurement.getKey();
        Synop synop = synopHashtable.get(statidtimeobs);
        if (synop == null) {
            synop = new Synop(measurement.getStatid(), measurement.getTimeobs());
            synopHashtable.put(statidtimeobs, synop);
        }
        synop.addMeasurement(measurement);
    }

    public JsonArray toJson() {
        JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();

        List<String> synopKeyList = Collections.list(synopHashtable.keys());
        Collections.sort(synopKeyList);

        for (String synopKey : synopKeyList) {
            Synop synop = synopHashtable.get(synopKey);
            jsonArrayBuilder.add(synop.toJson(Config.getParamConfig().getParameters()));
        }
        return jsonArrayBuilder.build();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;

        SynopMatrix otherSynopMatrix = (SynopMatrix) obj;
        return this.toJson().equals(otherSynopMatrix.toJson());
    }

    @Override
    public int hashCode() {
        return synopHashtable.hashCode();
    }

    @Override
    public String toString() {
        return toJson().toString();
    }
}
