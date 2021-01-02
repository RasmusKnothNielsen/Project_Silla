package dk.dmi.silla.service;

import dk.dmi.silla.model.Measurement;
import dk.dmi.silla.repository.MeasurementRepo;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.StringReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MeasurementService {

    private final MeasurementRepo measurementRepo;

    public MeasurementService() {
        measurementRepo = new MeasurementRepo();
    };

    // For injection
    public MeasurementService(MeasurementRepo measurementRepo) {
        this.measurementRepo = measurementRepo;
    }

    public void excludeMeasurements(String data) throws SQLException {
        List<Measurement> measurementList = getMeasurementList(data);
        measurementRepo.setExcludeAndQcFlags(measurementList);
    }

    public void qcMeasurements(String data) throws SQLException {
        List<Measurement> measurementList = getMeasurementList(data);
        measurementRepo.setQcFlag(measurementList);
    }

    private List<Measurement> getMeasurementList(String jsonString) {
        List<Measurement> measurementList = new ArrayList<>();
        JsonReader jsonReader = Json.createReader(new StringReader(jsonString));
        JsonArray jsonArray = jsonReader.readArray();
        jsonReader.close();

        for (int i = 0; i < jsonArray.size(); i++) {
            JsonObject measurementJson = jsonArray.getJsonObject(i);
            Measurement measurement = new Measurement(measurementJson);
            measurementList.add(measurement);
        }
        return measurementList;
    }
}
