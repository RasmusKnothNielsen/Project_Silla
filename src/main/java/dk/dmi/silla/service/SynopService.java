package dk.dmi.silla.service;

import dk.dmi.silla.config.Config;
import dk.dmi.silla.model.Measurement;
import dk.dmi.silla.model.SynopMatrix;
import dk.dmi.silla.repository.SynopRepo;

import javax.json.JsonArray;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class SynopService {

    SynopRepo synopRepo = new SynopRepo();

    private final String TIME_FORMAT = "yyy-MM-dd HH:mm:ss";
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(TIME_FORMAT);

    public JsonArray getSynopMatrix(List<Integer> stations, String fromTimestamp, String toTimestamp) throws SQLException {
        LocalDateTime fromTime = LocalDateTime.parse(fromTimestamp, formatter);
        LocalDateTime toTime = LocalDateTime.parse(toTimestamp, formatter);

        List<Measurement> measurementList;

        try {
            measurementList = synopRepo.getMeasurementList(stations, fromTime, toTime);
        } catch (SQLException e) {
            if (Config.DEBUG) e.printStackTrace();
            throw e;
        }

        SynopMatrix synopMatrix = new SynopMatrix();
        synopMatrix.addMeasurements(measurementList);
        return synopMatrix.toJson();
    }

    // test query
    // select * from basis_hourly_na_timeobs where statid=422000 and timeobs between '2020-10-10 00:00:00+00' and '2020-10-11 23:00:00+00';
    // select%20*%20from%20view_basis_hourly_na_timeobs%20where%20statid=422000%20and%20timeobs%20between%20'2020-10-10 00:00:00%2b00'%20and%20'2020-10-11 00:00:00%2b00';
    public JsonArray getSynopMatrix(String sqlQuery) throws SQLException {
        List<Measurement> measurementList;

        try {
            measurementList = synopRepo.getMeasurementList(sqlQuery);
        } catch (SQLException e) {
            if (Config.DEBUG) System.err.println("ERROR: "+e.getMessage()+" "+sqlQuery);

            measurementList = new ArrayList<>();
        }

        SynopMatrix synopMatrix = new SynopMatrix();
        synopMatrix.addMeasurements(measurementList);
        return synopMatrix.toJson();
    }

}
