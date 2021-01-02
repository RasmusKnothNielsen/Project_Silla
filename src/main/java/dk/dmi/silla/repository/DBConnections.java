package dk.dmi.silla.repository;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import dk.dmi.silla.config.Config;
import org.postgresql.Driver;

public class DBConnections {

    private static Properties readProperties;
    private static Properties writeProperties;

    private static boolean driverIsRegistered = false;

    private DBConnections() {
    }

    public static Connection getReadConnection() throws SQLException {
        if (readProperties == null) {
            readProperties = new Properties();
            readProperties.setProperty("host", Config.dbReadHost);
            readProperties.setProperty("port", Config.dbReadPort);
            readProperties.setProperty("database", Config.dbReadDatabase);
            readProperties.setProperty("username", Config.dbReadUsername);
            readProperties.setProperty("password", Config.dbReadPassword);

            if (isProductionServer()) {
                overridePropertiesWithConfigFile(readProperties, "read");
            }
        }

        String url = makeConnectionURL(readProperties);
        Properties connectionProperties = makeConnectionProperties(readProperties);

        return connectToDatabase(url, connectionProperties);
    }

    public static Connection getWriteConnection() throws SQLException {
        if (writeProperties == null) {
            writeProperties = new Properties();
            writeProperties.setProperty("host", Config.dbWriteHost);
            writeProperties.setProperty("port", Config.dbWritePort);
            writeProperties.setProperty("database", Config.dbWriteDatabase);
            writeProperties.setProperty("username", Config.dbWriteUsername);
            writeProperties.setProperty("password", Config.dbWritePassword);

            if (isProductionServer()) {
                overridePropertiesWithConfigFile(writeProperties, "write");
            }
        }

        String url = makeConnectionURL(writeProperties);
        Properties connectionProperties = makeConnectionProperties(writeProperties);

        return connectToDatabase(url, connectionProperties);
    }

    private static Properties makeConnectionProperties(Properties properties) {
        Properties connectionProperties = new Properties();
        connectionProperties.setProperty("user", properties.getProperty("username"));
        connectionProperties.setProperty("password", properties.getProperty("password"));
        connectionProperties.setProperty("charset", "UTF8");
        connectionProperties.setProperty("ApplicationName", Config.APPNAME);
        return connectionProperties;
    }

    private static void registerDriver() throws SQLException {
        try {
            DriverManager.registerDriver(new Driver());
            driverIsRegistered = true;
        } catch (SQLException e) {
            System.err.println("Failed to register PostgreSQL database driver.");
            throw e;
        }
    }

    private static Connection connectToDatabase(String url, Properties properties) throws SQLException {

        if (!driverIsRegistered) registerDriver();

        try {
            return DriverManager.getConnection(url, properties);
        } catch (SQLException e) {
            System.err.println("Failed to connect to: " + url);
            throw e;
        }
    }

    private static void overridePropertiesWithConfigFile(Properties properties, String prefix) {
        String filename = "/opt/" + Config.APPNAME + "/" + Config.configFileName;

        Properties propertyFile = getPropertiesFromFile(filename);

        for (String name : Config.propertyNames) {
            String option = propertyFile.getProperty(prefix + name);
            if (option != null) properties.setProperty(name, option);
        }
    }

    private static Properties getPropertiesFromFile(String filename) {
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(new File(filename)));
        } catch (FileNotFoundException e) {
            System.err.println("Could not find config file: " + filename);
        } catch (IOException e) {
            System.err.println("Could not read from config file: " + filename);
        }
        return properties;
    }

    private static String makeConnectionURL(Properties properties) {
        return "jdbc:postgresql://" + properties.getProperty("host")
                + ":" + properties.getProperty("port")
                + "/" + properties.getProperty("database");
    }

    private static boolean isProductionServer() {
        String pcName = getPCName();
        return (pcName != null) && Config.productionServers.contains(pcName);
    }

    private static String getPCName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            System.err.println("Failed to get hostname of local machine. Hostname is needed to run in production mode.");
            return null;
        }
    }
}
