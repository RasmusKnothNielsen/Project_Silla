package dk.dmi.silla.config;

import dk.dmi.silla.model.Station;
import dk.dmi.silla.repository.DBConnections;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class StationConfig {

    private List<Station> stationList;
    private List<Integer> statIdList;

    public List<Integer> getStatIdList() {
        if (statIdList == null) statIdList = createStatIdList();
        return statIdList;
    }

    public List<Station> getStationList() {
        if (stationList == null) stationList = createStationList();
        return stationList;
    }

    public Station getStation(Integer statId) {
        if (statIdList == null) statIdList = createStatIdList();
        for (int i = 0; i < statIdList.size(); i++) {
            if (statIdList.get(i).equals(statId)) return stationList.get(i);
        }
        throw new IndexOutOfBoundsException("No such station");
    }

    private List<Integer> createStatIdList() {
        if (stationList == null) stationList = createStationList();
        statIdList = new ArrayList<>();
        for (Station station : stationList) {
            statIdList.add(station.getStatid());
        }
        return statIdList;
    }

    private List<Station> createStationList() {
        stationList = new ArrayList<>();
        try (Connection connection = DBConnections.getReadConnection()) {
            String query = "SELECT * FROM statcat.view_qc_na_stations;";
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(query);
            while (rs.next()) {
                Station station = new Station(rs.getInt("statid"),
                        rs.getString("name"),
                        rs.getString("country"),
                        rs.getString("report_type"),
                        rs.getString("zone"));
                stationList.add(station);
            }
            rs.close();
            st.close();
        } catch (SQLException e) {
            System.err.println("ERROR reading stations: " + e.getMessage());
        }
        return stationList;
    }

}
