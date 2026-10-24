
package com.scc.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {

    public void initializeDatabase() {

        System.out.println(
                "DATABASE LOCATION: " +
                        new java.io.File("makeupsystemdatabase.db").getAbsolutePath()
        );

        String[] createTables = {
                """
            CREATE TABLE IF NOT EXISTS Users (
                user_id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                username TEXT NOT NULL UNIQUE,
                password TEXT NOT NULL,
                role TEXT NOT NULL,
                status TEXT NOT NULL DEFAULT 'PENDING'
            )
            """,

                """
            CREATE TABLE IF NOT EXISTS Makeup_Artists (
                artist_id INTEGER PRIMARY KEY AUTOINCREMENT,
                artist_name TEXT NOT NULL,
                specialization TEXT,
                contact_number TEXT,
                availability TEXT DEFAULT 'AVAILABLE'
            )
            """,

                """
            CREATE TABLE IF NOT EXISTS Services (
                service_id INTEGER PRIMARY KEY AUTOINCREMENT,
                service_name TEXT NOT NULL,
                price REAL NOT NULL,
                description TEXT,
                duration INTEGER DEFAULT 60,
                status TEXT DEFAULT 'ACTIVE'
            )
            """,

                """
            CREATE TABLE IF NOT EXISTS Appointments (
                appointment_id INTEGER PRIMARY KEY AUTOINCREMENT,
                customer_id INTEGER NOT NULL,
                artist_id INTEGER NOT NULL,
                service_id INTEGER NOT NULL,
                appointment_date TEXT NOT NULL,
                appointment_time TEXT,
                status TEXT NOT NULL DEFAULT 'PENDING',
                FOREIGN KEY (customer_id) REFERENCES Users(user_id),
                FOREIGN KEY (artist_id) REFERENCES Users(user_id),
                FOREIGN KEY (service_id) REFERENCES Services(service_id)
            )
            """,

                """
            CREATE TABLE IF NOT EXISTS Activity_Logs (
                log_id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id INTEGER,
                action TEXT NOT NULL,
                timestamp TEXT DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (user_id) REFERENCES Users(user_id)
            )
            """
        };

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            for (String sql : createTables) {
                stmt.execute(sql);
            }

            System.out.println("Database initialized successfully.");

        } catch (SQLException e) {
            System.err.println(
                    "Database initialization failed: " + e.getMessage()
            );
            throw new IllegalStateException(
                    "Unable to initialize the Glamoura database.", e
            );
        }
    }
}
