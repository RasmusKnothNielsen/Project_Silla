package dk.dmi.silla.repository;

import dk.dmi.silla.config.Config;
import dk.dmi.silla.model.Measurement;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class SynopRepo {

    private static final String SQL_GET_ALL_HOURLY_TIMEOBS = "SELECT * FROM view_basis_hourly_na_timeobs where statid = any(?) and timeobs between ? and ?;";

    public List<Measurement> getMeasurementList(List<Integer> stations, LocalDateTime fromTime, LocalDateTime toTime) throws SQLException {
        Connection connection = DBConnections.getReadConnection();

        //Converting from List to SQL Array
        Integer[] stationsArray = new Integer[stations.size()];
        stations.toArray(stationsArray);
        Array sqlStationArray = connection.createArrayOf("integer", stationsArray);

        OffsetDateTime offsetFromTime = OffsetDateTime.of(fromTime, ZoneOffset.UTC);
        OffsetDateTime offsetToTime = OffsetDateTime.of(toTime, ZoneOffset.UTC);

        PreparedStatement st = connection.prepareStatement(SQL_GET_ALL_HOURLY_TIMEOBS);
        st.setArray(1, sqlStationArray);
        st.setObject(2, offsetFromTime);
        st.setObject(3, offsetToTime);

        if (Config.DEBUG) System.out.println("Query:" + st.toString());

        ResultSet rs = st.executeQuery();
        List<Measurement> measurementList = getMeasurementList(rs);
        rs.close();
        connection.close();
        return measurementList;
    }

    public List<Measurement> getMeasurementList(String query) throws SQLException {
        Connection connection;
        Statement statement;
        ResultSet rs;

        if (Config.TIMING_MODE) {
            long start = System.nanoTime();

            connection = DBConnections.getReadConnection();
            statement = connection.createStatement();
            rs = statement.executeQuery(query);

            long end = System.nanoTime();
            System.out.printf("getMeasurementList took: %d ms.%n", TimeUnit.NANOSECONDS.toMillis(end - start));
        } else {
            connection = DBConnections.getReadConnection();
            statement = connection.createStatement();
            rs = statement.executeQuery(query);
        }

        List<Measurement> measurementList = getMeasurementList(rs);
        rs.close();
        connection.close();
        return measurementList;
    }

    private List<Measurement> getMeasurementList(ResultSet resultSet) throws SQLException {
        List<Measurement> measurementList = new LinkedList<>();
        while (resultSet.next()) {
            measurementList.add(new Measurement(resultSet));
        }
        return measurementList;
    }

}
