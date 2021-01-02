package dk.dmi.silla;

import dk.dmi.silla.config.Config;
import dk.dmi.silla.service.MeasurementService;
import dk.dmi.silla.service.ParameterService;
import dk.dmi.silla.service.StationService;
import dk.dmi.silla.service.SynopService;

import javax.json.JsonArray;
import javax.json.JsonException;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.sql.SQLException;
import java.util.List;

@Path("/")
public class Controller {

    private final SynopService synopService;
    private final MeasurementService measurementService;
    private final StationService stationService;
    private final ParameterService parameterService;

    public Controller() {
        synopService = new SynopService();
        measurementService = new MeasurementService();
        stationService = new StationService();
        parameterService = new ParameterService();
    }

    // Injection for tests
    public Controller(SynopService synopService,
                      MeasurementService measurementService,
                      StationService stationService,
                      ParameterService parameterService
    ) {
        this.synopService = synopService;
        this.measurementService = measurementService;
        this.stationService = stationService;
        this.parameterService = parameterService;
    }

    @GET
    @Path("/synops")
    @Produces(MediaType.APPLICATION_JSON)
    public JsonArray getSynopsMatrix(
            @QueryParam("stations") List<Integer> stations,
            @QueryParam("from_timestamp") String fromTimestamp,
            @QueryParam("to_timestamp") String toTimestamp,
            @QueryParam("params") List<Integer> parameters) {
        try {
            return synopService.getSynopMatrix(stations, fromTimestamp, toTimestamp);
        } catch (Exception e) {
            if (Config.DEBUG) e.printStackTrace();
            throw new WebApplicationException(500);
        }
    }

    @POST
    @Path("/sql")
    @Consumes("text/plain")
    @Produces(MediaType.APPLICATION_JSON)
    public JsonArray getSQL(String query) {
        System.out.println(query);
        try {
            return synopService.getSynopMatrix(query);
        } catch (Exception e) {
            if (Config.DEBUG) e.printStackTrace();
            System.out.println("MESSAGE: "+e.getMessage());
            throw new WebApplicationException(500);
        }
    }

    @POST
    @Path("/update_labels/exclude")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String excludeMeasurements(String data) {
        try {
            measurementService.excludeMeasurements(data);
            return "{\"status\": \"ok\"}";
        } catch (SQLException e) {
            if (Config.DEBUG) e.printStackTrace();
            throw new WebApplicationException(500);
        } catch (JsonException e) {
            if (Config.DEBUG) e.printStackTrace();
            throw new WebApplicationException(400);
        }
    }

    @POST
    @Path("/update_labels/qc")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String qcMeasurements(String data) {
        try {
            measurementService.qcMeasurements(data);
            return "{\"status\": \"ok\"}";
        } catch (SQLException e) {
            if (Config.DEBUG) e.printStackTrace();
            throw new WebApplicationException(500);
        } catch (JsonException e) {
            if (Config.DEBUG) e.printStackTrace();
            throw new WebApplicationException(400);
        }
    }

    @GET
    @Path("/statids")
    @Produces(MediaType.APPLICATION_JSON)
    public JsonArray getStatIds() {
        try {
            return stationService.getStatIdList();
        } catch (Exception e) {
            if (Config.DEBUG) e.printStackTrace();
            throw new WebApplicationException(500);
        }
    }

    @GET
    @Path("/stations")
    @Produces(MediaType.APPLICATION_JSON)
    public JsonArray getStations() {
        try {
            return stationService.getStationList();
        } catch (Exception e) {
            if (Config.DEBUG) e.printStackTrace();
            throw new WebApplicationException(500);
        }
    }

    @GET
    @Path("/params")
    @Produces(MediaType.APPLICATION_JSON)
    public JsonArray getParameters() {
        try {
            return parameterService.getParameterList();
        } catch (Exception e) {
            if (Config.DEBUG) e.printStackTrace();
            throw new WebApplicationException(500);
        }
    }

}
