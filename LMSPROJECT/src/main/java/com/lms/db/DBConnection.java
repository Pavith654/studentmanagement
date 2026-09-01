package com.lms.db;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Central place that opens JDBC connections to MySQL.
 * Reads src/main/resources/application.properties for credentials.
 */
public class DBConnection {

    private static final Properties props = new Properties();

    static {
        try (InputStream in = DBConnection.class.getClassLoader()
                .getResourceAsStream("application.properties")) {
            if (in != null) {
                props.load(in);
            } else {
                throw new RuntimeException("application.properties not found on classpath");
            }
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialise DB configuration", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        String url = props.getProperty("db.url");
        String user = props.getProperty("db.username");
        String pass = props.getProperty("db.password");
        return DriverManager.getConnection(url, user, pass);
    }
}
