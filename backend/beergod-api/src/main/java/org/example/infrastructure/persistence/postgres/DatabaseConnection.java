package org.example.infrastructure.persistence.postgres;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL = "jdbc:postgresql://ep-wandering-art-b4bowga7-pooler.c-6.us-east-2.aws.neon.tech:5432/neondb?sslmode=require";
    private static final String USER = "neondb_owner";
    private static final String PASS = "npg_y09SULcIsqiR";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}