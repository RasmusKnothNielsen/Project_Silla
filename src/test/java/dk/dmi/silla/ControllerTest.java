package dk.dmi.silla;

import dk.dmi.silla.service.MeasurementService;
import dk.dmi.silla.service.SynopService;
import org.json.JSONException;
import org.junit.Test;
import org.mockito.Mockito;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class ControllerTest {

    @Test
    public void testGetSynopsMatrix() throws SQLException {
        SynopService synopService = Mockito.mock(SynopService.class);
        Controller controller = new Controller(synopService, null, null, null);

        List<Integer> stations = Arrays.asList(37, 41);
        String fromTimestamp = "somestring";
        String toTimestamp = "also some string";

        controller.getSynopsMatrix(stations, fromTimestamp, toTimestamp, null);
        verify(synopService, times(1)).getSynopMatrix(stations, fromTimestamp, toTimestamp);
    }

    @Test
    public void testExcludeMeasurements() throws SQLException, JSONException {
        MeasurementService measurementService = Mockito.mock(MeasurementService.class);
        Controller controller = new Controller(null, measurementService, null, null);

        String data = "somestring";

        String result = controller.excludeMeasurements(data);
        verify(measurementService, times(1)).excludeMeasurements(data);
        assertEquals("{\"status\": \"ok\"}", result);
    }

    @Test
    public void testQCMeasurements() throws SQLException, JSONException {
        MeasurementService measurementService = Mockito.mock(MeasurementService.class);
        Controller controller = new Controller(null, measurementService, null, null);

        String data = "somestring";

        String result = controller.qcMeasurements(data);
        verify(measurementService, times(1)).qcMeasurements(data);
        assertEquals("{\"status\": \"ok\"}", result);
    }

}
