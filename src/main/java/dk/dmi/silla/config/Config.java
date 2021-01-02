package dk.dmi.silla.config;

import dk.dmi.silla.model.Parameter;

import dk.dmi.silla.model.Station;
import dk.dmi.silla.repository.DBConnections;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

public class Config {
    public static final String APPNAME = "silla";
    public static final Boolean DEBUG = true;
    public static final Boolean TIMING_MODE = true;
    public static final List<String> productionServers = Arrays.asList(
            "pippin",
            "minions",
            "shelob",
            "balrog",
            "evilminions",
            "azog",
            "bolg",
            "glklimadata01",
            "angakoq");

    public static final String configFileName = "config.properties";
    public static final String[] propertyNames = {"host", "port", "database", "username", "password"};

    public static final String dbReadHost = "";
    public static final String dbReadPort = "";
    public static final String dbReadDatabase = "";
    public static final String dbReadUsername = "";
    public static final String dbReadPassword = "";

    public static final String dbWriteHost = "";
    public static final String dbWritePort = "";
    public static final String dbWriteDatabase = "";
    public static final String dbWriteUsername = "";
    public static final String dbWritePassword = "";

    // Return the hour the meteorological day starts as in ]hour - hour]
    public static Integer meteoHour(String country) {
        switch(country){
            case "Grønland":
                return 6;
            case "Færøerne":
                return 0;
            default:
                return null;
        }
    }

    public static Station getStation(Integer statid) {
        return stationConfig.getStation(statid);
    }

    private static ParamConfig paramConfig;

    private static ParamConfig loadParamInfo() {
        ParamConfig newParamConfig = new ParamConfig();
        try (Connection connection = DBConnections.getReadConnection()) {
            newParamConfig.add(createParamMetaData(connection, 101, "basis_hourly_na_temperature_mean"));
            newParamConfig.add(createParamMetaData(connection, 112, "basis_hourly_na_temperature_max"));
            newParamConfig.add(createParamMetaData(connection, 113, "basis_hourly_na_temperature_max12h"));
            newParamConfig.add(createParamMetaData(connection, 122, "basis_hourly_na_temperature_min"));
            newParamConfig.add(createParamMetaData(connection, 123, "basis_hourly_na_temperature_min12h"));
            newParamConfig.add(createParamMetaData(connection, 201, "basis_hourly_na_relative_humidity_mean"));
            newParamConfig.add(createParamMetaData(connection, 301, "basis_hourly_na_wind_speed_mean"));
            newParamConfig.add(createParamMetaData(connection, 305, "basis_hourly_na_wind_speed_3sec_max"));
            newParamConfig.add(createParamMetaData(connection, 365, "basis_hourly_na_wind_direction_mean_10min"));
            newParamConfig.add(createParamMetaData(connection, 371, "basis_hourly_na_wind_direction_mean"));
            newParamConfig.add(createParamMetaData(connection, 401, "basis_hourly_na_pressure_mean"));
            newParamConfig.add(createParamMetaData(connection, 504, "basis_hourly_na_sunshine_sum"));
            newParamConfig.add(createParamMetaData(connection, 550, "basis_hourly_na_radiation_mean"));
            newParamConfig.add(createParamMetaData(connection, 601, "basis_hourly_na_precipitation_sum"));
            newParamConfig.add(createParamMetaData(connection, 603, "basis_hourly_na_precipitation_sum12h"));
            newParamConfig.add(createParamMetaData(connection, 609, "basis_hourly_na_precipitation_sum24h"));
            newParamConfig.add(createParamMetaData(connection, 801, "basis_hourly_na_cloud_cover"));
        } catch (SQLException e) {
            //NOP
        }
        return newParamConfig;
    }

    private static Parameter createParamMetaData(Connection connection, Integer elemNo, String table) {
        Integer paramDecimals = 0;
        String paramDescription = "";
        String query = "SELECT * FROM metadata.elem_no_descriptions WHERE derived_code = 'bhna' and elem_no = ? ;";
        try {
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, elemNo);
            ResultSet rs = ps.executeQuery();
            if (rs.next()){
                paramDecimals = rs.getInt("decimals");
                paramDescription = rs.getString("descrip");
            }
        } catch (SQLException e) {
            //NOP
        }

        return new Parameter(elemNo, paramDecimals, table, paramDescription);
    }

    public static ParamConfig getParamConfig() {
        if (paramConfig == null) paramConfig = loadParamInfo();
        return paramConfig;
    }

    public static ParamConfig refreshParamInfo() {
        paramConfig = null;
        return getParamConfig();
    }

    public static String getDbTableName(Integer elemNo) {
        return getParamConfig().getDbTableName(elemNo);
    }

    public static final DateTimeFormatter jsonFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    public static final DateTimeFormatter resultSetFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ssX");

    private static final StationConfig stationConfig = new StationConfig();

    public static List<Integer> getStatIdList() {
        return stationConfig.getStatIdList();
    }

    public static List<Station> getStationList() {
        return stationConfig.getStationList();
    }

    public static List<Parameter> getParameterList() {
        if (paramConfig == null) paramConfig = loadParamInfo();
        return paramConfig.getParameterList();
    }
}
