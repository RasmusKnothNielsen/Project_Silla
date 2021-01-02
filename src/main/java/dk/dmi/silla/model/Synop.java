package dk.dmi.silla.model;

import dk.dmi.silla.config.Config;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.time.LocalDateTime;
import java.util.*;

public class Synop {
    private Integer statid;
    private LocalDateTime timeobs;
    private final Hashtable<Integer, Measurement> measurementHashtable = new Hashtable<>();

    public Synop(Integer statid, LocalDateTime time) {
        this.statid = statid;
        this.timeobs = time;
    }

    public void addMeasurement(Measurement measurement) {
        measurementHashtable.put(measurement.getElemNo(), measurement);
    }

    public JsonObject toJson() {
        List<Integer> elem_noList = Collections.list(measurementHashtable.keys());
        return toJson(elem_noList);
    }

    public JsonObject toJson(List<Integer> elem_noList) {
        JsonObjectBuilder jsonBuilder = Json.createObjectBuilder()
                .add("statid", statid)
                .add("timeobs", timeobs.format(Config.jsonFormatter));
        for (Integer elem_no : elem_noList) {
            Measurement m = measurementHashtable.get(elem_no);
            if (m != null) {
                jsonBuilder.add(elem_no.toString(), m.toJson());
            }
        }

        return jsonBuilder.build();
    }

    public Integer getStatid() {
        return statid;
    }

    public void setStatid(Integer statid) {
        this.statid = statid;
    }

    public LocalDateTime getTimeobs() {
        return timeobs;
    }

    public void setTimeobs(LocalDateTime timeobs) {
        this.timeobs = timeobs;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;

        Synop otherSynop = (Synop) obj;
        return this.toJson().equals(otherSynop.toJson());
    }

    @Override
    public int hashCode() {
        return statid ^ timeobs.hashCode();
    }

    @Override
    public String toString() {
        return toJson().toString();
    }

}
