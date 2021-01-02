package dk.dmi.silla;

import dk.dmi.silla.model.Measurement;
import dk.dmi.silla.repository.MeasurementRepo;
import dk.dmi.silla.service.MeasurementService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.mockito.Mockito;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class MeasurementServiceTest {

    @Test
    public void exclude() throws SQLException, JSONException {
        // Mocking
        MeasurementRepo measurementRepo = Mockito.mock(MeasurementRepo.class);
        MeasurementService measurementService = new MeasurementService(measurementRepo);

        // Make input
        JsonObject jsonObjectInput = makeJsonMeasurement(10L, 10, 7.3, "2020-10-10 00:00", 1100, 422000);
        JsonArray jsonArrayInput = Json.createArrayBuilder()
                .add(jsonObjectInput)
                .build();
        String jsonStringInput = jsonArrayInput.toString();

        // Make expected output
        List<Measurement> measurementList = new ArrayList<>();
        JsonObject jsonObjectOutput = makeJsonMeasurement(10L, 10, 7.3, "2020-10-10 00:00", 1100, 422000);
        Measurement measurement = new Measurement(jsonObjectOutput);
        measurementList.add(measurement);

        // Run method
        measurementService.excludeMeasurements(jsonStringInput);

        // Test
        verify(measurementRepo, times(1)).setExcludeAndQcFlags(measurementList);
    }

    @Test
    public void qc() throws SQLException, JSONException {
        // Mocking
        MeasurementRepo measurementRepo = Mockito.mock(MeasurementRepo.class);
        MeasurementService measurementService = new MeasurementService(measurementRepo);

        // Make input
        JsonObject jsonObjectInput = makeJsonMeasurement(10L, 10, 7.3, "2020-10-10 00:00", 1100, 422000);
        JsonArray jsonArrayInput = Json.createArrayBuilder()
                .add(jsonObjectInput)
                .build();
        String jsonStringInput = jsonArrayInput.toString();

        // Make expected output
        List<Measurement> measurementList = new ArrayList<>();
        JsonObject jsonObjectOutput = makeJsonMeasurement(10L, 10, 7.3, "2020-10-10 00:00", 1100, 422000);
        Measurement measurement = new Measurement(jsonObjectOutput);
        measurementList.add(measurement);

        // Run method
        measurementService.qcMeasurements(jsonStringInput);

        // Test
        verify(measurementRepo, times(1)).setQcFlag(measurementList);
    }


    private JsonObject makeJsonMeasurement(Long interpolation_id,
                                           int elem_no,
                                           double elem_val,
                                           String timeobs,
                                           long label,
                                           int statid) {
        return Json.createObjectBuilder()
                .add("interpolation_id", interpolation_id)
                .add("elem_no", elem_no)
                .add("elem_val", elem_val)
                .add("timeobs", timeobs)
                .add("label", label)
                .add("statid", statid).build();
    }

}
